package test;

import java.io.Serializable;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Polymorphic;

interface IdEntity<T extends Serializable> extends Serializable {
	T getId();

	void setId(T id);
}

@SuppressWarnings("serial")
@Polymorphic
class BaseEntity implements IdEntity<String> {
	@Id
	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}

@SuppressWarnings("serial")
@Entity(noClassnameStored = true)
class User extends BaseEntity {
	private long seq;
	private String name;
	private Date creationTime = new Date();

	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

}

public class FastJsonTest {
	@Test
	public void test() {
		User user = new User();
		user.setId("vkrqsdrdsfks");
		String json = JSON.toJSONString(user, SerializerFeature.WriteClassName);
		System.out.println(json);
		user = (User) JSON.parse(json);// 此处抛异常
		Assert.assertNotNull(user);
	}

}
