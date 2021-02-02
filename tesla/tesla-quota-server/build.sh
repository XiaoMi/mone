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
cluster=$(echo $job | sed -n 's/.*_cluster\.\([^._]*\)_.*/\1/p')

jobgroup=$(echo $job | sed -n 's/.*_jobgroup\.\([^._]*\)_.*/\1/p')

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

cp target/tesla-quota-server-1.0.0-SNAPSHOT.jar release/
cp -r deploy release/


