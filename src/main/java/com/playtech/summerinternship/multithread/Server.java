package com.playtech.summerinternship.multithread;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * I am planning to use multi-thread in server side, but the timing problem 
 * have not been solved. 
 */
public class Server {
	public static void main(String[] args) {
		try {
			
			/*  
			 * Set Listen port
			 * Since ServerSocket is in an endless loop to wait for messages, 
			 *  it will never close.
			 */
			ServerSocket serverSocket = new ServerSocket(8888);
			Socket socket=null;
			System.out.println("***server is running***");
			while(true){
				socket=serverSocket.accept();
				ServerThread serverThread=new ServerThread(socket);
				serverThread.start();
				
				InetAddress address=socket.getInetAddress();
				System.out.println("Client IP："+address.getHostAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
