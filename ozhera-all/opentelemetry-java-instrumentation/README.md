# Overview
This is the project used by Hera to intercept methods and extract trace data, commonly referred to as a "probe". It's based on the open-source version of Opentelemetry, with added Hera-specific instrumentation, exporter, JVM metrics, and other features.

## Build Dependencies

### JDK
This project requires JDK version 11 or higher.

### Gradle
1. Download and install gradle-7.0.1.

2. Specify the Gradle data source. In the Gradle installation directory, create a new `init.d` directory. Inside `init.d`, create an `init.gradle` file. The file content can be set up as:

    ```gradle
    allprojects {
        repositories {
            mavenLocal()
            maven { url 'https://maven.aliyun.com/nexus/content/repositories/central/' }
            mavenCentral()
        }
    }
    ```

   This uses Alibaba's domestic mirror repository, which can speed up the dependency file download.

3. Add the local environment variable, `GRADLE_USER_HOME=${gradle installation directory}`, and then add `${gradle installation directory}/bin` to the PATH. After adding, you can execute `gradle -v` from any location to check if the environment variable is effective.

4. After importing the project into IDEA, you need to configure the project's internal Gradle in IDEA:
    - `Gradle user home`: Set the download location for Gradle dependencies.
    - `Use gradle from`: Choose 'gradle-wrapper.properties' file.
    - `Gradle JVM`: Select the directory of the installed JDK 11.

5. Accelerate dependency import by setting Maven. Set IDEA Build Tools----Maven's `Maven home path` to the commonly used Maven directory. Set `User setting files` to the commonly used Maven settings.xml and `Local repository` to the commonly used Maven repository. With these configurations, Gradle can use existing Maven dependencies, speeding up project import.

6. Check if there is a .git folder in the opentelemetry-java-instrumentation directory. If not, copy one from the parent directory, or execute `git init` in the opentelemetry-java-instrumentation directory. This is because many gradle plugins in the probe need to use git for version control.

7. Execute `Reload All Gradle Projects` in the IDEA Gradle toolbar and wait for Gradle to download dependency files. This process may take 30-60 minutes for the first import.

8. Execute `./gradlew assemble` in the project root directory to build. After successful construction, the `opentelemetry-javaagent-${version}-all.jar` file will be generated in the `javaagent` module's `build/libs` directory. This jar file is the final probe.

### *Possible Issues
1. If you encounter errors like ClassNotFound, missing symbols, or missing packages, it might be due to incomplete dependency downloads. Click on `Reload All Gradle Projects` to continue the download.

2. You might see errors like "Deprecated Gradle features were used in this build, making it incompatible with Gradle 8.0". This is just a version expiration warning from Gradle and will not cause any issues.

3. If you get the error "Make sure Gradle is running on a JDK, not JRE", ensure your local environment variables point to a JDK.

4. If the error "Missing symbol: classpathLoader(toolingRuntime, ClassLoader.getPlatformClassLoader(), project)" appears, check if the JDK version in your local environment variables is JDK 11 by executing `java -version`.

5. Use the HTTPS Maven repository URL. If a prompt like "Using insecure protocols with repositories...to redirect to a secure protocol (like HTTPS) or allow insecure protocols" appears, please change the repository URL in the `init.d` file we just created to use HTTPS.

6. When executing `gradlew assemble`, if you get an error like "Could not find opentelemetry-sdk-extension-jaeger-remote-sampler-1.15.0.jar (io.opentelemetry:opentelemetry-sdk-extension-jaeger-remote-sampler:1.15.0)", or similar missing jar file errors, you can comment out the `maven.aliyun.com` repository URL in `build.gradle.kts` under the `allprojects` section and re-execute `gradlew assemble`.

## Runtime Dependencies
### Environment Variables
`host.ip`: Used to record the current physical machine IP, displayed in trace's process.tags. In Kubernetes, it captures the pod's IP.

`node.ip`: Records the IP of the current node in Kubernetes.

`MIONE_LOG_PATH`: Used to specify the log directory on the mione application, storing trace span information in `${MIONE_LOG_PATH}/trace/trace.log`. If not set, it defaults to `/home/work/log/none/trace/trace.log`.

`mione.app.name`: Records the service name in the format `projectId-projectName`. For example: 1-test, where 1 is the projectId and test is the projectName. If not set, it defaults to "none".

`TESLA_HOST`: Same as `host.ip`. Used for Nacos registration and the serverIp tag in JVM metrics.

`JAVAAGENT_PROMETHEUS_PORT`: An available port number on the current machine. It's used for the httpServer that Prometheus uses to pull JVM metrics. Defaults to 55433 if not set.

`hera.buildin.k8s`: Indicates if the service is deployed in Kubernetes. Set to 1 if it is.

`MIONE_PROJECT_ENV_NAME`: Name of the current deployment environment, e.g., dev, uat, st, preview, production.

`MIONE_PROJECT_ENV_ID`: ID of the current deployment environment.

### JVM Parameters
Various JVM parameters are given for the probe configuration. For instance, `-javaagent:/opt/soft/opentelemetry-javaagent-all-0.0.1.jar` indicates the location of the javaagent probe jar on the server. Many parameters like this one set various properties for the probe.

## Opentelemetry-java
For more details, configurations, and design principles, please refer to the open-source version of opentelemetry-java-instrumentation:


https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/v1.3.x
