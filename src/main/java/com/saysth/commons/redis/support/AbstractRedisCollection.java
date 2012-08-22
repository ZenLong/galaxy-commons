/**
 * 
 */
package com.saysth.commons.redis.support;

/**
 * @author
 * 
 */
public abstract class AbstractRedisCollection extends RedisStorage {
	private String collectionName;
	private byte[] collectionNameBytes;

	public AbstractRedisCollection(String collectionName) {
		this.collectionName = collectionName;
	}

	/**
	 * @return the collectionName
	 */
	protected byte[] getCollectionName() {
		if (collectionNameBytes == null) {
			collectionNameBytes = serialize(collectionName);
		}
		return collectionNameBytes;
	}
}
