package com.meng.config;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.meng.Autoreply;
import com.meng.Methods;

public class SocketDicThread extends Thread {
	Socket socket = null;
	ConfigManager configManager;

	public SocketDicThread(ConfigManager configManager, ServerSocket serverSocket) {
		try {
			this.socket = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.configManager = configManager;
		InetAddress address = socket.getInetAddress();
		System.out.println("当前客户端的IP ： " + address.getHostAddress());
	}

	public void run() {

		try {
			InputStream inputStream = socket.getInputStream();
			DataInputStream dataInputStream = new DataInputStream(inputStream);
			String string = dataInputStream.readUTF();

			// string = new String(Base64.decryptBASE64(string), "utf-8");

			System.out.println("服务器读取客户端的：" + string);
			OutputStream outputStream = socket.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			String fileName = Autoreply.appDirectory + "dic"
					+ string.substring(0, string.indexOf(".")).replace("get", "").replace("write", "") + ".json";
			System.out.println(fileName);
			if (string.startsWith("get")) {
				dataOutputStream.writeUTF(Methods.readFileToString(fileName));
			} else if (string.startsWith("write")) {
				File file = new File(fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
				outputStreamWriter.write(string.substring(string.indexOf(".") + 1));
				outputStreamWriter.flush();
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
				dataOutputStream.writeUTF("ok");
			} else {
				dataOutputStream.writeUTF("fafafa");
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// if (!configManager.allowEdit) {
		// return;
		// }
	}
}