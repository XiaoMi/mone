# ozhera-log

# log-agent support jdk20（2023-08-29）
+ Note that in the startup parameters, add：--enable-preview --add-opens java.base/java.util.regex=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED  --add-opens=java.base/java.util=ALL-UNNAMED --add-modules=jdk.incubator.concurrent --add-opens java.base/sun.nio.fs=ALL-UNNAMED


# Support external compilation (remove all dependencies of xiaomi):2023-04-03

# Code specifications
### 1、Engineering specifications
#### 1.1 Parent project pom responsibility boundary
##### a) Manage subprojects through modules (important)
##### b) Manage package versions globally through dependency Management (important)
##### c) Manage the packages that all subprojects will depend on through dependency

#### 1.2 Specifications for each submodule
##### a) Reasonable layering design, the controller, job, and mq layers are the entry layer, the service is the business logic layer, and the common logic of the service can be lowered to the manager layer
##### b) Strictly avoid circular dependencies

### 2、Code specifications
Please refer to the "Alibaba Development Manual" for code writing, and install the p3c idea plug-in and maven plug-in for static code scanning.

### 3、branch management
##### a) intranet: online branch, staging: test environment branch

