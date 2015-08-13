package com.iot.activity;

public class MLTasksThread implements Runnable {
	private int userID;
	private String topic;
	private String delay;
	
	public static int numTasks = 4;

	public MLTasksThread(int userID, String topic, String delay) {
		this.userID = userID;
		this.topic = topic;
		this.delay = delay;
	}

	@Override
	public void run() {
		ActivitySamoaMLTask ast = new ActivitySamoaMLTask(userID);
		ast.buildMLTaskFile(
				"org.apache.samoa.learners.classifiers.trees.VerticalHoeffdingTree -p 4",
				"org.apache.samoa.streams.kafka.KafkaStream", "20", "400000",
				"1000", topic, delay);
		ast.launch();
	}

	public static void main(String[] args) {
		Thread[] thread = new Thread[numTasks + 1];
		int delay = 1000;
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
	}
}
