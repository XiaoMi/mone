#!/bin/bash
path=.
# 过滤网站
grep --exclude-dir={.git,node_modules,build} --exclude=.gitmodules -Ri "mi\.com" $path
# 过滤ip
grep --exclude-dir={.git,node_modules,build} --exclude=.gitmodules -Ri -Rni "10\.\d\{1,3\}\.\d\{1,3\}\.\d\{1,3\}" $path
## 过滤x-proxy-userdetail等
grep --exclude-dir={.git,node_modules,build} --exclude=.gitmodules -Rni "X-Proxy-Midun" $path
grep --exclude-dir={.git,node_modules,build} --exclude=.gitmodules -Rni "x-proxy-userdetail" $path
grep --exclude-dir={.git,node_modules,build} --exclude=.gitmodules -Rni "ProxyMidun" $path
grep --exclude-dir={.git,node_modules,build} --exclude=.gitmodules -Rni "ProxyUserDetail" $path
grep --exclude-dir={.git,node_modules,build} --exclude=.gitmodules -Rni "\w\{96\}==" $path