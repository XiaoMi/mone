package com.xiaomi.youpin.jmonitor;

import com.sun.management.OperatingSystemMXBean;
import com.xiaomi.youpin.jmonitor.utils.OS;
import com.xiaomi.youpin.jmonitor.utils.OSEnum;
import sun.jvmstat.monitor.*;
import sun.tools.jps.Arguments;
import sun.tools.jstack.JStack;
import sun.tools.jstat.Jstat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * @author gaoyibo
 */
public class Jmonitor {
    private static final int CPUTIME = 5000;

    private static final int PERCENT = 100;

    private static final int FAULTLENGTH = 10;

    /**
     * 获得当前的监控对象.
     *
     * @return 返回构造好的监控对象
     * @throws Exception
     */
    public MonitorInfo getMonitorInfo(String pid) {
        if (null == pid) {
            return null;
        }

        int kb = 1024;

        // 可使用内存
        long totalMemory = Runtime.getRuntime().totalMemory() / kb;
        // 剩余内存
        long freeMemory = Runtime.getRuntime().freeMemory() / kb;
        // 最大可使用内存
        long maxMemory = Runtime.getRuntime().maxMemory() / kb;

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();

        // 操作系统
        String osName = System.getProperty("os.name");
        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;
        // 已使用的物理内存
        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb
                .getFreePhysicalMemorySize()) / kb;

        Integer threadTotal = getThreadTotal(pid);

        double cpuRatio = 0;
        if (osName.toLowerCase().startsWith("windows")) {
            cpuRatio = this.getCpuRatioForWindows();
        }

        // 构造返回对象
        MonitorInfo infoBean = new MonitorInfo();
        infoBean.setFreeMemory(freeMemory);
        infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
        infoBean.setMaxMemory(maxMemory);
        infoBean.setOsName(osName);
        infoBean.setTotalMemory(totalMemory);
        infoBean.setTotalMemorySize(totalMemorySize);
        infoBean.setTotalThread(threadTotal);
        infoBean.setUsedMemory(usedMemory);
        infoBean.setCpuRatio(cpuRatio);
        return infoBean;
    }

    /**
     * 获得CPU使用率.
     *
     * @return 返回cpu使用率
     */
    private double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir")
                    + "//system32//wbem//wmic.exe process get Caption,CommandLine,"
                    + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPUTIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];
                return Double.valueOf(PERCENT * (busytime) / (busytime + idletime)).doubleValue();
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * 读取CPU信息.
     *
     * @param proc
     * @return
     */
    private long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = Bytes.substring(line, capidx, cmdidx - 1)
                        .trim();
                String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("wmic.exe") >= 0) {
                    continue;
                }
                // log.info("line="+line);
                if (caption.equals("System Idle Process")
                        || caption.equals("System")) {
                    idletime += Long.valueOf(
                            Bytes.substring(line, kmtidx, rocidx - 1).trim())
                            .longValue();
                    idletime += Long.valueOf(
                            Bytes.substring(line, umtidx, wocidx - 1).trim())
                            .longValue();
                    continue;
                }

                kneltime += Long.valueOf(
                        Bytes.substring(line, kmtidx, rocidx - 1).trim())
                        .longValue();
                usertime += Long.valueOf(
                        Bytes.substring(line, umtidx, wocidx - 1).trim())
                        .longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public NetworkInfo getNetworkInfo() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();

        String hostName = addr.getHostName();
        String hostAddress = addr.getHostAddress();

        NetworkInfo networkInfo = new NetworkInfo();
        networkInfo.setHostAddress(hostAddress);
        networkInfo.setHostName(hostName);

        return networkInfo;
    }

    public class JInfo {
        private String pid;
        private String name;
        private String jvmArgs;

        public String getJvmArgs() {
            return jvmArgs;
        }

        public void setJvmArgs(String jvmArgs) {
            this.jvmArgs = jvmArgs;
        }

        public String getPid() {
            return pid;
        }

        public String getName() {
            return name;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "JInfo{" +
                    "pid='" + pid + '\'' +
                    ", name='" + name + '\'' +
                    ", jvmArgs='" + jvmArgs + '\'' +
                    '}';
        }
    }

    /**
     * http://www.docjar.com/html/api/sun/tools/jps/Jps.java.html
     */
    public List<JInfo> getJpsInfo() throws MonitorException {
        String[] args = {"-lvV"};
        Arguments arguments = new Arguments(args);

        HostIdentifier hostId = arguments.hostId();
        MonitoredHost monitoredHost = MonitoredHost.getMonitoredHost(hostId);

        Set jvms = monitoredHost.activeVms();

        List<JInfo> res = new ArrayList<>();

        for (Iterator j = jvms.iterator(); j.hasNext(); /* empty */) {
            StringBuilder output = new StringBuilder();
            Throwable lastError = null;
            JInfo jinfo = new JInfo();

            int lvmid = ((Integer) j.next()).intValue();

            jinfo.setPid(String.valueOf(lvmid));

            output.append(String.valueOf(lvmid));

            if (arguments.isQuiet()) {
                System.out.println(output);
                continue;
            }

            MonitoredVm vm = null;
            String vmidString = "//" + lvmid + "?mode=r";

            String errorString = null;

            try {

                errorString = " -- process information unavailable";
                VmIdentifier id = new VmIdentifier(vmidString);
                vm = monitoredHost.getMonitoredVm(id, 0);

                errorString = " -- main class information unavailable";
                output.append(" " + MonitoredVmUtil.mainClass(vm,
                        arguments.showLongPaths()));

                jinfo.setName(MonitoredVmUtil.mainClass(vm, arguments.showLongPaths()));

                if (arguments.showMainArgs()) {
                    errorString = " -- main args information unavailable";
                    String mainArgs = MonitoredVmUtil.mainArgs(vm);
                    if (mainArgs != null && mainArgs.length() > 0) {
                        output.append(" " + mainArgs);
                    }
                }
                if (arguments.showVmArgs()) {
                    errorString = " -- jvm args information unavailable";
                    String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
                    if (jvmArgs != null && jvmArgs.length() > 0) {
                        output.append(" " + jvmArgs);
                        jinfo.setJvmArgs(jvmArgs);
                    }
                }
                if (arguments.showVmFlags()) {
                    errorString = " -- jvm flags information unavailable";
                    String jvmFlags = MonitoredVmUtil.jvmFlags(vm);
                    if (jvmFlags != null && jvmFlags.length() > 0) {
                        output.append(" " + jvmFlags);
                    }
                }

                errorString = " -- detach failed";
                monitoredHost.detach(vm);

                errorString = null;

                res.add(jinfo);

            } catch (URISyntaxException e) {

                lastError = e;
                assert false;
            } catch (Exception e) {
                lastError = e;
            } finally {
                if (errorString != null) {

                    output.append(errorString);
                    if (arguments.isDebug()) {
                        if ((lastError != null)
                                && (lastError.getMessage() != null)) {
                            output.append("\n\t");
                            output.append(lastError.getMessage());
                        }
                    }
                    System.out.println(output);
                    if (arguments.printStackTrace()) {
                        lastError.printStackTrace();
                    }
                    continue;
                }
            }
        }

        return res;
    }

    /**
     * http://www.docjar.com/docs/api/sun/tools/jstat/Jstat.html
     */
    public void getJstat(String[] args) {
        Jstat.main(args);
    }


    /**
     * http://www.docjar.com/docs/api/sun/tools/jstack/JStack.html
     */
    public void getJstack(String[] args) throws Exception {
        JStack.main(args);
    }

    public Integer getThreadTotal(String pid) {
        List<String> args = new ArrayList<>();
        OSEnum os = OS.getOsName();
        args.add("ps");

        if (os == OSEnum.Mac) {
            args.add("hu");
        } else if (os == OSEnum.Unix) {
            args.add("huH");
        } else {
            return -1;
        }

        args.add(pid);

        try {
            String[] argsArr = new String[args.size()];
            argsArr = args.toArray(argsArr);
            Process process = Runtime.getRuntime().exec(argsArr);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            Integer total = 0;
            while ((line = reader.readLine()) != null) {
                total++;
            }
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                return total;
            } else {

            }
        } catch (Throwable e) {
            //
            System.out.println(e);
        }

        return -1;
    }

}
