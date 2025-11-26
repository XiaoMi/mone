package run.mone.mcp.vue3.service;

import com.xiaomi.youpin.codegen.SpringBootProGen;
import com.xiaomi.youpin.infra.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.HashMap;

@Service
public class Vue3TemplateService {

    private static final Logger log = LoggerFactory.getLogger(Vue3TemplateService.class);

    /**
     * 生成Vue3组件模板
     */
    public Flux<String> generateVue3Component(Map<String, Object> params) {
        // 统一走项目生成逻辑：将 componentName 作为 projectName 传入
        String componentName = (String) params.getOrDefault("componentName", "MyComponent");
        return Flux.defer(() -> {
            Map<String, Object> p = new HashMap<>(params);
            p.putIfAbsent("projectName", componentName);
            return generateVue3Project(p);
        });
    }

    /**
     * 生成Vue3页面模板
     */
    public Flux<String> generateVue3Page(Map<String, Object> params) {
        String pageName = (String) params.getOrDefault("pageName", "HomePage");
        String layout = (String) params.getOrDefault("layout", "default");
        Boolean useTypeScript = (Boolean) params.getOrDefault("useTypeScript", false);
        Boolean usePinia = (Boolean) params.getOrDefault("usePinia", false);
        Boolean useRouter = (Boolean) params.getOrDefault("useRouter", true);

        return Flux.defer(() -> {
            try {
                StringBuilder page = new StringBuilder();
                
                page.append("<template>\n");
                page.append("  <div class=\"page\">\n");
                page.append("    <header>\n");
                page.append("      <h1>").append(pageName).append("</h1>\n");
                page.append("    </header>\n");
                page.append("    <main>\n");
                page.append("      <p>Welcome to ").append(pageName).append("</p>\n");
                page.append("    </main>\n");
                page.append("  </div>\n");
                page.append("</template>\n\n");

                if (useTypeScript) {
                    page.append("<script setup lang=\"ts\">\n");
                } else {
                    page.append("<script setup>\n");
                }

                page.append("import { ref, onMounted } from 'vue'\n");

                if (usePinia) {
                    page.append("import { useStore } from '@/stores'\n");
                }

                if (useRouter) {
                    page.append("import { useRouter } from 'vue-router'\n");
                }

                page.append("\n// 页面数据\n");
                page.append("const pageData = ref({\n");
                page.append("  title: '").append(pageName).append("',\n");
                page.append("  loading: false\n");
                page.append("})\n\n");

                page.append("// 页面方法\n");
                page.append("const handlePageLoad = () => {\n");
                page.append("  pageData.value.loading = true\n");
                page.append("  // 页面加载逻辑\n");
                page.append("  setTimeout(() => {\n");
                page.append("    pageData.value.loading = false\n");
                page.append("  }, 1000)\n");
                page.append("}\n\n");

                page.append("onMounted(() => {\n");
                page.append("  handlePageLoad()\n");
                page.append("})\n");

                page.append("</script>\n\n");

                page.append("<style scoped>\n");
                page.append(".page {\n");
                page.append("  min-height: 100vh;\n");
                page.append("  padding: 20px;\n");
                page.append("}\n");
                page.append("header {\n");
                page.append("  margin-bottom: 20px;\n");
                page.append("}\n");
                page.append("main {\n");
                page.append("  flex: 1;\n");
                page.append("}\n");
                page.append("</style>\n");

                log.info("Generated Vue3 page: {}", pageName);
                return Flux.just(page.toString());
            } catch (Exception e) {
                log.error("Error generating Vue3 page", e);
                return Flux.just("Error: " + e.getMessage());
            }
        });
    }

    /**
     * 生成Vue3项目结构
     */
    public Flux<String> generateVue3Project(Map<String, Object> params) {
        String projectName = (String) params.getOrDefault("projectName", "vue3-project");
        String outputDir = (String) params.getOrDefault("outputDir", "/tmp");
        String groupId = (String) params.getOrDefault("groupId", "com.example");
        String packageName = (String) params.getOrDefault("package", "com.example." + projectName.replaceAll("[-]", ""));
        String author = (String) params.getOrDefault("author", "generator");
        String version = (String) params.getOrDefault("version", "1.0.0");
        Boolean needTomcat = (Boolean) params.getOrDefault("needTomcat", true);

        return Flux.defer(() -> {
            try {
                SpringBootProGen proGen = new SpringBootProGen();
                Result<String> res = proGen.generateAndZip(outputDir, projectName, groupId, packageName, author, version, needTomcat);
                if (res.getCode() == 0) {
                    log.info("Generated project zip: {}", res.getData());
                    return Flux.just(res.getData());
                } else {
                    String msg = "Generate failed: code=" + res.getCode() + ", msg=" + res.getMessage();
                    log.warn(msg);
                    return Flux.just(msg);
                }
            } catch (Exception e) {
                log.error("Error generating project via SpringBootProGen", e);
                return Flux.just("Error: " + e.getMessage());
            }
        });
    }
}
