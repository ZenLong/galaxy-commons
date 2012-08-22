package com.saysth.commons.unit.hibernate;

import org.springframework.stereotype.Component;

import com.saysth.commons.dao.hibernate.HibernateDao;

@Component
public class CategoryDao extends HibernateDao<Category, Long> {
}
