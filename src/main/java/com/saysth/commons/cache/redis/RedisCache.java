package com.saysth.commons.cache.redis;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import redis.clients.jedis.Jedis;

import com.saysth.commons.redis.support.RedisStorage2;

/**
 * A redis-based cache
 * 
 * @author
 * 
 */
public class RedisCache extends RedisStorage2 implements Cache {
	private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

	private String cacheName;

	public RedisCache(String cacheName) {
		this.cacheName = cacheName;
	}

	@Override
	public String getName() {
		return this.cacheName;
	}

	@Override
	public Object getNativeCache() {
		return null;
	}

	@Override
	public ValueWrapper get(Object key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			String json = jedis.hget(cacheName, key.toString());
			Object value = StringUtils.isBlank(json) ? null : deserialize(json);
			return (value != null ? new SimpleValueWrapper(value) : null);
		} catch (Throwable e) {
			logger.error("Can't get from redis cache.", e);
		} finally {
			returnJedis(jedis);
		}
		return null;
	}

	@Override
	public void put(Object key, Object value) {
		if (value == null) {
			return;
		}
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hset(cacheName, key.toString(), serialize(value));
		} catch (Throwable e) {
			logger.error("Can't put object to redis cache.", e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public void evict(Object key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hdel(cacheName, key.toString());
		} catch (Throwable e) {
			logger.error("Can't evict object from redis cache.", e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public void clear() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.del(cacheName);
		} catch (Throwable e) {
			logger.error("Can't clear redis cache.", e);
		} finally {
			returnJedis(jedis);
		}
	}

}
