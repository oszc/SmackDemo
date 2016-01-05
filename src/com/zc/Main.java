package com.zc;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {

    public static String receiveMsg = null;
    public static boolean shouldClose = false;
    public static String server = "50.116.3.94";

    private static final String CMD_SEND ="send";
    private static final String CMD_CREATE_ROOM ="createRoom";
    private static final String CMD_ROASTER="roaster";
    private static final String CMD_JOIN="join";
    private static final String CMD_SEND_GROUP="sendGroup";


    public static void main(String[] args) {
        /*
        final XMPPTCPConnection connection= ConnectionUtil.getInstance(server, 5222);
        if(args.length==2){

            String u = args[0];
            String p = args[1];

            start(connection,u,p);

        }else {

            start(connection,"haha1","1234");
        }
        */
        while (true){
            testReceive();
        }
    }
    public static void testReceive(){
        DatagramSocket ds = null;  //定义服务，监视端口上面的发送端口，注意不是send本身端口
        try {
            System.out.println("waiting...");
            ds = new DatagramSocket(10000);
            byte[] buf = new byte[1024];//接受内容的大小，注意不要溢出

            DatagramPacket dp = new DatagramPacket(buf,0,buf.length);//定义一个接收的包

            ds.receive(dp);//将接受内容封装到包中

            String data = new String(dp.getData(), 0, dp.getLength());//利用getData()方法取出内容
            System.out.println("received!");

            System.out.println(data);//打印内容

            ds.close();//关闭资源
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void start(final XMPPTCPConnection connection,String u,String p){

        UserUtil.login(connection,u,p);
        UserUtil.getRoaster(connection,null);
        final ChatManager chatManager = ChatManager.getInstanceFor(connection);

        final DeliveryReceiptManager deliveryReceiptManager = DeliveryReceiptManager.getInstanceFor(connection);
        deliveryReceiptManager.autoAddDeliveryReceiptRequests();
        deliveryReceiptManager.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
        deliveryReceiptManager.addReceiptReceivedListener(new ReceiptReceivedListener() {
            @Override
            public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
                //System.out.println(fromJid.split("@")[0]+"已经接收到消息:)");
                System.out.println(Utils.getStringBeforeAt(fromJid)+"已经接收到消息:)");
            }
        });
        //监听全局聊天
        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        receiveMsg =message.getBody();
                        String from = message.getFrom();
                        if(receiveMsg!=null) {
                            System.out.println(Utils.getStringBeforeAt(from)+":"+receiveMsg);
                            if (("quit").equals(receiveMsg))
                                shouldClose = Boolean.TRUE;
                        }
                    }
                });
            }
        });

        Thread keepRunning =new Thread(new Runnable() {
            @Override
            public void run() {
                MultiUserChat muc = null;
                while (true) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    try {
                        String s = br.readLine();
                        if (s==null || s.isEmpty()){
                            continue;
                        }
                        String[] console = s.split(" ");
                        if (console.length <2 ){
                            System.out.println("usage: command msg");
                            continue;
                        }

                        String cmd = console[0];
                        String msg = console[1];

                        if(cmd.equals(CMD_SEND)){
                            String to =console[2];
                            UserUtil.sendMsg(connection, msg, to);
                        }

                        if(cmd.equals(CMD_CREATE_ROOM)){
                            muc = UserUtil.createRoom(connection,msg);
                        }

                        if(cmd.equals(CMD_ROASTER)){
                            UserUtil.getRoaster(connection,msg);
                        }

                        if(cmd.equals(CMD_JOIN)){
                            muc = UserUtil.joinRoom(connection,msg);
                        }

                        if(cmd.equals(CMD_SEND_GROUP) && muc!=null ){
                            muc.sendMessage(msg);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                    if(shouldClose){
                        break;
                    }
                }
            }
        });
        keepRunning.start();
    }
}

