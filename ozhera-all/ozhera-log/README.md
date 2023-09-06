# milog

# log-agent supports JDK20 (2023-08-29)
+ Remember to include the following start-up parameters:
  --enable-preview --add-opens java.base/java.util.regex=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED  --add-opens=java.base/java.util=ALL-UNNAMED --add-modules=jdk.incubator.concurrent --add-opens java.base/sun.nio.fs=ALL-UNNAMED

# Support for external compilation (removed all Xiaomi dependencies): 2023-04-03

# Code Standards
### 1. Project Standards
#### 1.1 Responsibilities of Parent Project POM
##### a) Manage sub-projects through modules (Important)
##### b) Globally manage package versions through dependencyManagement (Important)
##### c) Manage dependencies that all sub-projects have in common through dependency

#### 1.2 Standards for Each Sub-module
##### a) Design sensible layers. The controller, job, and mq layers serve as entry points, and the service layer encapsulates business logic. Common logic in the service can be further divided into the manager layer.
##### b) Strictly avoid cyclic dependencies.

### 2. Code Standards
Please refer to the "Alibaba Developer Manual" when writing code. Install the p3c IDEA plugin and Maven plugin for static code scanning.

### 3. Branch Management
##### a) intranet: Deployment branch, staging: Testing environment branch
