package com.iot.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActivityDatasetParser {

	// public static String BASE_INPUT_PATH = "./activity-dataset";
	// public static String BASE_OUTPUT_PATH = "./activity-storm-input";

	public String baseInputPath;
	public String baseOutputPath;
	public static Map<String, Integer> map;
	public static int partCount = 1;
	public static final int activityCount = 6;
	public static final int inputFileCount = 5;
	public static final int instancesPerFile = 1000000;

	public ActivityDatasetParser(String baseInputPath, String baseOutputPath) {
		this.baseInputPath = baseInputPath;
		this.baseOutputPath = baseOutputPath;

	}

	public ArrayList<String> getFileNames(String path) {
		ArrayList<String> fileNames = new ArrayList<String>();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()
					&& !listOfFiles[i].toString().contains("DS_Store")) {

				fileNames.add(listOfFiles[i].toString());
				System.out.println(listOfFiles[i].toString());
			}
		}
		return fileNames;
	}

	public void processFiles(String dataFile, String outputFile) {

		System.out.println(dataFile + " " + outputFile);
		try {
			FileInputStream fstream;
			fstream = new FileInputStream(dataFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fstream));

			File file = new File(baseOutputPath + "/" + outputFile);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			String strLine, outLine = "";
			int lineCount = 0;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {

				String[] tokens = strLine.split(" ");

				String activityID = tokens[1];
				outLine = tokens[3];
				for (int i = 4; i < tokens.length; i++) {
					outLine = outLine + "," + tokens[i];
				}

				if (outLine.contains("NaN") == true
						|| Integer.parseInt(activityID) == 0
						|| Integer.parseInt(activityID) > activityCount)
					continue;

				outLine = outLine + "," + activityID;
				lineCount++;

				if (lineCount % instancesPerFile == 0) {
					bw.close();
					file = new File(baseOutputPath + "/Part-" + partCount + "-"
							+ outputFile);
					partCount++;
					fw = new FileWriter(file.getAbsoluteFile());
					bw = new BufferedWriter(fw);
				}
				bw.write(outLine);
				bw.write("\n");
				addToMap(activityID);
			}
			System.out.println(lineCount);
			// Close the input stream
			br.close();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addToMap(String key) {
		if (map.containsKey(key) == true) {
			map.put(key, map.get(key).intValue() + 1);
		} else {
			map.put(key, 1);
		}
	}

	public void printMap() {
		int totalData = 0;
		Set<String> keys = map.keySet();
		for (String k : keys) {
			int value = map.get(k);
			totalData = totalData + value;
			System.out.println("Key:" + k + " Value:" + value);
		}
		System.out.println("Total number of instances:" + totalData);

	}

	public void processInput() {

		ArrayList<String> fileNames = getFileNames(baseInputPath);
		map = new HashMap<String, Integer>();
		for (int i = 0; i < fileNames.size(); i++) {
			String outputFile = fileNames.get(i).replace(baseInputPath + "/",
					"");
			outputFile = "Processed_" + outputFile;
			processFiles(fileNames.get(i), outputFile);
		}
	}

	public static int lines = 0;

	public static void main(String[] args) {
		for (int i = 0; i < 4; i++) {
			ActivityDatasetParser one = new ActivityDatasetParser(
					"./activity-dataset/task"+(i+1), "./activity-storm-input/task"+(i+1));
			one.processInput();
			one.printMap();
		}
	}

}
