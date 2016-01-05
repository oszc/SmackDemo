package com.zc;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UserUtil {

    /**
     * 登录
     *
     * @param connection xmpp服务器连接
     * @param username   登录帐号
     * @param password   登录密码
     * @return
     */
    public static boolean login(AbstractXMPPConnection connection, String username, String password) {
        try {
            if (connection == null)
                return false;

            connectIfNeeded(connection);
            /** 登录 */
            connection.login(username, password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更改密码
     *
     * @param connection xmpp服务器连接
     * @param username   帐号
     * @param password   旧密码
     * @param password   新密码
     * @return
     */
    public static boolean update(AbstractXMPPConnection connection, String username, String password, String newpassword) {
        try {
            if (connection == null)
                return false;

            connectIfNeeded(connection);
            /** 登录 */
            connection.login(username, password);
            /** 用户操作实例对象：AccountManager */
            AccountManager manager = AccountManager.getInstance(connection);
            manager.sensitiveOperationOverInsecureConnection(true);
            manager.changePassword(newpassword);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 注销
     *
     * @param connection xmpp服务器连接
     * @param username   帐号
     * @param password   密码
     * @return
     */
    public static boolean deleteAll(AbstractXMPPConnection connection, String username, String password) {
        try {
            if (connection == null)
                return false;

            connectIfNeeded(connection);
            /** 登录 */
            connection.login(username, password);
            /** 用户操作实例对象：AccountManager */
            AccountManager manager = AccountManager.getInstance(connection);
            manager.sensitiveOperationOverInsecureConnection(true);
            manager.deleteAccount();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createUser(AbstractXMPPConnection connection, String userName, String pwd) {

        try {
            if (connection == null) {
                return;
            }
            connectIfNeeded(connection);
            AccountManager accountManager = AccountManager.getInstance(connection);
            if (accountManager.supportsAccountCreation()) {
                accountManager.sensitiveOperationOverInsecureConnection(true);
                accountManager.createAccount(userName, pwd);
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    public static void sendMsg(AbstractXMPPConnection connection, String msg, String to) {
        try {
            if (connection == null) {
                return;
            }
            //    System.out.println("msg:"+msg + "  to:"+to);
            connectIfNeeded(connection);
            //connection.login("admin","admin");
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            Chat chat = chatManager.createChat(to + "@" + Constants.SERVER_NAME);
            Message message = new Message();
            message.setBody(msg);
            DeliveryReceiptRequest.addTo(message);
            chat.sendMessage(message);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }
    }

    public static MultiUserChat createRoom(AbstractXMPPConnection connection, String roomName) throws XMPPException, SmackException, IOException {

        connectIfNeeded(connection);
        // Get the MultiUserChatManager
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        // Create a MultiUserChat using an XMPPConnection for a room
        //hello为房间名称,后缀为固定形式
        MultiUserChat muc = manager.getMultiUserChat(roomName + "@conference." + Constants.SERVER_NAME);

        // Create the room
        //testRoom为房间昵称Nickname
        muc.create(roomName);
        muc.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                System.out.println(Utils.getStringBeforeAt(message.getFrom()) + ":" + message.getBody());
            }
        });

        // Get the the room's configuration form
        Form form = muc.getConfigurationForm();
        // Create a new form to submit based on the original form
        Form submitForm = form.createAnswerForm();
        // Add default answers to the form to submit
        for (Iterator fields = form.getFields().iterator(); fields.hasNext(); ) {
            FormField field = (FormField) fields.next();
            if (!FormField.Type.hidden.equals(field.getType()) && field.getVariable() != null) {
                // Sets the default value as the answer
                submitForm.setDefaultAnswer(field.getVariable());
            }
        }
        List owners = new ArrayList();
        owners.add(connection.getUser() + "@" + Constants.SERVER_NAME);
        submitForm.setAnswer("muc#roomconfig_roomowners", owners);
        // Send the completed form (with default values) to the server to configure the room
        muc.sendConfigurationForm(submitForm);
        return muc;
    }

    public static void getRoaster(AbstractXMPPConnection connection, String jUid) {
        try {
            connectIfNeeded(connection);

            Roster roster = Roster.getInstanceFor(connection);
            if (jUid != null && jUid.isEmpty()) {
                Presence presence = roster.getPresence(jUid);
                if (presence != null) {
                    String u = presence.getFrom();
                    Presence.Mode mode = presence.getMode();
                    System.out.println(Utils.getStringBeforeAt(u) + "," + mode);

                } else {
                    System.out.println("not exist!");
                }
            } else {
                Collection<RosterEntry> entries = roster.getEntries();
                for (RosterEntry entry : entries) {
                    System.out.println(entry);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

    }

    public static MultiUserChat joinRoom(AbstractXMPPConnection connection, String roomName) {


        // Get the MultiUserChatManager
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);

        // Create a MultiUserChat using an XMPPConnection for a room
        MultiUserChat muc = manager.getMultiUserChat(roomName+"@conference."+Constants.SERVER_NAME);

        // User2 joins the new room using a password
        // The room service will decide the amount of history to send
        try {
            muc.join(connection.getUser());
            muc.addMessageListener(new MessageListener() {
                @Override
                public void processMessage(Message message) {
                    System.out.println(Utils.getStringBeforeAt(message.getFrom()) + ":" + message.getBody());
                }
            });
            return muc;

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void connectIfNeeded(AbstractXMPPConnection connection) throws IOException, XMPPException, SmackException {
        if (connection != null && !connection.isConnected()) {
            connection.connect();
        }
    }
}