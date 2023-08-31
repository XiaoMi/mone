+ 第一个不依赖小米库可以编译过的版本:2023-04-02
+ The first version that can be compiled without relying on the Xiaomi library: 2023-04-02 
+ 类似于谷歌的guava,提供一些组内经常使用的库
+ Similar to Google's Guava, it provides some internally used libraries 
+ jcommon　是Mone开发团队在开发过程中抽象的一套Java基础库，包含了日志，缓存，rpc框架，多线程多个模块。
+ Jcommon is a Java library developed by the Mone dev team that contains common packages for logging, caching, rpc, concurrency, and database access.
+ hera 主要依赖 rcurve+docean+rpc
+ hera mainly relies on rcurve+docean+rpc 
+ mifaas 主要依赖 rcurve+docean+docean-plugin
+ mifaas mainly depends on rcurve+docean+docean-plugin 
+ 支持Hera(可观测系统) Odin(Mesh系统) Tesla(网关) 等项目
+ Support for projects such as Hera (Observable System), Odin (Mesh System), Tesla (Gateway), etc.
+ 推荐maven设置(主要是国内用阿里的源),阿里的那个可以不设置,阿里的主要是用来提速
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
