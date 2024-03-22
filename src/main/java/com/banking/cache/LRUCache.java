package com.banking.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import com.banking.utils.CustomException;

public class LRUCache<K, V> extends LinkedHashMap<K, V> implements Cache<K, V> {

	private static final long serialVersionUID = 1L;
	private final int capacity;

	public LRUCache(int capacity) {
		super(capacity, 0.75f, true);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > capacity;
	}

	@Override
	public void set(K key, V value) {
		put(key, value);
	}

	@Override
	public void rem(String key) {
		super.remove(key);
	}

	@Override
	public void close() {
		super.clear();
	}

	@Override
	public V get(String string) throws CustomException {
		return super.get(string);
	}

}