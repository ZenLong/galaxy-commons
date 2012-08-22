package com.saysth.commons.unit.redis;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { "/applicationContext-redis-test.xml" })
public class PersonDaoTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private PersonDao personPao;

	@Test
	public void testSave() {
		for (int i = 0; i < 100; i++) {
			Person person = new Person();
			person.setId(String.valueOf(i));
			person.setFirstName("FirstName" + i);
			person.setLastName("LastName" + i);
			person.setGender(i % 2 == 0 ? GENDER.MALE : GENDER.FEMALE);
			personPao.save(person);
		}
	}

	@Test
	public void testGet() {
		for (int i = 0; i < 100; i++) {
			Person person = personPao.get(String.valueOf(i));
			System.out.println(person);
		}
	}
}
