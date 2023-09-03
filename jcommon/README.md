+ The first version that can be compiled without relying on the Xiaomi library.:2023-04-02
+ Try to implement the new code version using Java 20's coroutines instead of threads.
+ Similar to Google's Guava, it provides some commonly used libraries within the group.
+ Similar to Google's Guava, it provides some internally used libraries
+ jcommon Mone is an abstract Java foundation library developed by the development team, which includes modules such as
  logging, caching, RPC framework, and multi-threading.
+ Jcommon is a Java library developed by the Mone dev team that contains common packages for logging, caching, rpc,
  concurrency, and database access.
+ hera Mainly rely on rcurve+docean+rpc
+ hera mainly relies on rcurve+docean+rpc
+ mifaas Mainly rely on rcurve+docean+docean-plugin
+ mifaas mainly depends on rcurve+docean+docean-plugin
+ Support projects like Hera (Observable System), Odin (Mesh System), Tesla (Gateway), etc.
+ Support for projects such as Hera (Observable System), Odin (Mesh System), Tesla (Gateway), etc.
+ Recommend maven settings (mainly using Alibaba's source in China), Alibaba's can be not set, Alibaba's is mainly used
  for speeding up.

```
<settings>

    <interactiveMode>false</interactiveMode>

   <mirrors>
        <mirror>
            <id>alimaven</id>
            <name>aliyun maven</name>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>


    <profiles>
        <profile>
            <repositories>
                <repository>
                    <snapshots />
                    <id>ossrh</id>
                    <name>ossrh-snapshot</name>
                    <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
                </repository>
            </repositories>
            <id>artifactory</id>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>artifactory</activeProfile>
    </activeProfiles>
    <proxies/>
</settings>
```
