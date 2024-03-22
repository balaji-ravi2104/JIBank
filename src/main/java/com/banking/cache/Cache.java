package com.banking.cache;

import com.banking.utils.CustomException;

public interface Cache<K, V> {

	void set(K key, V value) throws CustomException;

	V get(String key) throws CustomException;

	void rem(String key);

	void close();
}
