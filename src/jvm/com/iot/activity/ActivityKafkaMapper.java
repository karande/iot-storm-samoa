package com.iot.activity;

import backtype.storm.tuple.Tuple;
import storm.kafka.bolt.mapper.TupleToKafkaMapper;

public class ActivityKafkaMapper<K, V> implements TupleToKafkaMapper<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static long sequence = 0;

	@SuppressWarnings("unchecked")
	@Override
	public K getKeyFromTuple(Tuple arg0) {
		sequence++;
		String key = sequence + "";
		return (K) key;
	}

	@SuppressWarnings("unchecked")
	@Override
	public V getMessageFromTuple(Tuple arg0) {

		String instance = arg0.toString();
		String value = "";

		String[] tokens = instance.split(", ");
		if (tokens.length > 0) {
			value = tokens[3].toString().replace("[", "").replace("]", "");
		}
		return (V) value;
	}

}
