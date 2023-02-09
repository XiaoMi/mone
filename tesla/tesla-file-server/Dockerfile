#  Copyright 2020 Xiaomi
#
#     Licensed under the Apache License, Version 2.0 (the "License");
#     you may not use this file except in compliance with the License.
#     You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
#     Unless required by applicable law or agreed to in writing, software
#     distributed under the License is distributed on an "AS IS" BASIS,
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.

FROM cr.d.xiaomi.net/mixiao/miserver:latest

WORKDIR xxxx/tesla-file-server

RUN mkdir -p xxxx/log/agent/

COPY deploy/release/fileserver.jar ./

ENTRYPOINT ["java", "-jar", "-Xms5120M", "-Xmx5120M", "-XX:MaxDirectMemorySize=5120M", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-XX:+PrintReferenceGC", "-XX:+PrintGCDetails", "-XX:+PrintGCDateStamps", "-XX:+PrintHeapAtGC", "-verbose:gc", "-Xloggc:xxxx/log/agent/gc.log", "xxxx/tesla-file-server/fileserver.jar"]
