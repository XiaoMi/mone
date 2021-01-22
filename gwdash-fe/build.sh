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

# 用法build.sh $clusterName $commitMsg

echo $(pwd)
cluster=$1
mode=:$1

if [ "$mode" == ":master" ]; then
    mode=""
fi

echo $mode

if [ ! -d "gwdash-fe-release" ]; then
    git clone -b $cluster git@git.n.xiaomi.com:youpin-gateway/gwdash-fe-release.git
    rm -rf ./css
    rm -rf *.js
    rm -rf *.js.map
    echo "clone gwdash-fe-release from gitlab"
else
    cd gwdash-fe-release
    git checkout $cluster
    git pull origin $cluster
    rm -rf ./css
    rm -rf *.js
    rm -rf *.js.map
    echo "pull latest release from gitlab"
    cd ../
fi

if [ "`ls -A gwdash-fe-release`" = "" ]; then
    echo "something is wrong and  not executed successfully."
    exit 1
fi

echo "start build locally"
yarn run build$mode

if [ ! -d "dist" ]; then
    echo "failed to build gwdash-fe"
else
    cp -r ./dist/* gwdash-fe-release/
    cd gwdash-fe-release
    rm -rf *.js.map
    git add .
    git commit -m "$2"
    git push origin $cluster
fi


