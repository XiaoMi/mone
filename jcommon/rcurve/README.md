#### Communication framework for the mesh layer (sidecar)
+ A well-performing mesh underlying communication framework.
+ Support UDS communication and TCP communication.
+ Support hessian gson protostuff encoding.
+ The performance is pretty good.
+ jvm
+ --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED --add-opens=java.base/sun.reflect=ALL-UNNAMED --add-exports=java.base/sun.reflect.annotation=ALL-UNNAMED --add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED --enable-preview