+ docker build -f java16 -t mone_java16 .
+ docker run -it mone_java16 /bin/bash
+ docker tag 33bfa1c62a39 x.net/mixiao/mone_java16
+ docker push x.net/mixiao/mone_java16
+ docker pull x.net/mixiao/mone_java16
+ image_name=mone_java16
+ https://x.net/products/docker/#/namespaceDetail?namespaceId=1760&namespaceName=mixiao 
+ docker tag IMAGEID(镜像id) mone_java16