package com.saysth.commons.redis.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;

/**
 * @author
 * 
 */
public class RedisMap<K extends Serializable, V extends Serializable> extends AbstractRedisCollection {

	public RedisMap(String collectionName) {
		super(collectionName);
	}

	public int size() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Long length = jedis.hlen(getCollectionName());
			return length != null ? length.intValue() : 0;
		} finally {
			returnJedis(jedis);
		}
	}

	public boolean isEmpty() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.exists(getCollectionName());
		} finally {
			returnJedis(jedis);
		}
	}

	public boolean containsKey(K key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hexists(getCollectionName(), serialize(key));
		} finally {
			returnJedis(jedis);
		}
	}

	@SuppressWarnings("unchecked")
	public V get(K key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			byte[] bytes = jedis.hget(getCollectionName(), serialize(key));
			return bytes != null && bytes.length > 0 ? (V) deserialize(bytes) : null;
		} finally {
			returnJedis(jedis);
		}
	}

	public void put(K key, V value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hset(getCollectionName(), serialize(key), serialize(value));
		} finally {
			returnJedis(jedis);
		}
	}

	public void remove(K key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hdel(getCollectionName(), serialize(key));
		} finally {
			returnJedis(jedis);
		}
	}

	public void putAll(Map<K, V> m) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			for (Map.Entry<K, V> entry : m.entrySet()) {
				jedis.hset(getCollectionName(), serialize(entry.getKey()), serialize(entry.getValue()));
			}

		} finally {
			returnJedis(jedis);
		}

	}

	public void clear() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.del(getCollectionName());
		} finally {
			returnJedis(jedis);
		}
	}

	@SuppressWarnings("unchecked")
	public Set<K> keySet() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Map<byte[], byte[]> bytesMap = jedis.hgetAll(getCollectionName());
			Set<K> k = new HashSet<K>();
			for (Map.Entry<byte[], byte[]> entry : bytesMap.entrySet()) {
				k.add((K) deserialize(entry.getKey()));
			}
			return k;
		} finally {
			returnJedis(jedis);
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<V> values() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			List<byte[]> bytesList = jedis.hvals(getCollectionName());
			Collection<V> values = new ArrayList<V>();
			for (byte[] bytes : bytesList) {
				values.add((V) deserialize(bytes));
			}
			return values;
		} finally {
			returnJedis(jedis);
		}
	}

	@SuppressWarnings("unchecked")
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Map<byte[], byte[]> bytesMap = jedis.hgetAll(getCollectionName());
			Map<K, V> m = new HashMap<K, V>();
			for (Map.Entry<byte[], byte[]> entry : bytesMap.entrySet()) {
				m.put((K) deserialize(entry.getKey()), (V) deserialize(entry.getValue()));
			}
			return m.entrySet();
		} finally {
			returnJedis(jedis);
		}
	}

}
