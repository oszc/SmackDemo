package com.zc;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

public class UserUtil {

	/**
	 * 登录
	 * 
	 * @param connection
	 *            xmpp服务器连接
	 * @param username
	 *            登录帐号
	 * @param password
	 *            登录密码
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
			* @param connection
	*            xmpp服务器连接
	* @param username
	*            帐号
	* @param password
	*            旧密码
	* @param password
	*            新密码
	* @return
			*/
	public static boolean update(AbstractXMPPConnection connection, String username, String password,String newpassword) {
		try {
			if (connection == null)
				return false;

			connectIfNeeded(connection);
			/** 登录 */
			connection.login(username, password);
			/** 用户操作实例对象：AccountManager */
			AccountManager manager=AccountManager.getInstance(connection);
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
	 * @param connection
	 *            xmpp服务器连接
	 * @param username
	 *            帐号
	 * @param password
	 *            密码
	 * @return
	 */
	public static boolean deleteAll(AbstractXMPPConnection connection,String username, String password) {
		try {
			if (connection == null)
				return false;

			connectIfNeeded(connection);
			/** 登录 */
			connection.login(username, password);
			/** 用户操作实例对象：AccountManager */
			AccountManager manager=AccountManager.getInstance(connection);
			manager.sensitiveOperationOverInsecureConnection(true);
			manager.deleteAccount();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void createUser(AbstractXMPPConnection connection,String userName,String pwd){

		try {
			connectIfNeeded(connection);
			connection.login("admin","admin");
			AccountManager accountManager = AccountManager.getInstance(connection);
			accountManager.sensitiveOperationOverInsecureConnection(true);
			accountManager.createAccount(userName, pwd);
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

	public static void connectIfNeeded(AbstractXMPPConnection connection) throws IOException, XMPPException, SmackException {
		if(connection!=null && !connection.isConnected()){
			connection.connect();
		}
	}
}