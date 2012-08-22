package com.saysth.commons.redis.support;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.saysth.commons.utils.ThreadUtils;
import com.saysth.commons.utils.reflection.ReflectionUtils;

public class RedisDao<T extends Serializable> {
	private static final Logger logger = LoggerFactory.getLogger(RedisDao.class);
	// 实体类的类对象
	protected Class<T> entityClass;
	protected String regionName;
	/**
	 * redis连接缓存池
	 */
	private JedisPool jedisPool;
	/**
	 * redis数据库索引号
	 */
	private int dbIndex;

	public RedisDao() {
		entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
		regionName = entityClass.getName();
	}

	/**
	 * 获取redis连接
	 * 
	 * @return conn
	 */
	public Jedis getJedis() {
		Jedis jedis = null;
		// 捕捉异常
		try {
			jedis = jedisPool.getResource();
		} catch (Exception e) {
			logger.error("get jedist from jedis pool error.", e);
			ThreadUtils.sleep(1000);
			jedis = jedisPool.getResource();
		}
		if (jedis != null) {
			jedis.select(dbIndex);
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

	protected String serialize(T t) {
		return JSON.toJSONString(t);
	}

	protected T deserialize(String json) {
		if (StringUtils.isBlank(json)) {
			return null;
		}
		return JSON.parseObject(json, entityClass);
	}
}
