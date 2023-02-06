import java.nio.file.Files
import java.nio.file.Paths

def log4j(Map<String, String> m) {
    println("log4j begin")
    String path = "/Users/zhangzhiyong/IdeaProjects/untitled"
    File file = new File(path)
    ArrayList<File> al = new ArrayList<File>()
    flist(file, al)
    for (File it : al) {
        if (it.getName() == "pom.xml") {
            println(it)
            List<String> lines = it.readLines()
            int artifactLine = 0
            int versionLine = 0
            int v = 0
            for (String l : lines) {
                if (artifactLine == 0) {
                    if (l.contains("<artifactId>log4j-core</artifactId>")) {
                        artifactLine = 1
                    }
                }
                if (artifactLine == 1) {
                    if (l.contains("version")) {
                        artifactLine = 2
                        v = versionLine
                    }
                }
                versionLine++;
            }

            if (artifactLine == 2) {
                String version = lines.get(v)
                String versionNo = version.substring(version.indexOf('>') + 1, version.lastIndexOf('<'))
                println("current version no:" + versionNo)
                if (!checkLog4jVersion(versionNo)) {
                    lines.set(v, "            <version>2.17.0</version>")
                    Files.write(Paths.get(it.path), lines)
                }
            }
        }
    }
}


static boolean checkLog4jVersion(String version) {
    String rightVersion = "2.17.0"
    if (compareVersion(version, rightVersion) >= 0) {
        return true
    }
    return false
}

/**
 * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
 *
 * @param version1
 * @param version2
 * @return
 */
static def int compareVersion(String version1, String version2) throws Exception {
    if (version1 == null || version2 == null) {
        throw new Exception("compareVersion error:illegal params.");
    }
    //注意此处为正则匹配，不能用"."；
    String[] versionArray1 = version1.split("\\.");
    String[] versionArray2 = version2.split("\\.");
    int idx = 0;
    int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
    int diff = 0;
    while (idx < minLength
            && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0
            && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {
        ++idx;
    }
    //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
    diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
    return diff;
}

def flist(File f, List<File> list) {
    if (f.isDirectory()) {
        File[] lf = f.listFiles()
        for (File i : lf) {
            flist(i, list)
        }
    } else {
        list.add(f)
    }
}

log4j(new HashMap<String, String>())