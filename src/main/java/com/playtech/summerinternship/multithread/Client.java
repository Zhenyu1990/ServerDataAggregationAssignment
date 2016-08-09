package com.playtech.summerinternship.multithread;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is the client of port listening program. 
 * @author Zhenyu Wu
 *
 */
public class Client {
	
	/**
	 * Only for testing purposes
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client();
		for(int i = 0; i < 5; i++){
			String content = "local.random.diceroll1 " + (100 + i) + " " + 1461140237010L;
			client.startClient(content);
			System.out.println(content);
			content = "local.random.diceroll2 " + (200 + i) + " " + 1461140237010L;
			client.startClient(content);
			System.out.println(content);
			content = "local.random.diceroll3 " + (300 + i) + " " + 1461140237010L;
			client.startClient(content);
			System.out.println(content);
		}
		for(int i = 0; i < 5; i++){
			String content = "local.random.diceroll1 " + (101 + i) + " " + 1461140238020L;
			client.startClient(content);
			System.out.println(content);
			content = "local.random.diceroll2 " + (204 + i) + " " + 1461140238020L;
			client.startClient(content);
			System.out.println(content);
			content = "local.random.diceroll3 " + (305 + i) + " " + 1461140238040L;
			client.startClient(content);
			System.out.println(content);
		}
		for(int i = 0; i < 5; i++){
			String content = "local.random.diceroll1 " + (100 + i) + " " + 1461140337020L;
			client.startClient(content);
			System.out.println(content);
			content = "local.random.diceroll2 " + (200 + i) + " " + 1461140337020L;
			client.startClient(content);
			System.out.println(content);
			content = "local.random.diceroll3 " + (300 + i) + " " + 1461140337040L;
			client.startClient(content);
			System.out.println(content);
		}
	}
	
	/**
	 * Create a socket and send data to server
	 * @param content data to send
	 */
	public void startClient(String content) {
		try {
			// set ip address and port to send message
			Socket socket = new Socket("localhost", 8888);
			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os);
			pw.write(content);
			pw.flush();
			socket.shutdownOutput();
			pw.close();
			os.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}