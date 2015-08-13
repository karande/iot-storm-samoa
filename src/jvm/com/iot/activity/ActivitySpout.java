package com.iot.activity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ActivitySpout extends BaseRichSpout {
	private static final long serialVersionUID = 1L;
	SpoutOutputCollector _collector;
	TopologyContext context;
	FileReader[] filereader;
	BufferedReader[] br;
	String line;
	public static int lineCount;
	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		_collector = collector;
		lineCount = 0 ;

		try {
			this.context = context;
			ArrayList<String> fileNames = new ArrayList<String>();

			//ActivityDatasetParser.processInput();
			fileNames = null;

			filereader = new FileReader[fileNames.size()];
			br = new BufferedReader[fileNames.size()];

			for (int i = 0; i < fileNames.size(); i++) {
				System.out.println(i + ":" + fileNames.get(i).toString());
				this.filereader[i] = new FileReader(fileNames.get(i).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nextTuple() {
		try {
			for (int i = 0; i < br.length; i++) {
				br[i] = new BufferedReader(filereader[i]);
				line = br[i].readLine();
				
				while (line != null) {
					lineCount++;
					if(lineCount > 0)
						_collector.emit(new Values(line));
					line = br[i].readLine();
				}
				// br[i].close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("fileline"));

	}

}
