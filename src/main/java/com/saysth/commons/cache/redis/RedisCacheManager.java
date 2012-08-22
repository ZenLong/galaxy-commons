package com.saysth.commons.cache.redis;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

import redis.clients.jedis.JedisPool;

/**
 * Redis Cache Manager
 * 
 * @author
 * 
 */
public class RedisCacheManager extends AbstractCacheManager {
	private JedisPool jedisPool;
	private int dbIndex;

	@Override
	protected Collection<? extends Cache> loadCaches() {
		String[] names = { "default" };
		Collection<Cache> caches = new LinkedHashSet<Cache>(names.length);
		for (String name : names) {
			caches.add(buildCache(name));
		}
		return caches;
	}

	@Override
	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		if (cache == null) {
			cache = buildCache(name);
			super.addCache(cache);
		}
		return cache;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public void setDbIndex(int dbIndex) {
		this.dbIndex = dbIndex;
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(jedisPool, "jedisPool must not be null.");
		super.afterPropertiesSet();
	}

	/**
	 * Build a cache
	 * @param cacheName - a name for this cache
	 * @return <code>RedisCache</code>
	 */
	public RedisCache buildCache(String cacheName) {
		RedisCache redisCache = new RedisCache(cacheName);
		redisCache.setJedisPool(jedisPool);
		redisCache.setDbIndex(dbIndex);
		return redisCache;
	}

}
