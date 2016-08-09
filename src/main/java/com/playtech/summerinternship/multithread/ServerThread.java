package com.playtech.summerinternship.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * multi-thread version server has not been completed, it can receive and store the 
 * raw data from port, but there are still problems about aggregation
 */
public class ServerThread extends Thread {
	private Socket socket = null;
	
	
	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		File file = null;
		FileWriter fw = null;
		PrintWriter pw = null;
		
		String filePath = "data2/";
		String dirPath = "data2";
		try {
			String[] dataArr = null;
			is = socket.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String info = null;
			while ((info = br.readLine()) != null) {
				dataArr = info.split(" ");
				String[] dirArr = dataArr[0].split("\\.");
				for (int i = 0; i < dirArr.length - 1; i++) {
					dirPath = dirPath + "/" + dirArr[i];
				}
				filePath = filePath + dataArr[0].replace(".", "/");
			}
			socket.shutdownInput();

			// store input message into local files
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			file = new File(filePath);
			fw = new FileWriter(file, true);
			pw = new PrintWriter(fw);
			pw.println(dataArr[0] + " " + dataArr[1] + " " + dataArr[2]);
			pw.flush();
				
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
				if (pw != null)
					pw.close();
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
				if (is != null)
					is.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
