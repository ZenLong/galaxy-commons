package com.saysth.commons.redis.support;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.saysth.commons.utils.ThreadUtils;

public class RedisStorage2 {
	private static final Logger logger = LoggerFactory.getLogger(RedisStorage2.class);
	/**
	 * redis连接缓存池
	 */
	private JedisPool jedisPool;
	/**
	 * redis数据库索引号
	 */
	private int dbIndex;

	/**
	 * 获取redis连接
	 * 
	 * @return conn
	 */
	/**
	 * 获取redis连接
	 * 
	 * @return conn
	 */
	public Jedis getJedis() {
		Jedis jedis = null;
		int times = 0;// 获取连接次数
		int repeats = 2;// 重复次数
		Exception exception = null;
		while (jedis == null) {
			try {
				times++;
				jedis = jedisPool.getResource();
			} catch (Exception e) { // 捕捉异常
				exception = e;
				if (times <= repeats) {
					ThreadUtils.sleep(1000);
				} else {
					break;
				}
			}
		}
		if (jedis != null) {
			jedis.select(dbIndex);
		} else {
			logger.error("get jedist from jedis pool error.", exception);
		}
		return jedis;
	}

	/**
	 * 销毁连接缓冲池
	 */
	public void destroyJedisPool() {
		if (jedisPool != null) {
			logger.info("destroy jedis pool.");
			jedisPool.destroy();
			jedisPool = null;
		}
	}

	/**
	 * 释放redis连接
	 * 
	 * @param conn
	 */
	public void returnJedis(Jedis jedis) {
		if (null != jedis) {
			try {
				jedisPool.returnResource(jedis);
			} catch (Exception e) {
				logger.error("return jedist to jedis pool error.", e);
			}
		}
	}

	/**
	 * 设置连接池
	 * 
	 */
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	/**
	 * 设置redis数据库索引号
	 * 
	 * @param dbIndex
	 *            the dbIndex to set
	 */
	public void setDbIndex(int dbIndex) {
		this.dbIndex = dbIndex;
	}

	/**
	 * @return the jedisPool
	 */
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	/**
	 * @return the dbIndex
	 */
	public int getDbIndex() {
		return dbIndex;
	}

	protected String serialize(Object obj) {
		return JSON.toJSONString(obj, SerializerFeature.WriteClassName);
	}

	@SuppressWarnings("unchecked")
	protected <T> T deserialize(String json) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		return (T) JSON.parse(json);
	}
}
