package com.saysth.commons.unit.redis;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.saysth.commons.redis.support.RedisDao;

public class PersonDao extends RedisDao<Person> {
	private static final Logger logger = LoggerFactory.getLogger(PersonDao.class);

	@Test
	public void save(Person person) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			jedis.hset(regionName, person.getId(), serialize(person));
		} catch (Throwable e) {
			logger.error("Can't add object to redis.", e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Test
	public Person get(String id) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			String value = jedis.hget(regionName, id);
			return deserialize(value);
		} catch (Throwable e) {
			logger.error("Can't get object from redis.", e);
			return null;
		} finally {
			returnJedis(jedis);
		}

	}
}
