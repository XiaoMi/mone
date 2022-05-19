+ docker build -f java16 -t mone_java16 .
+ docker run -it mone_java16 /bin/bash
+ docker tag 33bfa1c62a39 cr.d.xiaomi.net/mixiao/mone_java16
+ docker push cr.d.xiaomi.net/mixiao/mone_java16
+ docker pull cr.d.xiaomi.net/mixiao/mone_java16
+ image_name=mone_java16
+ https://cloud.d.xiaomi.net/products/docker/#/namespaceDetail?namespaceId=1760&namespaceName=mixiao 
+ docker tag IMAGEID(镜像id) mone_java16