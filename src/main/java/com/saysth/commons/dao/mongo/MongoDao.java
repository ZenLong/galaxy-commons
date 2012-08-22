package com.saysth.commons.dao.mongo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.IllegalClassException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.mapping.MappedClass;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.saysth.commons.dao.BaseDao;
import com.saysth.commons.dao.Order;
import com.saysth.commons.dao.Page;
import com.saysth.commons.utils.reflection.ReflectionUtils;

/**
 * MongoDB Dao based on morphia API
 * 
 * @author
 * 
 * @param <T>
 * @param <PK>
 */
public class MongoDao<T, PK extends Serializable> implements BaseDao<T, PK> {

	// 实体类的缺省主键属性名,子类可重载getIdName方法返回自定义主键属性名
	public static final String DEFAULT_ID_NAME = "id";
	public static final int ASC = 1; // 升序(1)
	public static final int DESC = -1; // 降序(-1)

	// 实体类的类对象
	protected Class<T> entityClass;
	protected DBCollection dbCollection;
	protected Field idField;
	protected BasicDAO<T, PK> morphiaDao;
	protected String collectionName;

	/**
	 * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class
	 * 例如: public class UserDao extends SpringMongoDao<User, Long>
	 */
	public MongoDao() {
		entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * 用于用于省略Dao层, 在Service层直接使用通用GenericMongoDao的构造函数
	 * 
	 * 在构造函数中定义对象类型Class. 例如: SpringMongoDao<User, Long> userDao = new
	 * SpringMongoDao<User, Long>(mongoFactory, User.class);
	 */
	public MongoDao(MongoFactoryBean mongoFactory) {
		setMongoFactory(mongoFactory);
	}

	@Autowired
	public void setMongoFactory(MongoFactoryBean mongoFactory) {
		init(mongoFactory);
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public String getTableName() {
		return dbCollection.getName();
	}

	/**
	 * 按id获取对象
	 */
	public T get(PK id) {
		return morphiaDao.get(id);
	}

	/**
	 * 按属性查找唯一对象
	 */
	public T getByProperty(final String propName, final Serializable propValue) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query<T> query = createQuery().field(propName).equal(propValue);
		return morphiaDao.findOne(query);
	}

	/**
	 * 按属性查找唯一对象
	 * 
	 * @param propName - 属性名
	 * @param propValue - 属性值
	 * @param orders - 排序
	 * @return
	 */
	public T getByProperty(final String propName, final Serializable propValue, final Order... orders) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query<T> query = createQuery().field(propName).equal(propValue);
		if (orders != null && orders.length > 0)
			query.order(this.getOrderString(orders));
		return morphiaDao.findOne(query);
	}

	/**
	 * 按多个属性查找唯一对象
	 * 
	 * @param properties - 属性
	 * @return
	 */
	public T getByProperties(final Map<String, Serializable> properties) {
		Assert.notEmpty(properties);
		Query<T> query = createQuery();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.filter(entry.getKey(), entry.getValue());
		}
		return morphiaDao.findOne(query);
	}

	/**
	 * 按多个属性查找唯一对象
	 * 
	 * @param properties - 属性
	 * @param orders - 排序
	 * @return
	 */
	public T getByProperties(final Map<String, Serializable> properties, final Order... orders) {
		Assert.notEmpty(properties);
		Query<T> query = createQuery();
		if (orders != null && orders.length > 0)
			query.order(this.getOrderString(orders));
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.filter(entry.getKey(), entry.getValue());
		}
		return morphiaDao.findOne(query);
	}

	/**
	 * 按条件获取对象
	 * 
	 * @param query - 查询条件
	 * @return
	 */
	public T getByQuery(Query<T> query) {
		Assert.notNull(query);
		return morphiaDao.findOne(query);
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
					PropertyUtils.setProperty(entity, idField.getName(), new ObjectId().toString());
				} catch (Exception e) {
				}
			}
		}
		morphiaDao.save(entity);
	}

	/**
	 * 删除对象
	 * 
	 * @param entity
	 */
	public void delete(T entity) {
		Assert.notNull(entity);
		morphiaDao.delete(entity);
	}

	/**
	 * 按主键删除对象
	 * 
	 * @param id
	 */
	public void delete(PK id) {
		Assert.notNull(id);
		morphiaDao.deleteById(id);
	}

	/**
	 * 按条件删除对象
	 * 
	 * @param id
	 */
	public void delete(final String propName, final Object propValue) {
		Assert.hasText(propName);
		Query<T> query = createQuery().field(propName).equal(propValue);
		Assert.notNull(query);
		morphiaDao.deleteByQuery(query);
	}

	/**
	 * 按条件删除对象
	 * 
	 * @param id
	 */
	public void delete(final Map<String, Object> properties) {
		Assert.notEmpty(properties);
		Query<T> query = createQuery();
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			query.filter(entry.getKey(), entry.getValue());
		}
		morphiaDao.deleteByQuery(query);
	}

	/**
	 * 删除集合下的全部对象
	 */
	public void clear() {
		morphiaDao.getCollection().drop();
	}

	/**
	 * 获取全部对象
	 * 
	 * @return
	 */
	public List<T> findAll(final Order[] orders) {
		Query<T> query = this.createQuery();
		if (orders != null && orders.length > 0)
			query.order(this.getOrderString(orders));
		return morphiaDao.find(query).asList();

	}

	/**
	 * 获取全部对象
	 * 
	 * @return
	 */
	public List<T> findAll() {
		Query<T> query = createQuery();
		return morphiaDao.find(query).asList();
	}

	/**
	 * 获取全部对象
	 * 
	 * @return
	 */
	public List<T> findAll(final int... rowStartIdxAndCount) {
		Query<T> query = createQuery();
		if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
			query.offset(rowStartIdxAndCount[0]);
		}
		if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 1) {
			query.limit(rowStartIdxAndCount[1]);
		}
		return morphiaDao.find(query).asList();

	}

	public Iterable<T> fetch(Order... orders) {
		Query<T> query = this.createQuery();
		if (orders != null && orders.length > 0)
			query.order(this.getOrderString(orders));
		return morphiaDao.find(query).fetch();
	}

	public Iterable<Key<T>> fetchKeys(Order... orders) {
		Query<T> query = this.createQuery();
		if (orders != null && orders.length > 0)
			query.order(this.getOrderString(orders));
		return morphiaDao.find(query).fetchKeys();
	}

	/**
	 * 按条件查询
	 * 
	 * @param query
	 * @return
	 */
	public QueryResults<T> findByQuery(Query<T> query) {
		Assert.notNull(query);
		return morphiaDao.find(query);
	}

	/**
	 * 按page获取对象
	 * 
	 * @param page
	 * @return
	 */
	public Page<T> find(Page<T> page) {
		Assert.notNull(page);
		Query<T> query = createQuery();
		// 条件过渡
		if (page.isFilterSetted()) {
			for (Map.Entry<String, Object> filter : page.getFilters().entrySet()) {
				query.filter(filter.getKey(), filter.getValue());
			}
		}
		// 统计数量
		if (page.isAutoCount()) {
			page.setTotalCount(morphiaDao.count(query));
		}
		// 排序
		if (page.isOrderBySetted()) {
			query.order(getOrderString(page.getOrders()));
		}
		// 分页
		if (page.isPageSizeSetted()) {
			query.offset((page.getPageNo() - 1) * page.getPageSize()).limit(page.getPageSize());
		}
		page.setResult(morphiaDao.find(query).asList());
		return page;
	}

	/**
	 * 按属性查找对象列表
	 * 
	 * @param propName - 属性名
	 * @param propValue - 属性值
	 * @return
	 */
	public List<T> findByProperty(final String propName, final Serializable propValue) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query<T> query = createQuery();
		query.filter(propName, propValue);
		return morphiaDao.find(query).asList();
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
		Query<T> query = createQuery();
		query.filter(propName, propValue);
		if (orders != null && orders.length > 0)
			query.order(this.getOrderString(orders));
		return morphiaDao.find(query).asList();
	}

	/**
	 * 按属性查找对象列表
	 * 
	 * @param propName - 属性名
	 * @param propValue - 属性值
	 * @param rowStartIdxAndCount - 起始索引和返回数
	 * @return
	 */
	public List<T> findByProperty(final String propName, final Serializable propValue, int... rowStartIdxAndCount) {
		Assert.hasText(propName);
		Assert.notNull(propValue);
		Query<T> query = createQuery();
		query.filter(propName, propValue);
		if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
			query.offset(rowStartIdxAndCount[0]);
		}
		if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 1) {
			query.limit(rowStartIdxAndCount[1]);
		}
		return morphiaDao.find(query).asList();
	}

	/**
	 * 按多个属性查找对象列表
	 * 
	 * @param properties - 查询条件的属性名称与属性值
	 * @param orders - 排序规则
	 * @return
	 */
	public List<T> findByProperties(final Map<String, Serializable> properties, Order... orders) {
		Assert.notEmpty(properties);
		Query<T> query = createQuery();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.filter(entry.getKey(), entry.getValue());
		}
		if (orders != null && orders.length > 0)
			query.order(this.getOrderString(orders));
		return morphiaDao.find(query).asList();
	}

	/**
	 * between查询
	 * 
	 * @param propName - 属性名
	 * @param minxValue - 最小值
	 * @param maxValue - 最大值
	 * @return
	 */
	public List<T> findWithBetween(final String propName, final Serializable minxValue, final Serializable maxValue) {
		Assert.notNull(propName);
		Assert.notNull(minxValue);
		Assert.notNull(maxValue);
		Query<T> query = createQuery();
		query.field(propName).greaterThanOrEq(minxValue);
		query.field(propName).lessThanOrEq(maxValue);
		return morphiaDao.find(query).asList();
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
		Query<T> query = createQuery();
		query.field(propName).notEqual(value);
		return morphiaDao.find(query).asList();
	}

	/**
	 * in查询
	 * 
	 * @param propName
	 * @param values
	 * @return
	 */
	public List<T> findWithIn(final String propName, final Iterable<?> values) {
		Assert.notNull(propName);
		Assert.notNull(values);
		Query<T> query = createQuery();
		query.field(propName).hasAnyOf(values);
		return morphiaDao.find(query).asList();
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
		Query<T> query = createQuery();
		query.field(propName).hasThisOne(value);
		return morphiaDao.find(query).asList();
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
		Query<T> query = createQuery();
		query.field(propName).hasNoneOf(values);
		return morphiaDao.find(query).asList();
	}

	/**
	 * like查询
	 * 
	 * @param propName
	 * @param values
	 * @return
	 */
	public List<T> findWithLike(final String propName, final String regexes) {
		return findByProperty(propName, Pattern.compile(regexes));
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一
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
		Query<T> query = createQuery();
		for (Map.Entry<String, Serializable> entry : properties.entrySet()) {
			query.filter(entry.getKey(), entry.getValue());
		}
		return count(query);
	}

	/**
	 * 统计符合条件的对象总数
	 * 
	 * @param query - 条件
	 * @return 符合条件的对象总数
	 */
	public long count(Query<T> query) {
		return morphiaDao.count(query);
	}

	/**
	 * 统计对象总数
	 * 
	 * @return 对象总数
	 */
	public long count() {
		return morphiaDao.count();
	}

	/**
	 * 创建查询对象
	 * 
	 * @return 查询对象
	 */
	public Query<T> createQuery() {
		return morphiaDao.createQuery();
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

	protected void init(MongoFactoryBean sessionFactory) {
		String databaseName = sessionFactory.getDatabase();
		Mongo mongo = sessionFactory.getMongo();
		Morphia morphia = sessionFactory.getMorphia();
		morphiaDao = new BasicDAO<T, PK>(entityClass, mongo, morphia, databaseName);
		morphiaDao.ensureIndexes();
		// 获取集合名
		Entity annotation = this.entityClass.getAnnotation(Entity.class);
		if (annotation == null) {
			throw new IllegalClassException("the class " + entityClass.getName() + " has no morphia Entity annotation.");
		}
		collectionName = annotation.value();
		if (Mapper.IGNORED_FIELDNAME.equals(collectionName) || collectionName == null) {
			collectionName = entityClass.getSimpleName();
		}
		dbCollection = sessionFactory.getMongo().getDB(databaseName).getCollection(collectionName);

		idField = getIdField(morphia, collectionName);
	}

	protected Field getIdField(Morphia morphia, String collectionName) {
		MappedClass mappedClass = morphia.getMapper().getMCMap().get(entityClass.getName());
		return mappedClass != null ? mappedClass.getIdField() : null;
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

	protected String getOrderString(Set<Order> orders) {
		StringBuffer sb = new StringBuffer();
		for (Order order : orders) {
			sb.append(",").append(getOrderString(order));
		}
		String orderStr = sb.toString();
		return orderStr.startsWith(",") ? orderStr.substring(1) : orderStr;
	}

	protected String getOrderString(final Order... orders) {
		StringBuffer sb = new StringBuffer();
		for (Order order : orders) {
			sb.append(",").append(getOrderString(order));
		}
		String orderStr = sb.toString();
		return orderStr.startsWith(",") ? orderStr.substring(1) : orderStr;
	}

	protected String getOrderString(Order order) {
		if (order.isAsc()) {
			return order.getName();
		} else {
			return "-" + order.getName();
		}
	}

}
