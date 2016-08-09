package com.playtech.summerinternship;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetDataPoints {

	private String pattern;
	private long start;
	private long end;

	public GetDataPoints(String pattern, long start, long end) {
		super();
		this.pattern = pattern;
		this.start = start;
		this.end = end;
	}

	/**
	 * This method is used to convert "pattern" to regular expression which Java
	 * can understand, and match local files to it. Files can match the
	 * expression will be used to create DataForQuery class and be added to
	 * ArrayList<DataForQuery>. If no file matches, it will return a ArrayList
	 * with an empty DataForQuery class.
	 * 
	 * @return an ArrayList<DataForQuery> which will be used to generate an json
	 *         array and be sent back later
	 */
	public ArrayList<DataForQuery> findFile() {
		// format pattern
		pattern = "data\\." + pattern.replace(".", "\\.").replace("?", ".").replace("*", ".*");
		Pattern p = Pattern.compile(pattern);
		// get all file names
		ArrayList<String> fileNames = getAllFileName();
		// this array list contains file names which match the expression
		ArrayList<String> queryFile = new ArrayList<String>();
		// this array list contains DataForQuery which will be returned
		ArrayList<DataForQuery> dataList = new ArrayList<DataForQuery>();
		for (String string : fileNames) {
			string = string.replace("\\", ".");
			Matcher m = p.matcher(string);
			if (m.matches()) {
				queryFile.add(string);
			}
		}
		fileNames = null;

		if (queryFile.size() < 1) {
			dataList.add(new DataForQuery());
		} else {
			for (String string : queryFile) {
				// System.out.println("match: " + string); // test
				dataList.add(getData(string, start, end));
			}
		}
		return dataList;
	}

	/**
	 * Call listDirectory(dir) method and read "FileList" into an ArrayList
	 * <String> for later use
	 * 
	 * @return the ArrayList of all file names
	 */
	public static ArrayList<String> getAllFileName() {
		ArrayList<String> fileNames = new ArrayList<String>();
		try {
			// if there is a FileList already, delete it before list
			// the directory
			File fileList = new File("data/FileList");
			if (fileList.exists()) {
				fileList.delete();
			}
			listDirectory(new File("data"));

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileList)));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					fileNames.add(line);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileNames.remove("data\\FileList");
		return fileNames;
	}

	/**
	 * List directory contents and write the all the file paths into "FileList"
	 * file
	 * 
	 * @param the
	 *            directory you want to list
	 * @throws IOException
	 */
	public static void listDirectory(File dir) throws IOException {
		if (!dir.exists()) {
			throw new IllegalArgumentException("Directory: " + dir + " does not exist.");
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(dir + " is not a directory.");
		}

		// create a file list to store files' name
		File fileList = new File("data/FileList");
		FileWriter fileWriter = new FileWriter(fileList, true);
		PrintWriter pw = new PrintWriter(fileWriter);

		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isDirectory()) {
					listDirectory(file);
				} else {
					pw.println(file);
					pw.flush();
				}
			}
		}
		fileWriter.close();
		pw.close();
	}

	/**
	 * get a DataForQuery class which can be used to generate a json string
	 * later.
	 * 
	 * @param the
	 *            path of file. NOTICE: For neatness, I created a directory
	 *            "data" to store all the files and directories, this identifier
	 *            should be removed when creating json
	 * @param start
	 *            start timestamp
	 * @param end
	 *            end timestamp
	 * @return
	 */
	public static DataForQuery getData(String name, long start, long end) {
		// get directory path and file path
		String[] pathArr = name.split("\\.");
		// this is directory path
		String dirPath = "data/";
		// this is used to create DataForQuery class which doesn't contain
		// prefix "data"
		String fileName = "";
		// convert path string to what Java can read
		for (int i = 1; i < pathArr.length - 2; i++) {
			dirPath = dirPath + pathArr[i] + "/";
			fileName = fileName + pathArr[i] + ".";
		}
		String filePath = dirPath + pathArr[pathArr.length - 2] + "." + pathArr[pathArr.length - 1];
		fileName = fileName + pathArr[pathArr.length - 2] + "." + pathArr[pathArr.length - 1];

		// read data from local stored files and use them to generate
		// DataForQuery class, this class will be returned and be used to
		// create json string
		ArrayList<long[]> dataPointList = new ArrayList<long[]>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					String[] dataPointStrings = line.split(" ");
					long timestamp = Long.parseLong(dataPointStrings[0]);
					if (timestamp >= start && timestamp <= end) {
						long[] dataPoint = { timestamp, Long.parseLong(dataPointStrings[1]) };
						dataPointList.add(dataPoint);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new DataForQuery(fileName, dataPointList);

	}
}
