
# 1.docker login
docker login --username=your_name test.com

# 2.打包
mvn -U clean package -Dmaven.test.skip=true -P open

# 3.构建并push镜像(修改版本号vx)
cd m78-server

docker build -t test.com/m78:oz-m78-v10 --platform linux/amd64 -f Dockerfile .
docker build -t test.com/ozx:oz-m78-v63 --platform linux/amd64 -f Dockerfile .

docker push test.com/ozx:oz-m78-v63

# 4.deploy
修改 [deploy.yaml](deploy.yaml) 中镜像地址，
注意: 填写镜像地址时 加个-vpc：test.com/ozx:oz-m78-vx

kubectl apply -f deploy.yaml




