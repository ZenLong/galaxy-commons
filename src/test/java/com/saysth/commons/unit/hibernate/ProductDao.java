package com.saysth.commons.unit.hibernate;

import org.springframework.stereotype.Component;

import com.saysth.commons.dao.hibernate.HibernateDao;

@Component
public class ProductDao extends HibernateDao<Product, Long> {
}
