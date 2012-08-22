package com.saysth.commons.dao.mongo;

import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.Mongo;

/**
 * @author
 * 
 */
public class MongoHelper implements InitializingBean {
	@Autowired
	private MongoFactoryBean mongoFactory;
	private Mongo mongo;
	private MongoInfo mongoInfo;

	public void upgrade() {
		DB db = mongo.getDB(mongoInfo.getDatabaseName());
		Set<String> collectionNames = db.getCollectionNames();
		for (String collectionName : collectionNames) {
			upgradeCollectionData(db.getCollection(collectionName));
		}
	}

	private void upgradeCollectionData(DBCollection dbCollection) {
		DBCursor cur = dbCollection.find();
		while (cur.hasNext()) {
			DBObject dbObject = cur.next();
			DBObject newDBObject = new BasicDBObject();
			for (String key : dbObject.keySet()) {
				Object newValue = getNewValue(dbObject.get(key));
				newDBObject.put(key, newValue);
			}
			dbCollection.update(dbObject, newDBObject, false, false);
		}
	}

	private Object getNewValue(Object value) {
		if (value instanceof ObjectId) {
			return ((ObjectId) value).toString();
		} else if (value instanceof DBRef) {
			DBRef dbref = new DBRef(null, ((DBRef) value).getRef(), ((DBRef) value).getId().toString());
			return dbref;
		} else if (value instanceof BasicDBList) {
			BasicDBList dbList = (BasicDBList) value;
			BasicDBList newList = new BasicDBList();
			for (Object element : dbList) {
				newList.add(getNewValue(element));
			}
			return newList;
		} else if (value instanceof BasicDBObject) {
			BasicDBObject dbObject = (BasicDBObject) value;
			DBObject newDBObject = new BasicDBObject();
			for (String key : dbObject.keySet()) {
				Object newValue = getNewValue(dbObject.get(key));
				newDBObject.put(key, newValue);
			}
			return newDBObject;
		} else {
			return value;
		}
	}

	/**
	 * @return the mongoInfo
	 */
	public MongoInfo getMongoInfo() {
		return mongoInfo;
	}

	public String getDriverVersion() {
		return mongo.getVersion();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		mongo = mongoFactory.getMongo();
		mongoInfo = new MongoInfo();
		mongoInfo.setVersion(getVersion());
		mongoInfo.setHost(mongo.getAddress().getHost());
		mongoInfo.setPort(mongo.getAddress().getPort());
		mongoInfo.setDatabaseName(mongoFactory.getDatabase());
	}

	private String getVersion() {
		DB adminDb = mongo.getDB("admin");
		CommandResult result = adminDb.command("buildinfo");
		return result.getString("version");
	}
}
