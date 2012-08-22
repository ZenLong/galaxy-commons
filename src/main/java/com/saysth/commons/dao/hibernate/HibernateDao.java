package com.saysth.commons.dao.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;

import com.saysth.commons.dao.BaseDao;
import com.saysth.commons.dao.Page;
import com.saysth.commons.utils.reflection.ReflectionUtils;

/**
 * Hibernate的范型基类.
 * 
 * @param <T> DAO操作的对象类型
 * @param <PK> 主键类型
 * 
 * @author
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HibernateDao<T, PK extends Serializable> implements BaseDao<T, PK> {
	private HibernateTemplate hibernateTemplate;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Class<T> entityClass;

	private SessionFactory sessionFactory;

	/**
	 * 用于Dao层子类使用的构造函数. 通过子类的泛型定义取得对象类型Class
	 * 例如: public class UserDao extends HibernateDao<User, Long>
	 */
	public HibernateDao() {
		this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * 用于用于省略Dao层, 在Service层直接使用通用GenericHibernateDao的构造函数. 在构造函数中定义对象类型Class.
	 * 例如: HibernateDao<User, Long> userDao = new HibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public HibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
		setSessionFactory(sessionFactory);
		this.entityClass = entityClass;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		if (this.hibernateTemplate == null || sessionFactory != this.hibernateTemplate.getSessionFactory()) {
			this.hibernateTemplate = createHibernateTemplate(sessionFactory);
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param entity
	 */
	public void save(T entity) {
		Assert.notNull(entity);
		getHibernateTemplate().saveOrUpdate(entity);
		logger.info("save entity: {}", entity);
	}

	/**
	 * 删除对象
	 * 
	 * @param entity
	 */
	public void delete(T entity) {
		Assert.notNull(entity);
		getHibernateTemplate().delete(entity);
		logger.info("delete entity: {}", entity);
	}

	/**
	 * 按主键删除对象
	 * 
	 * @param id
	 */
	public void delete(PK id) {
		Assert.notNull(id);
		delete(get(id));
	}

	/**
	 * 获取全部对象
	 * 
	 * @return
	 */
	public List<T> findAll(final int... rowStartIdxAndCount) {
		return (List<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria c = createCriteria();
				if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
					c.setFirstResult(rowStartIdxAndCount[0]);
				}
				if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 1) {
					c.setMaxResults(rowStartIdxAndCount[1]);
				}
				return c.list();
			}
		});
	}

	/**
	 * 按page获取全部对象
	 * 
	 * @param page
	 * @return
	 */
	public Page<T> find(Page<T> page) {
		return findByCriterion(page);
	}

	/**
	 * 按id获取对象
	 */
	public T get(final PK id) {
		return (T) getHibernateTemplate().get(entityClass, id);
	}

	public T getByProperty(final String propertyName, final Serializable value) {
		Assert.hasText(propertyName);
		return findUniqueByProperty(propertyName, value);
	}

	/**
	 * 按HQL查询对象列表
	 * 
	 * @param hql - hql语句
	 * @param values - 数量可变的参数
	 */
	public List find(final String hql, final Serializable... values) {
		return (List) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return createQuery(hql, values).list();
			}
		});
	}

	/**
	 * 按HQL分页查询. 暂不支持自动获取总结果数,需用户另行执行查询
	 * 
	 * @param page - 分页参数.包括pageSize 和firstResult.
	 * @param hql - hql语句.
	 * @param values - 数量可变的参数.
	 * 
	 * @return 分页查询结果,附带结果列表及所有查询时的参数.
	 */
	public Page<T> find(final Page<T> page, final String hql, final Serializable... values) {
		Assert.notNull(page);
		return (Page<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				if (page.isAutoCount() == true) {
					logger.warn("HQL查询暂不支持自动获取总结果数,hql为{}", hql);
				}
				Query q = createQuery(hql, values);
				if (page.isFirstSetted()) {
					q.setFirstResult(page.getFirst());
				}
				if (page.isPageSizeSetted()) {
					q.setMaxResults(page.getPageSize());
				}
				page.setResult(q.list());
				return page;
			}
		});

	}

	/**
	 * 按HQL查询唯一对象.
	 */
	public Object findUnique(final String hql, final Serializable... values) {
		return (Object) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return createQuery(hql, values).uniqueResult();
			}
		});
	}

	/**
	 * 按HQL查询Intger类形结果.
	 */
	public Integer findInt(String hql, Object... values) {
		return (Integer) findUnique(hql, values);
	}

	/**
	 * 按HQL查询Long类型结果.
	 */
	public Long findLong(String hql, Object... values) {
		return (Long) findUnique(hql, values);
	}

	/**
	 * 原生SQL 查询
	 */
	public List<T> findByNativeSQL(final Class<T> clazz, final String sql, final Serializable... values) {
		Assert.notNull(clazz);
		Assert.notNull(sql);
		return (List<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Query query = getSession().createSQLQuery(sql).addEntity(clazz);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						query.setParameter(i, values[i]);
					}
				}
				return query.list();
			}
		});
	}

	/**
	 * 按Criterion查询对象列表.
	 * 
	 * @param criterion - 数量可变的Criterion.
	 */
	public List<T> findByCriterion(final Criterion... criterion) {
		return (List<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return createCriteria(criterion).list();
			}
		});
	}

	/**
	 * 按Criterion分页查询.
	 * 
	 * @param page - 分页参数.包括pageSize、firstResult、orderBy、asc、autoCount. 其中firstResult可直接指定,也可以指定pageNo.
	 *            autoCount指定是否动态获取总结果数.
	 * 
	 * @param criterion - 数量可变的Criterion.
	 * @return 分页查询结果.附带结果列表及所有查询时的参数.
	 */
	public Page<T> findByCriterion(final Page page, final Criterion... criterion) {
		Assert.notNull(page);
		return (Page<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria c = createCriteria(criterion);

				if (page.isAutoCount()) {
					page.setTotalCount(countQueryResult(c));
					if (page.getTotalCount() == 0) {
						return page;
					}
				}
				if (page.isFirstSetted()) {
					c.setFirstResult(page.getFirst());
				}
				if (page.isPageSizeSetted()) {
					c.setMaxResults(page.getPageSize());
				}

				if (page.isOrderBySetted()) {
					for (com.saysth.commons.dao.Order order : page.getOrders()) {
						if (order.isAsc()) {
							c.addOrder(Order.asc(order.getName()));
						} else {
							c.addOrder(Order.desc(order.getName()));
						}
					}
				}
				page.setResult(c.list());
				return page;
			}
		});

	}

	/**
	 * 按DetachedCriteria分页查询
	 */
	public Page<T> findByDetachedCriteria(final DetachedCriteria detachedCriteria, final Page<T> page) {
		return (Page<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria criteria = detachedCriteria.getExecutableCriteria(session);

				if (page.isAutoCount()) {
					page.setTotalCount(countQueryResult(criteria));
					if (page.getTotalCount() == 0) {
						return page;
					}
				}
				if (page.isFirstSetted()) {
					criteria.setFirstResult(page.getFirst());
				}
				if (page.isPageSizeSetted()) {
					criteria.setMaxResults(page.getPageSize());
				}

				if (page.isOrderBySetted()) {
					for (com.saysth.commons.dao.Order order : page.getOrders()) {
						if (order.isAsc()) {
							criteria.addOrder(Order.asc(order.getName()));
						} else {
							criteria.addOrder(Order.desc(order.getName()));
						}
					}
				}
				page.setResult(criteria.list());
				return page;
			}
		});
	}

	/**
	 * 按DetachedCriteria查询对象列表
	 */
	public List<T> findByDetachedCriteria(final DetachedCriteria detachedCriteria) {
		return (List<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria criteria = detachedCriteria.getExecutableCriteria(session);

				return criteria.list();
			}
		});
	}

	/**
	 * 按属性查找对象列表.
	 */
	public List<T> findByProperty(final String propertyName, final Serializable value) {
		Assert.hasText(propertyName);
		return (List<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return createCriteria(Restrictions.eq(propertyName, value)).list();

			}
		});
	}

	/**
	 * 按属性查找对象列表.
	 */
	public List<T> findByProperty(final String propertyName, final Serializable value, final int... rowStartIdxAndCount) {
		Assert.hasText(propertyName);
		return (List<T>) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria c = createCriteria(Restrictions.eq(propertyName, value));
				if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 0) {
					c.setFirstResult(rowStartIdxAndCount[0]);
				}
				if (rowStartIdxAndCount != null && rowStartIdxAndCount.length > 1) {
					c.setMaxResults(rowStartIdxAndCount[1]);
				}
				return c.list();
			}
		});
	}

	/**
	 * 按属性查找唯一对象.
	 */
	public T findUniqueByProperty(final String propertyName, final Serializable value) {
		Assert.hasText(propertyName);
		return (T) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return createCriteria(Restrictions.eq(propertyName, value)).uniqueResult();

			}
		});
	}

	/**
	 * 按多个属性查找唯一对象.
	 */
	public T findUniqueByProperties(final Map<String, Serializable> properties) {
		Assert.notEmpty(properties);
		return (T) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria criteria = createCriteria();
				for (Map.Entry<String, Serializable> m : properties.entrySet()) {
					criteria.add(Restrictions.eq(m.getKey(), m.getValue()));
				}
				return criteria.uniqueResult();

			}
		});
	}

	/**
	 * 根据查询方法与参数列表创建Query对象.
	 */
	protected Query createQuery(final String queryString, final Serializable... values) {
		Assert.hasText(queryString);
		Query q = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				q.setParameter(i, values[i]);
			}
		}
		q.setCacheable(true);
		return q;
	}

	/**
	 * 根据Criterion条件创建Criteria.
	 */
	protected Criteria createCriteria(Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		criteria.setCacheable(true);
		return criteria;
	}

	/**
	 * 根据Criterion条件创建DetachedCriteria,后续可进行更多处理,辅助方法.创建离线查询
	 * 
	 * @return DetachedCriteria
	 */
	public DetachedCriteria createDetachedCriteria(Criterion... criterions) {
		DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 * 判断对象的属性值在数据库内是否唯一.
	 * 
	 * 在修改对象的情景下,如果属性新修改的值(value)等于属性原值(orgValue)则不作比较.
	 * 传回orgValue的设计侧重于从页面上发出Ajax判断请求的场景. 否则需要SS2里那种以对象ID作为第3个参数的isUnique方法.
	 */
	public boolean isPropertyUnique(final String propertyName, final Serializable newValue, final Serializable orgValue) {
		if (newValue == null || newValue.equals(orgValue))
			return true;

		Object object = findUniqueByProperty(propertyName, newValue);
		if (object == null)
			return true;
		else
			return false;
	}

	public long count() {
		return (Long) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				Criteria c = createCriteria();
				// 执行Count查询
				Long totalCount = (Long) c.setProjection(Projections.rowCount()).uniqueResult();
				return totalCount;

			}
		});
	}

	/**
	 * 通过count查询获得本次查询所能获得的对象总数.
	 * 
	 * @return page对象中的totalCount属性将赋值.
	 */
	protected long countQueryResult(final Criteria c) {
		return (Long) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				CriteriaImpl impl = (CriteriaImpl) c;
				impl.setCacheable(true);
				// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
				Projection projection = impl.getProjection();
				ResultTransformer transformer = impl.getResultTransformer();

				List<CriteriaImpl.OrderEntry> orderEntries = null;
				try {
					orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
					ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
				} catch (Exception e) {
					logger.error("不可能抛出的异常:{}", e.getMessage());
				}

				// 执行Count查询
				long totalCount = (Long) c.setProjection(Projections.rowCount()).uniqueResult();

				// 将之前的Projection和OrderBy条件重新设回去
				c.setProjection(projection);

				if (projection == null) {
					c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
				}
				if (transformer != null) {
					c.setResultTransformer(transformer);
				}

				try {
					ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
				} catch (Exception e) {
					logger.error("不可能抛出的异常:{}", e.getMessage());
				}

				return totalCount;

			}
		});
	}

	/**
	 * 通过count查询获得本次查询所能获得的对象总数.
	 * 
	 * @return page对象中的totalCount属性将赋值.
	 */
	public long countQueryResult(final DetachedCriteria detachedCriteria) {
		return (Long) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {

				CriteriaImpl impl = (CriteriaImpl) detachedCriteria.getExecutableCriteria(session);
				impl.setCacheable(true);
				// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
				Projection projection = impl.getProjection();
				ResultTransformer transformer = impl.getResultTransformer();

				List<CriteriaImpl.OrderEntry> orderEntries = null;
				try {
					orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
					ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
				} catch (Exception e) {
					logger.error("不可能抛出的异常:{}", e.getMessage());
				}

				// 执行Count查询
				long totalCount = (Long) impl.setProjection(Projections.rowCount()).uniqueResult();

				// 将之前的Projection和OrderBy条件重新设回去
				impl.setProjection(projection);

				if (projection == null) {
					impl.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
				}
				if (transformer != null) {
					impl.setResultTransformer(transformer);
				}

				try {
					ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
				} catch (Exception e) {
					logger.error("不可能抛出的异常:{}", e.getMessage());
				}

				return totalCount;

			}
		});
	}

	/**
	 * 执行HQL
	 * 
	 * @param hql - hql语句
	 * @param values - 数量可变的参数
	 */
	public int execute(final String hql, final Serializable... values) {
		return (Integer) getHibernateTemplate().executeWithNativeSession(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException {
				return createQuery(hql, values).executeUpdate();
			}
		});
	}

	public void flush() {
		getHibernateTemplate().flush();
	}

	public void clearCache(Object entity) {
		Assert.notNull(entity);
		getHibernateTemplate().evict(entity);
	}

	@SuppressWarnings("deprecation")
	public void clearAllCache(Class clazz) {
		Assert.notNull(clazz);
		getSessionFactory().evict(clazz);
	}

	public void clearAllCache() {
		getHibernateTemplate().clear();
	}

	public void reloadLazy(Object proxy) {
		Assert.notNull(proxy);
		Hibernate.initialize(proxy);
		logger.info("initialize proxy: {}", proxy);
	}

	protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
		return new HibernateTemplate(sessionFactory);
	}

	protected final Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public final SessionFactory getSessionFactory() {
		return (this.hibernateTemplate != null ? this.hibernateTemplate.getSessionFactory() : null);
	}

	protected final HibernateTemplate getHibernateTemplate() {
		return this.hibernateTemplate;
	}

}