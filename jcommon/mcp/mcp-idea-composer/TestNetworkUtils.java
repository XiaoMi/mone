import run.mone.mcp.idea.composer.util.NetworkUtils;

public class TestNetworkUtils {
    public static void main(String[] args) {
        System.out.println("=== Testing NetworkUtils ===");
        
        String localIp = NetworkUtils.getLocalIpAddress();
        System.out.println("Local IP Address: " + localIp);
    }
} 