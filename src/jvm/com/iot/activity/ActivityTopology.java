package com.iot.activity;

import java.util.ArrayList;
import java.util.Properties;

import storm.kafka.bolt.KafkaBolt;
import storm.kafka.bolt.selector.DefaultTopicSelector;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

public class ActivityTopology {

	public static final int userCount = 4;

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		String kafkaTopicPrefix = "user";

		for (int i = 1; i <= userCount; i++) {
			builder.setSpout("spout" + i, new UserSpout(
					"./activity-dataset/task" + i,
					"./activity-storm-input/task" + i), 1);
		}

		ArrayList<KafkaBolt<String, String>> boltList = new ArrayList<KafkaBolt<String, String>>();

		for (int i = 1; i <= userCount; i++) {
			boltList.add(new KafkaBolt<String, String>().withTopicSelector(
					new DefaultTopicSelector(kafkaTopicPrefix + i + ""))
					.withTupleToKafkaMapper(
							new ActivityKafkaMapper<String, String>()));
			builder.setBolt("sendToKafka" + i, boltList.get(i - 1), 1)
					.shuffleGrouping("spout" + i);
		}

		Config conf = new Config();
		Properties props = new Properties();
		props.put("metadata.broker.list", "localhost:9092");
		props.put("request.required.acks", "1");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer",
				"org.apache.kafka.common.serialization.StringSerializer");
		props.put("metadata.fetch.timeout.ms", 1000);
		props.put("queued.max.requests", 1000000);
		props.put("linger.ms", 0);
		conf.put(KafkaBolt.KAFKA_BROKER_PROPERTIES, props);
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("testTopo", conf, builder.createTopology());
		Utils.sleep(1000000);
		cluster.shutdown();
	}
}
