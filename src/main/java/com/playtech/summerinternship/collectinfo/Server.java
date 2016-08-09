package com.playtech.summerinternship.collectinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.playtech.summerinternship.Data;

/**
 * This is the server of port listening program. I use socket to receive
 * messages from client side. To make the data reliable, I use TCP protocol in
 * the transmitting process. If we want to make it more efficient but less
 * reliable, UDP can be used.
 * 
 * P.S. I was planning to use multi-thread with socket to make the listening
 * process more efficient, but there are some problems I have no time to solve:
 * The main one is about how to count 1 second/minute. If I set a timer on the
 * server, there can be some errors or delay between server and client; I have
 * also considered about the way which used in this single-thread method but
 * with multi-thread, data may not be received in time order. But I have
 * finished half of the work and them are in
 * com.playtech.summerinternship.multithread package.
 * 
 * @author Zhenyu Wu
 *
 */
public class Server {

	/**
	 * In main method I put startServer method into an endless loop so that
	 * we can keep listening the designated port. 
	 * I use prevSecTimeStamp to remember the timestamp of last second,
	 * secTimeStamp to remember the timestamp of current second, and compare
	 * them in every single loop. If they are different, it means the second
	 * has changed, we can start to aggregate data and write them into final
	 * file. The same way is applied to every minute data.
	 * Here I use a HashSet to store intermediary file name for every second/
	 * minute. When second/minute get changed, call doCalculate method to
	 * aggregate raw data and clear the set.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Server server = new Server();
			// set the port to listen
			ServerSocket serverSocket = new ServerSocket(8888);
			System.out.println("*****Server is running, waiting for message: *****");

			String prevSecTimeStamp = "";
			String secTimeStamp = null;
			String prevMinTimeStamp = "";
			String minTimeStamp = null;
			Set<String> secPathCache = new HashSet<String>();
			Set<String> minPathCache = new HashSet<String>();

			while (true) {
				// start server
				Data data = server.startServer(serverSocket);

				
				// create timestamp for each second and minute
				Date date = new Date(Long.parseLong(data.getTimeStamp()));
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				long sec = c.get(Calendar.SECOND);
				secTimeStamp = data.getTimeStamp().substring(0, 10) + "000";
				minTimeStamp = (Long.parseLong(secTimeStamp) - sec * 1000) + "";

				// if time changed to next second, calculate values of last
				// second
				if (!prevSecTimeStamp.equals("") && !secTimeStamp.equals(prevSecTimeStamp)) {
					doCalculate(secPathCache, ".1Second");
					secPathCache.clear();
					
				}

				// if time changed to next minute, calculate values of last
				// minute
				if (!prevMinTimeStamp.equals("") && !minTimeStamp.equals(prevMinTimeStamp)) {
 					doCalculate(minPathCache, ".1Minute");
 					minPathCache.clear();
				}
				secPathCache.add(data.getSecFilePath());
				minPathCache.add(data.getMinFilePath());
				prevSecTimeStamp = data.getTimeStamp().substring(0, 10) + "000";
				prevMinTimeStamp = (Long.parseLong(prevSecTimeStamp) - sec * 1000) + "";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to create a new socket to receive data from 
	 * client. But the data will not be store in the final aggregated form.
	 * Data will be store by path and timestamp of second/minute, but 
	 * average value and max value will not be calculated immediately.
	 * For example "local.random.diceroll 123 1461140337012", will be written 
	 * into following files:
	 * local/random/diceroll.sec.1461140337000
	 * local/random/diceroll.min.1461140220000
	 * and raw data will be written into both of them
	 * 
	 * @param serverSocket 
	 * @return this method return a Data type class, which is a bean class
	 * 			to store received data.
	 */
	public Data startServer(ServerSocket serverSocket) {
		Data data = new Data();
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		Socket socket = null;
		File secFile = null;
		File minFile = null;
		FileWriter secFW = null;
		FileWriter minFW = null;
		PrintWriter secPW = null;
		PrintWriter minPW = null;

		String[] dataArr = null;
		String secTimeStamp = "";
		String minTimeStamp = "";
		String filePath = "data/";
		String dirPath = "data";

		try {
			socket = serverSocket.accept();
			is = socket.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String info = null;
			while ((info = br.readLine()) != null) {
				// get file path which java can understand
				dataArr = info.split(" ");
				String[] dirArr = dataArr[0].split("\\.");
				for (int i = 0; i < dirArr.length - 1; i++) {
					dirPath = dirPath + "/" + dirArr[i];
				}
				filePath = filePath + dataArr[0].replace(".", "/");

				// truncate timestamp to second precision and minute precision
				Date date = new Date(Long.parseLong(dataArr[2]));
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				long sec = c.get(Calendar.SECOND);
				secTimeStamp = dataArr[2].substring(0, 10) + "000";
				minTimeStamp = (Long.parseLong(secTimeStamp) - sec * 1000) + "";
			}
			socket.shutdownInput();

			// store input message into local files
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			secFile = new File(filePath + ".sec." + secTimeStamp);
			minFile = new File(filePath + ".min." + minTimeStamp);
			secFW = new FileWriter(secFile, true);
			minFW = new FileWriter(minFile, true);
			secPW = new PrintWriter(secFW);
			minPW = new PrintWriter(minFW);
			secPW.println(dataArr[0] + " " + dataArr[1] + " " + dataArr[2]);
			secPW.flush();
			minPW.println(dataArr[0] + " " + dataArr[1] + " " + dataArr[2]);
			minPW.flush();
			data.setSecFilePath(filePath + ".sec." + secTimeStamp);
			data.setMinFilePath(filePath + ".min." + minTimeStamp);
			data.setPath(dataArr[0]);
			data.setValue(Integer.parseInt(dataArr[1]));
			data.setTimeStamp(dataArr[2]);
			
			secFW.close();
			minFW.close();
			secPW.close();
			minPW.close();
			br.close();
			isr.close();
			is.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * Calculate max value and average value for corresponding second/minute
	 * 
	 * @param pathSet a Set of file names, contains all files created in last
	 * 					second/minute
	 * @param postfix this postfix is used to compose real file name
	 */
	public static void doCalculate(Set<String> pathSet, String postfix) {
		
		Iterator<String> it = pathSet.iterator();  
		while (it.hasNext()) {  
			String fileName = it.next(); 
			BufferedReader br = null;
			int maxValue = 0;
			int avgValue = 0;
			int sum = 0;
			int count = 0;
			
			// get file paths
			String[] nameArr = fileName.split("\\.");
			String maxFilePath = nameArr[0] + postfix + "Max";
			String avgFilePath = nameArr[0] + postfix + "Avg";
			String timestamp = nameArr[nameArr.length - 1];
			
			try {
				// read raw data from intermediary file and written them
				// into aggregated file
				br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
				String line;
				while ((line = br.readLine()) != null) {
					if(!line.equals("")) {
						String[] strArr = line.split(" ");
						int value = Integer.parseInt(strArr[1]);
						sum += value;
						if(maxValue < value) {
							maxValue = value;
						}
						count++;
					}
				}
				avgValue = sum / count;
				File maxFile = new File(maxFilePath);
				File avgFile = new File(avgFilePath);
				FileWriter maxFW = new FileWriter(maxFile, true);
				FileWriter avgFW = new FileWriter(avgFile, true);
				PrintWriter maxPW = new PrintWriter(maxFW);
				PrintWriter avgPW = new PrintWriter(avgFW);
				maxPW.println(timestamp + " " + maxValue);
				maxPW.flush();
				avgPW.println(timestamp + " " + avgValue);
				avgPW.flush();
				
				maxFW.close();
				avgFW.close();
				maxPW.close();
				avgPW.close();
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}  
	}
}
