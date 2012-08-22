/**
 * 
 */
package com.saysth.commons.redis.support;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import redis.clients.jedis.JedisPool;

import com.saysth.commons.serializer.KryoTranscoder;
import com.saysth.commons.serializer.Transcoder;

/**
 * 
 * @author
 * 
 */
public final class RedisCollectionFactory implements InitializingBean {
	/**
	 * redis连接缓存池
	 */
	private JedisPool jedisPool;
	/**
	 * redis数据库索引号
	 */
	private int dbIndex;
	/**
	 * 序列化编码器
	 */
	private Transcoder transcoder = new KryoTranscoder();

	public <E extends Serializable> RedisList<E> newRedisList(String name) {
		RedisList<E> list = new RedisList<E>(name);
		initRedisConllection(list);
		return list;
	}

	public <K extends Serializable, V extends Serializable> RedisMap<K, V> newRedisMap(String name) {
		RedisMap<K, V> map = new RedisMap<K, V>(name);
		initRedisConllection(map);
		return map;
	}

	/**
	 * @param jedisPool
	 *            the jedisPool to set
	 */
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * @param dbIndex
	 *            the dbIndex to set
	 */
	public void setDbIndex(int dbIndex) {
		this.dbIndex = dbIndex;
	}

	/**
	 * @param transcoder
	 *            the transcoder to set
	 */
	public void setTranscoder(Transcoder transcoder) {
		this.transcoder = transcoder;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(jedisPool, "jedis pool cannot be null.");
	}

	private void initRedisConllection(AbstractRedisCollection redisCollection) {
		redisCollection.setJedisPool(jedisPool);
		redisCollection.setDbIndex(dbIndex);
		redisCollection.setTranscoder(transcoder);
	}

}
