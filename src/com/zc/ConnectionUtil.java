package com.zc;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

public class ConnectionUtil {

    private static XMPPTCPConnection connection =null;
    static {
     // SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
     // SASLAuthentication.blacklistSASLMechanism("CRAM-MD5");
      //  SASLAuthentication.blacklistSASLMechanism("PLAIN");
    }
    //静态工厂方法
    public static XMPPTCPConnection getInstance(String serverName,  int port) {
        if (connection == null) {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setServiceName(Constants.SERVER_NAME) //must be the same ServiceName as set in the server! localhost or 127.0.0.1 or some error may occur
                    .setDebuggerEnabled(false)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setHost(serverName)
                    .setPort(port)
                    .build();
            connection = new XMPPTCPConnection(config);
        }
        return connection;
    }


}