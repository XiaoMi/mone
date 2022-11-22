#!/bin/bash

# Extract the cluster from the job name
job=$1
cluster=$(echo $job | sed -n 's/.*_cluster\.\([^._]*\)_.*/\1/p')

jobgroup=$(echo $job | sed -n 's/.*_jobgroup\.\([^._]*\)_.*/\1/p')

if [ $jobgroup != 'gateway' ]; then
    cluster=$jobgroup
fi

export JAVA_HOME=/opt/soft/openjdk1.8.0_202
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=.:$JAVA_HOME/lib:$JRE_HOME/lib
export PATH=$JAVA_HOME/bin:$PATH

rm -rf release
mkdir -p release

echo $cluster
files=$(ls)
echo $files
cur=$(pwd)
echo $cur

mvn -U clean -D$cluster=true package install -P$cluster -Dmaven.test.skip=true

cp target/tesla-gateway-1.0.0-SNAPSHOT.jar release/
cp -r deploy release/


