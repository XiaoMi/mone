# Overview
ozhera-monitor is one of the core projects within the ozhera system. It is responsible for managing various crucial functions related to the monitoring system, including the application center, metric monitoring, metric alert configuration, alert group configuration, monitoring dashboard display, and more.

# Supported Environments
JDK: Java 8

DB: MySQL (For specific table structures, please refer to the SQL files in the project)

Others: RocketMQ, Redis

Deployment：
You can deploy it with a one-click operation using the ozhera-operator. Please refer to the ozhera-operator deployment process in the same directory.

# Application Integration
Applications are first created through TPC (presumably a management interface). Application information is automatically synchronized to the ozhera application resource pool through TPC. Users can add applications they want to monitor to the participation list and follow list in the application center. For specific usage, please refer to the ozhera user manual.

# **Prometheus**
### Version: V2.37.2 and above
### Configuration Changes:
#### Populate the AlertManager address, for example:
   alerting:

   alertmanagers:

   static_configs: - targets: - 127.0.0.1:9093

##### Populate the path to the alert rules file, for example:
   rule_files: - /home/work/app/prometheus_mione/alert.rules

### Download Link:
<https://prometheus.io/docs>

# **Grafana**
### Version: 
#### V7.5 and above
### Runtime Configuration Dependencies:
1. Apply for an API key in advance and replace grafana.api.key with it.
2. Configure the Grafana address in the settings and replace grafana.address with it.
3. Configure the Prometheus data source in Grafana in advance and replace the data source name with grafana.prometheus.datasource.
4. Create directories in Grafana in advance, call the Grafana API to get the directory UID, and populate it in grafana.folder.id and grafana.folder.uid.
5. Deploy container and physical machine monitoring charts in advance and populate the configurations in grafana.container.url and grafana.host.url.

### Configuration Changes:
domain= // Change to your local IP
   
disable_login_form=true
   
oauth_auto_login=true

[auth.generic_oauth]

enabled = true

name = TPC

allow_sign_up = true

client_id = zgftest

client_secret =

empty_scopes = true

auth_url = http://xx.xx.xx/user-manage/login

token_url = http://localhost:8098/oauth/token

api_url = http://localhost:8098/oauth/api/user

cookie_samesite = none   // Cookie policy

allow_embedding = true  // Allow iframes

cookie_secure = true    // Set if Grafana domain is HTTPS

auto_assign_org_role = Editor  // Default editor permissions

[auth.anonymous]

enabled = true        // Default anonymous login

### Add Plugins:
   Add the grafana-singlestat-panel and grafana-piechart-panel plugins.
### Download Link:
<https://grafana.com/docs>

# **AlertManager**
### Version: 
V0.22.2 and above
### Configuration Changes:
   Configure alert notifications, for example, using the webhook method:
##### receivers: - name: 'web.hook'
##### webhook_configs:
- url: 'http://localhost:8080/api/v1/rules/alert/sendAlert'
### Download Link:
# **Node-exporter**
### Version: 
##### V1.2.2 and above
### Download Link
https://github.com/prometheus/node_exporter/releases
# **Cadvisor**
### Version: 
##### V0.42.0 and above
### Download Link:
https://github.com/google/cadvisor



