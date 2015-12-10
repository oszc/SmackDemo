package com.zc;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

public class ConnectionUtil {

    private static XMPPTCPConnection connection =null;
    static {
      SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
      SASLAuthentication.blacklistSASLMechanism("CRAM-MD5");
      //  SASLAuthentication.blacklistSASLMechanism("PLAIN");
    }
    //静态工厂方法
    public static XMPPTCPConnection getInstance(String serverName,  int port) {
        if (connection == null) {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setServiceName(serverName)
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