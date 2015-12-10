package com.zc;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class Main {

    public static String receiveMsg = null;
    public static void main(String[] args) {
	// write your code here
    //   String version =  SmackConfiguration.getVersion();
     //   System.out.println(version);

        String server = "150.116.3.98";
        String user = "helloWorld";
        String pwd = "helloWorld";

        XMPPTCPConnection connection= ConnectionUtil.getInstance(server, 5222);

        UserUtil.login(connection,user,pwd);

        final ChatManager chatManager = ChatManager.getInstanceFor(connection);

        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {


                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        receiveMsg =message.getBody();
                        if(receiveMsg!=null)
                        System.out.println(receiveMsg);
                    }
                });
            }
        });

        Thread keepRunning =new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        keepRunning.start();
    }
}
