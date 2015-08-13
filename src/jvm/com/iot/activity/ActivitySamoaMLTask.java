package com.iot.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ActivitySamoaMLTask {

	// Samoa base path
	public static String SAMOA_BASE_PATH = "PATH-TO-SAMOA";

	public static String OUTPUT_PATH = "/tmp/dump.csv";
	public static String MODE = "local";
	private int userID;

	public ActivitySamoaMLTask(int userID) {
		this.userID = userID;
	}

	public static void main(String[] args) {
		ActivitySamoaMLTask one = new ActivitySamoaMLTask(1);
		one.buildMLTaskFile(
				"org.apache.samoa.learners.classifiers.trees.VerticalHoeffdingTree -p 4",
				"org.apache.samoa.streams.kafka.KafkaStream", "10", "400000",
				"10000", "task1", "5000");
		one.launch();
	}

	public void buildMLTaskFile(String learner, String stream, String maxReads,
			String trainNum, String testNum, String topic, String delay) {
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("User" + userID + ".sh"), "utf-8"));
			writer.write("#!/bin/bash");
			writer.write("\n");
			writer.write("cd" + " " + SAMOA_BASE_PATH);
			writer.write("\n");
			writer.write("ls");
			writer.write("\n");
			writer.write("bin/samoa local target/SAMOA-Local-0.3.0-incubating-SNAPSHOT.jar \"PrequentialEvaluation -d /tmp/dump.csv "
					+ "-l ("
					+ learner
					+ ") "
					+ "-s ("
					+ stream
					+ " "
					+ "-r "
					+ maxReads
					+ " "
					+ "-t "
					+ topic
					+ ") "
					+ "-i "
					+ trainNum
					+ " " + "-f " + testNum + " " + "-w " + delay + "\"");

		} catch (IOException ex) {
			// report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {/* ignore */
			}
		}

	}

	public void launch() {

		Process p = null;
		BufferedReader br = null;
		ProcessHandler ph = null;

		try {
			String[] cmd = new String[] { "/bin/sh", "User" + userID + ".sh" };
			p = Runtime.getRuntime().exec(cmd);
			System.out.println("\nLaunching task..");
			ph = new ProcessHandler(p.getInputStream(), userID);
			ph.start();
			p.waitFor();
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}

			if (ph != null) {
				ph.done = true;
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		} finally {
			if (p != null) {
				p.destroy();
			}

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}

}
