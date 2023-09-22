package run.mone.hera.webhook;

import com.xiaomi.youpin.docean.Ioc;
import io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequest;
import io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequestList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import run.mone.hera.webhook.common.FileUtils;
import run.mone.hera.webhook.common.K8sUtilBean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * @author dingtao
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"run.mone.hera.webhook"})
@Slf4j
public class Bootstrap {

    private static K8sUtilBean k8sUtilBean;

    private static KubernetesClient kubernetesClient;

    private static String webhookConfigYaml;

    private static final String HERA_NAMESPACE = "hera-namespace";

    public static void main(String[] args) {
        try {
            Ioc.ins().init("run.mone.docean.plugin", "com.xiaomi.youpin.docean.plugin", "run.mone.hera.webhook");
            k8sUtilBean = Ioc.ins().getBean(K8sUtilBean.class);
            kubernetesClient = Ioc.ins().getBean(KubernetesClient.class);
            createHeraEnvWebhook();
            SpringApplication.run(Bootstrap.class, args);
            Runtime.getRuntime().addShutdownHook(new Thread(()->{deleteWebHookConfig();}));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.exit(-1);
        }

    }

    private static void deleteWebHookConfig() {
        k8sUtilBean.applyYaml(webhookConfigYaml, HERA_NAMESPACE, "delete");
    }

    public static void createHeraEnvWebhook() {
        try {
            String app = "hera-webhook-server";
            String csrName = app + "." + HERA_NAMESPACE + ".svc";
            String dir = "/tmp/hera-webhook-tls/";
            /**
             * This is the encryption password for generating the p12 file.
             * If you need to modify it, remember to also modify the value of 'server.ssl.key-store-password' in the 'application.properties' file of the 'hera-webhook-server' project.
             */
            String defaultP12Pwd = "mone";
            String csrShellFilePath = "/tmp/hera-webhook-tls-sh/generate_csr_by_openssl.sh";
            // generate csr file, and get csr base64 string
            String csrArgs = buildShellArgs(app, HERA_NAMESPACE, dir, csrName);
            Process process = callScript(csrShellFilePath, csrArgs);
            if (process == null) {
                log.error("generate SSL file error!!");
                return;
            }
            String csrBase64 = getCsrBase64(process);
            if(StringUtils.isEmpty(csrBase64)){
                log.error("get csr base64 string is empty");
                return;
            }
            // create k8s certificate, approve is and get certificate from k8s
            String certificate = getCertificate(csrName, csrBase64);
            // generate .pem and .p12 file, to be used by webhook-server
            String pemShellFilePath = "/tmp/hera-webhook-tls-sh/generate_pem_p12_by_openssl.sh";
            String pemArgs = buildShellArgs(app, dir, certificate, defaultP12Pwd);
            callScript(pemShellFilePath, pemArgs);
            // load webhook config
            String webhookConfigYaml = FileUtils.readResourceFile("/hera_init/webhook/hera_webhook_config.yaml");
            webhookConfigYaml = webhookConfigYaml.replace("${webhook_caBundle}", "'" + certificate + "'");
            Bootstrap.webhookConfigYaml = webhookConfigYaml;
            k8sUtilBean.applyYaml(webhookConfigYaml, HERA_NAMESPACE, "add");
        } catch (Throwable t) {
            log.error("create hera env webhook error : ", t);
        }
    }

    private static Process callScript(String script, String args) {
        try {
            String cmd = "sh " + script + " " + args;
            log.info("callScript comand : " + cmd);
            return Runtime.getRuntime().exec(cmd, null, null);
        } catch (Exception e) {
            throw new RuntimeException("call script error : ", e);
        }
    }

    private static String getCsrBase64(Process process) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = "";
            String csrLogPrefix = "csr base64 is :";
            while ((line = input.readLine()) != null) {
                log.info(line);
                if (line.startsWith(csrLogPrefix)) {
                    return line.substring(csrLogPrefix.length());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("call script error : ", e);
        }
        return null;
    }

    private static String getCertificate(String csrName, String csrBase64) {
        // delete existing csr
        CertificateSigningRequestList list = kubernetesClient.certificates().v1().certificateSigningRequests().list();
        if (list != null) {
            for (CertificateSigningRequest csr : list.getItems()) {
                if (csrName.equals(csr.getMetadata().getName())) {
                    kubernetesClient.certificates().v1().certificateSigningRequests().delete(csr);
                }
            }
        }
        // replace yaml, create csr and approve it
        String cstYaml = FileUtils.readResourceFile("/hera_init/webhook/csr/webhook_csr.yaml");
        cstYaml = cstYaml.replace("${CSR_NAME}", csrName).replace("${CSR_BASE64}", csrBase64);
        try {
            k8sUtilBean.applyYaml(cstYaml, null, "add");
            // wait for csr create
            boolean isSuccessCreate = false;
            for (int i = 0; i < 30; i++) {
                io.fabric8.kubernetes.client.dsl.Resource<io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequest> csrResource = kubernetesClient.certificates().v1().certificateSigningRequests().withName(csrName);
                if (csrResource != null && csrResource.get() != null) {
                    isSuccessCreate = true;
                    break;
                }
                TimeUnit.SECONDS.sleep(2);
            }
            if (!isSuccessCreate) {
                throw new RuntimeException("the csr not create success!");
            }
            // approve
            kubernetesClient.certificates().v1().certificateSigningRequests().withName(csrName).approve();
            // get certificate
            // wait for be present in kubernetes
            boolean isSuccessPresent = false;
            String certificate = null;
            for (int i = 0; i < 30; i++) {
                certificate = kubernetesClient.certificates().v1().certificateSigningRequests().withName(csrName).get().getStatus().getCertificate();
                if (StringUtils.isNotEmpty(certificate)) {
                    isSuccessPresent = true;
                    break;
                }
                TimeUnit.SECONDS.sleep(2);
            }
            if (!isSuccessPresent) {
                throw new RuntimeException("the csr not present success!");
            }
            return certificate;
        } catch (Throwable t) {
            throw new RuntimeException("laod yaml error : ", t);
        }
    }

    private static String buildShellArgs(String... args) {
        StringBuilder result = new StringBuilder();
        for (String arg : args) {
            result.append(arg).append(" ");
        }
        return result.toString().trim();
    }

}