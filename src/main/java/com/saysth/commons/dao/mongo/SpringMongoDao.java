package com.saysth.commons.dao.mongo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.IllegalClassException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Sort;
import org.springframework.util.Assert;

import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.saysth.commons.dao.BaseDao;
import com.saysth.commons.dao.Order;
import com.saysth.commons.dao.Page;
import com.saysth.commons.utils.reflection.ReflectionUtils;

/**
 * MongoDB Dao based on spring-data-mongodb API
 * 
 * @author
 * 
 * @param <T>
 * @param <PK>
 */
public class SpringMongoDao<T, PK extends Serializable> implements BaseDao<T, PK>, InitializingBean {

	// 实体类的缺省主键属性名,子类可重载getIdName方法返回自定义主键属性名
	public static final String DEFAULT_ID_NAME = "id";
	public static final int ASC = 1;// 升序(1)
	public static final int DESC = -1;// 降序(-1)

	// 实体类的类对象
	protected Class<T> entityClass;
	protected DBCollection dbCollection;
	protected Map<String, Order> indexes = new LinkedHashMap<String, Order>();
	protected Field idField;
	protected MongoTemplate mongoTemplate;
	protected String collectionName;

	/**
	 * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class.
	 * 例如: public class UserDao extends SpringMongoDao<User, Long>
	 */
	public SpringMongoDao() {
		entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * @return the entityClass
	 */
	public Class<T> getEntityClass() {
		return entityClass;
	}

	/**
	 * @return the dbCollection
	 */
	public String getTableName() {
		return dbCollection.getName();
	}

	/**
	 * 按id获取对象.
	 */
	public T get(PK id) {
		return mongoTemplate.findById(id, entityClass);
	}

	/**
	 * 按属性查找唯一对象.
	 */
	public T getByProperty(final String propName, final Serializable propValue) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).is(propValue));
		return getByQuery(query);
	}

	/**
	 * 按属性查找唯一对象.
	 */
	public T getByProperty(final String propName, final Serializable propValue, final Order... orders) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).is(propValue));
		if (orders != null && orders.length > 0) {
			Sort sort = query.sort();
			for (Order order : orders) {
				sort.on(order.getName(), order.isAsc() ? org.springframework.data.mongodb.core.query.Order.ASCENDING
						: org.springframework.data.mongodb.core.query.Order.DESCENDING);
			}
		}
		return getByQuery(query);
	}

	/**
	 * 按多个属性查找唯一对象.
	 */
	public T getByProperties(final Map<String, Serializable> properties) {
		Assert.notEmpty(properties);
		Query query = createQuery();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
		}
		return getByQuery(query);
	}

	/**
	 * 按多个属性查找唯一对象.
	 */
	public T getByProperties(final Map<String, Serializable> properties, final Order... orders) {
		Assert.notEmpty(properties);
		Query query = createQuery();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
		}
		if (orders != null && orders.length > 0) {
			Sort sort = query.sort();
			for (Order order : orders) {
				sort.on(order.getName(), order.isAsc() ? org.springframework.data.mongodb.core.query.Order.ASCENDING
						: org.springframework.data.mongodb.core.query.Order.DESCENDING);
			}
		}
		return getByQuery(query);
	}

	/**
	 * 按条件获取对象
	 * 
	 * @param query - 条件
	 * @return
	 */
	public T getByQuery(Query query) {
		Assert.notNull(query);
		return mongoTemplate.findOne(query, entityClass);
	}

	/**
	 * 保存对象
	 * 
	 * @param entity
	 */
	public void save(T entity) {
		Assert.notNull(entity);
		if (idField != null) {
			PK id = getId(entity);
			if (id == null && idField.getType().equals(String.class)) {
				try {
					PropertyUtils.setProperty(entity, idField.getName(), UUID.randomUUID().toString());
				} catch (Exception e) {
				}
			}
		}
		mongoTemplate.save(entity);
	}

	/**
	 * 删除对象
	 * 
	 * @param entity
	 */
	public void delete(T entity) {
		Assert.notNull(entity);
		mongoTemplate.remove(entity);
	}

	/**
	 * 按主键删除对象
	 * 
	 * @param id
	 */
	public void delete(PK id) {
		Assert.notNull(id);
		delete(this.get(id));
	}

	/**
	 * 按条件删除对象
	 * 
	 * @param id
	 */
	public void delete(final String propName, final Object propValue) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).is(propValue));
		mongoTemplate.remove(query, entityClass);
	}

	/**
	 * 按条件删除对象
	 * 
	 * @param id
	 */
	public void delete(final Map<String, Serializable> properties) {
		Assert.notEmpty(properties);
		Query query = createQuery();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
		}
		mongoTemplate.remove(query, entityClass);
	}

	/**
	 * 删除集合下的全部对象
	 */
	public void clear() {
		mongoTemplate.dropCollection(entityClass);
	}

	/**
	 * 获取全部对象
	 * 
	 * @return
	 */
	public List<T> findAll() {
		return mongoTemplate.findAll(entityClass);
	}

	/**
	 * 获取全部对象
	 * 
	 * @return
	 */
	public List<T> findAll(final Order[] orders) {
		Query query = createQuery();
		if (orders != null && orders.length > 0) {
			Sort sort = query.sort();
			for (Order order : orders) {
				sort.on(order.getName(), order.isAsc() ? org.springframework.data.mongodb.core.query.Order.ASCENDING
						: org.springframework.data.mongodb.core.query.Order.DESCENDING);
			}
		}
		return findByQuery(query);
	}

	/**
	 * 获取全部对象
	 * 
	 * @return
	 */
	public List<T> findAll(final int... rowStartIdxAndCount) {
		Query query = createQuery();
		if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
			query.skip(rowStartIdxAndCount[0]);
		}
		if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 1) {
			query.limit(rowStartIdxAndCount[1]);
		}
		return findByQuery(query);

	}

	/**
	 * 按page获取对象
	 * 
	 * @param page
	 * @return
	 */
	public Page<T> find(Page<T> page) {
		Assert.notNull(page);
		Query query = createQuery();

		// 条件过渡
		if (page.isFilterSetted()) {
			for (Map.Entry<String, Object> entry : page.getFilters().entrySet()) {
				query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
			}
		}
		// 统计数量
		if (page.isAutoCount()) {
			page.setTotalCount(count(query));
		}
		// 排序
		if (page.isOrderBySetted()) {
			Sort sort = query.sort();
			for (Order order : page.getOrders()) {
				sort.on(order.getName(), order.isAsc() ? org.springframework.data.mongodb.core.query.Order.ASCENDING
						: org.springframework.data.mongodb.core.query.Order.DESCENDING);
			}
		}
		// 分页
		if (page.isPageSizeSetted()) {
			query.skip((page.getPageNo() - 1) * page.getPageSize()).limit(page.getPageSize());
		}
		page.setResult(findByQuery(query));
		return page;
	}

	/**
	 * 按属性查找对象列表
	 * 
	 * @param propName - 属性名
	 * @param propValue - 属性值
	 * @param orders - 排序属性
	 * @return
	 */
	public List<T> findByProperty(final String propName, final Serializable propValue) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).is(propValue));
		return findByQuery(query);
	}

	/**
	 * 按属性查找对象列表
	 * 
	 * @param propName - 属性名
	 * @param propValue - 属性值
	 * @param orders - 排序属性
	 * @return
	 */
	public List<T> findByProperty(final String propName, final Serializable propValue, Order... orders) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).is(propValue));
		if (orders != null && orders.length > 0) {
			Sort sort = query.sort();
			for (Order order : orders) {
				sort.on(order.getName(), order.isAsc() ? org.springframework.data.mongodb.core.query.Order.ASCENDING
						: org.springframework.data.mongodb.core.query.Order.DESCENDING);
			}
		}
		return findByQuery(query);
	}

	/**
	 * 按属性查找对象列表
	 * 
	 * @param propName - 属性名
	 * @param propValue - 属性值
	 * @param orders - 排序属性
	 * @return
	 */
	public List<T> findByProperty(final String propName, final Serializable propValue, int... rowStartIdxAndCount) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).is(propValue));
		if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
			query.skip(rowStartIdxAndCount[0]);
		}
		if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 1) {
			query.limit(rowStartIdxAndCount[1]);
		}
		return findByQuery(query);
	}

	/**
	 * 按多个属性查找对象列表.
	 * 
	 * @param properties - 查询条件的属性名称与属性值
	 * @return
	 */
	public List<T> findByProperties(final Map<String, Serializable> properties, Order... orders) {
		Assert.notEmpty(properties);
		Query query = createQuery();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
		}
		if (orders != null && orders.length > 0) {
			Sort sort = query.sort();
			for (Order order : orders) {
				sort.on(order.getName(), order.isAsc() ? org.springframework.data.mongodb.core.query.Order.ASCENDING
						: org.springframework.data.mongodb.core.query.Order.DESCENDING);
			}
		}
		return findByQuery(query);
	}

	/**
	 * 小于(lt)查询
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	public List<T> findWithLt(final String propName, final Serializable value) {
		Assert.notNull(propName);
		Assert.notNull(value);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).lt(value));
		return findByQuery(query);
	}

	/**
	 * 小于等于(lte)查询
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	public List<T> findWithLte(final String propName, final Serializable value) {
		Assert.notNull(propName);
		Assert.notNull(value);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).lte(value));
		return findByQuery(query);
	}

	/**
	 * 大于(gt)查询
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	public List<T> findWithGt(final String propName, final Serializable value) {
		Assert.notNull(propName);
		Assert.notNull(value);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).gt(value));
		return findByQuery(query);
	}

	/**
	 * 小于等于(gte)查询
	 * 
	 * @param propName
	 * @param value
	 * @return
	 */
	public List<T> findWithGte(final String propName, final Serializable value) {
		Assert.notNull(propName);
		Assert.notNull(value);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).gte(value));
		return findByQuery(query);
	}

	/**
	 * between查询
	 * 
	 * @param propName - 属性名
	 * @param minxValue - 最小值
	 * @param maxValue - 最大值
	 * @return
	 */
	public List<T> findWithBetween(final String propName, final Serializable minValue, final Serializable maxValue) {
		Assert.notNull(propName);
		Assert.notNull(minValue);
		Assert.notNull(maxValue);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).gte(minValue).lte(maxValue));
		return findByQuery(query);
	}

	/**
	 * 不等于查询
	 * 
	 * @param propName
	 * @param minxValue
	 * @param maxValue
	 * @return
	 */
	public List<T> findWithNe(final String propName, final Serializable value) {
		Assert.notNull(propName);
		Assert.notNull(value);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).ne(value));
		return findByQuery(query);
	}

	/**
	 * in查询
	 * 
	 * @param propName
	 * @param values
	 * @return
	 */
	public List<T> findWithIn(final String propName, final Collection<?> values) {
		Assert.notNull(propName);
		Assert.notNull(values);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).in(values));
		return findByQuery(query);
	}

	/**
	 * has查询
	 * 
	 * @param propName
	 * @param values
	 * @return
	 */
	public List<T> findWithHas(final String propName, final Object value) {
		Assert.notNull(propName);
		Assert.notNull(value);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).elemMatch(Criteria.where(propName).is(value)));
		return findByQuery(query);
	}

	/**
	 * nin(not in)查询
	 * 
	 * @param propName
	 * @param values
	 * @return
	 */
	public List<T> findWithNin(final String propName, final Iterable<Serializable> values) {
		Assert.notNull(propName);
		Assert.notNull(values);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).nin(values));
		return findByQuery(query);
	}

	/**
	 * like查询
	 * 
	 * @param propName
	 * @param values
	 * @return
	 */
	public List<T> findWithLike(final String propName, final String regex) {
		Assert.notNull(propName);
		Assert.notNull(regex);
		Query query = createQuery();
		query.addCriteria(Criteria.where(propName).regex(regex));
		return findByQuery(query);
		// return findByProperty(propName, Pattern.compile(regex));
	}

	/**
	 * 按条件获取多个对象
	 * 
	 * @param query
	 * @return
	 */
	public List<T> findByQuery(Query query) {
		Assert.notNull(query);
		return mongoTemplate.find(query, entityClass);
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 */
	public boolean isPropertyUnique(String propName, Serializable propValue) {
		return getByProperty(propName, propValue) != null;
	}

	/**
	 * 统计符合条件的对象总数
	 * 
	 * @param properties - 条件
	 * @return 符合条件的对象总数
	 */
	public long count(Map<String, Serializable> properties) {
		Assert.notEmpty(properties);
		Query query = createQuery();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue()));
		}
		return count(query);
	}

	/**
	 * 统计符合条件的对象总数
	 * 
	 * @param query - 条件
	 * @return 符合条件的对象总数
	 */
	public long count(Query query) {
		return mongoTemplate.count(query, entityClass);
	}

	/**
	 * 统计对象总数
	 * 
	 * @return 对象总数
	 */
	public long count() {
		return mongoTemplate.count(new Query(), entityClass);
	}

	/**
	 * 创建查询对象
	 * 
	 * @return 查询对象
	 */
	public Query createQuery() {
		return new Query();
	}

	/**
	 * 创建查询对象
	 * 
	 * @return 查询对象
	 */
	public Query createQuery(Criteria criteria) {
		return new Query();
	}

	@Override
	public boolean isPropertyUnique(String propertyName, Serializable newValue, Serializable oldValue) {
		if (newValue == null || newValue.equals(oldValue))
			return true;

		Object object = getByProperty(propertyName, newValue);
		if (object == null)
			return true;
		else
			return false;
	}

	protected void init() {
		String databaseName = mongoTemplate.getDb().getName();
		Mongo mongo = mongoTemplate.getDb().getMongo();
		// 获取集合名
		Document annotation = this.entityClass.getAnnotation(Document.class);
		if (annotation == null) {
			throw new IllegalClassException("the class " + entityClass.getName() + " has no morphia Entity annotation.");
		}
		collectionName = annotation.collection();
		if ("".equals(collectionName) || collectionName == null) {
			collectionName = entityClass.getSimpleName();
		}
		dbCollection = mongo.getDB(databaseName).getCollection(collectionName);
		idField = getIdField();
	}

	protected Field getIdField() {
		for (Class<?> superClass = entityClass; superClass != Object.class; superClass = superClass.getSuperclass()) {
			for (Field field : superClass.getDeclaredFields()) {
				if (field.getName().equals("id")) {
					return field;
				}
			}
		}
		for (Class<?> superClass = entityClass; superClass != Object.class; superClass = superClass.getSuperclass()) {
			for (Field field : superClass.getDeclaredFields()) {
				if (field.getName().equals("id")) {
					return field;
				}
			}
		}
		return null;
	}

	/**
	 * @return the mongoTemplate
	 */
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	/**
	 * @param mongoTemplate
	 */
	@Autowired
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * 取得对象的主键值,辅助函数.
	 */
	@SuppressWarnings("unchecked")
	protected PK getId(T entity) {
		Assert.notNull(entity);
		try {
			return (PK) PropertyUtils.getProperty(entity, idField.getName());
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.mongoTemplate);
		init();
	}

}
