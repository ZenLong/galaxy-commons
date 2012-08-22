/**
 * 
 */
package com.saysth.commons.unit.hibernate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.saysth.commons.test.data.RandomData;

/**
 * @author
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/applicationContext-*.xml" })
public class HibernateDaoTest {

	@Autowired
	private CategoryDao categoryDao;
	@Autowired
	private ProductDao productDao;

	public void testSave() {
		Category category = new Category();
		category.setName(RandomData.randomName("Category"));
		categoryDao.save(category);
		Product product = new Product();
		product.setName(RandomData.randomName("Product"));
		product.setCategory(category);
		productDao.save(product);
	}

	@Test
	public void testGet() {
		Long id = 1L;
		Product product = productDao.get(id);
		System.out.println(product);
	}

	@Test
	public void testGetAndSave() {
		Long id = 1L;
		Product product = productDao.get(id);
		System.out.println(product);
		product.setName(RandomData.randomName("Product"));
		productDao.save(product);
	}
}
