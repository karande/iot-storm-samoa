package com.iot.activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class UserSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	SpoutOutputCollector _collector;
	TopologyContext context;
	FileReader[] filereader;
	BufferedReader[] br;
	String line;
	public static int lineCount;
	public int fileCount;
	public FileReader curFileReader;
	public BufferedReader curBufferedReader;
	public String baseInputPath;
	public String baseOutputPath;
	ActivityDatasetParser adp;
	public static int count = 0;
	public static int numTasks = 4;

	public UserSpout(String baseInputPath, String baseOutputPath) {
		this.baseInputPath = baseInputPath;
		this.baseOutputPath = baseOutputPath;

	}
	

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		_collector = collector;
		lineCount = 0;

		try {
			this.context = context;
			ArrayList<String> fileNames = new ArrayList<String>();
			adp = new ActivityDatasetParser(baseInputPath, baseOutputPath);
			adp.processInput();
			fileNames = adp.getFileNames(baseOutputPath);
			this.fileCount = 0;
			filereader = new FileReader[fileNames.size()];
			br = new BufferedReader[fileNames.size()];

			for (int i = 0; i < fileNames.size(); i++) {
				System.out.println(i + ":" + fileNames.get(i).toString());
				this.filereader[i] = new FileReader(fileNames.get(i).toString());
			}

			this.curFileReader = this.filereader[0];
			this.curBufferedReader = new BufferedReader(this.curFileReader);
			
			Thread[] thread = new Thread[numTasks + 1];
			int delay = 0;
			for (int i = 1; i < numTasks + 1; i++) {
				//delay = delay+ i*1000;
				MLTasksThread taskThread = new MLTasksThread(i, "task"+i,
						delay+"");
				thread[i] = new Thread(taskThread);
				thread[i].start();
			}

			for (int i = 1; i < numTasks + 1; i++) {
				try {
					thread[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nextTuple() {

		try {
			String line = this.curBufferedReader.readLine();
			if (line == null
					&& this.fileCount < adp.getFileNames(baseOutputPath).size()) {
				this.curBufferedReader.close();
				this.curFileReader = this.filereader[++this.fileCount];
				this.curBufferedReader = new BufferedReader(this.curFileReader);
			}
			count++;
			//System.out.println("Filecount" + this.fileCount + " " + count + " "
			//		+ line);
			_collector.emit(new Values(line));
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("fileline"));

	}

}
