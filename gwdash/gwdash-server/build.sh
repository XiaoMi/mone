#!/bin/bash

#
#  Copyright 2020 Xiaomi
#
#      Licensed under the Apache License, Version 2.0 (the "License");
#      you may not use this file except in compliance with the License.
#      You may obtain a copy of the License at
#
#          http://www.apache.org/licenses/LICENSE-2.0
#
#      Unless required by applicable law or agreed to in writing, software
#      distributed under the License is distributed on an "AS IS" BASIS,
#      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#      See the License for the specific language governing permissions and
#      limitations under the License.
#

# Extract the cluster from the job name
job=$1
cluster=$(echo $job | sed -n 's/.*_jobgroup\.\([^._]*\)_.*/\1/p')
mode=''
branch='master'

if [ $cluster == 'xmnstaging' ] || [ $cluster == 'newstaging' ]; then
    mode=':staging'
    branch='staging'
elif [ $cluster == 'dev' ]; then
    mode=':dev'
    branch='dev'
elif [ $cluster == 'intranet' ]; then
    mode=':intranet'
    branch='intranet'
fi

echo $cluster
echo $mode
echo $branch

export JAVA_HOME=/opt/soft/jdk1.8.0_131
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=.:$JAVA_HOME/lib:$JRE_HOME/lib
export PATH=$JAVA_HOME/bin:$PATH

rm -rf release
mkdir -p release

rm -rf fe
mkdir -p fe

git clone -b $branch 
rm -rf ./gwdash-fe-release/.git

rm -rf ./src/main/resources/static/*
cp -R gwdash-fe-release/* ./src/main/resources/static/

ls ./src/main/resources/static/

files=$(ls)
echo $files
cur=$(pwd)
echo $cur

mvn -U clean -D$cluster=true package install -P$cluster -Dmaven.test.skip=true
cp target/gwdash-server-0.0.1-SNAPSHOT.jar release/
cp -r deploy release/


