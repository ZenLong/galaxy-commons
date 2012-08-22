package com.saysth.commons.redis.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import redis.clients.jedis.Jedis;

public class RedisList<E extends Serializable> extends AbstractRedisCollection {

	public RedisList(String collectionName) {
		super(collectionName);
	}

	public int size() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			Long length = jedis.llen(getCollectionName());
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

	public boolean add(E e) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.rpush(getCollectionName(), serialize(e));
			return true;
		} finally {
			returnJedis(jedis);
		}
	}

	public boolean remove(Object o) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.lrem(getCollectionName(), 0, serialize(o));
			return true;
		} finally {
			returnJedis(jedis);
		}
	}

	public boolean addAll(Collection<? extends E> c) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			for (E e : c) {
				jedis.rpush(getCollectionName(), serialize(e));
			}
			return true;
		} finally {
			returnJedis(jedis);
		}
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			int i = 0;
			Iterator<? extends E> iter = c.iterator();
			while (iter.hasNext()) {
				E e = iter.next();
				if (++i >= index) {
					jedis.rpush(getCollectionName(), serialize(e));
				}
			}
			return true;
		} finally {
			returnJedis(jedis);
		}
	}

	public boolean removeAll(Collection<?> c) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			for (Object o : c) {
				jedis.lrem(getCollectionName(), 0, serialize(o));
			}
			return true;
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
	public E get(int index) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			byte[] bytes = jedis.lindex(getCollectionName(), index);
			return bytes != null && bytes.length > 0 ? (E) deserialize(bytes) : null;
		} finally {
			returnJedis(jedis);
		}
	}

	public E set(int index, E element) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.lset(getCollectionName(), index, serialize(element));
			return element;
		} finally {
			returnJedis(jedis);
		}
	}

	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	public List<E> subList(int fromIndex, int toIndex) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			List<byte[]> bytesList = jedis.lrange(getCollectionName(), fromIndex, toIndex);
			List<E> values = new ArrayList<E>();
			for (byte[] bytes : bytesList) {
				values.add((E) deserialize(bytes));
			}
			return values;
		} finally {
			returnJedis(jedis);
		}
	}

}
