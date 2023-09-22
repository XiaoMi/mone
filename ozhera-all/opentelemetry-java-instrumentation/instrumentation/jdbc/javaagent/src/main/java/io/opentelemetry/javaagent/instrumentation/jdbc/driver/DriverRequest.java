package io.opentelemetry.javaagent.instrumentation.jdbc.driver;

public class DriverRequest {

    private String userName;
    private String password;
    private String domainPort;
    private String dataBaseName;
    private String type;

    public DriverRequest(){}

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDomainPort() {
        return domainPort;
    }

    public String getType() {
        return type;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDomainPort(String domainPort) {
        this.domainPort = domainPort;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "DriverRequest{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", domainPort='" + domainPort + '\'' +
                ", dataBaseName='" + dataBaseName + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
