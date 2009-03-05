package net.rubyeye.xmemcached.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import net.rubyeye.xmemcached.XMemcachedClient;

class Name implements Serializable {
	String firstName;
	String lastName;
	int age;
	int money;

	public Name(String firstName, String lastName, int age, int money) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.money = money;
	}

	public String toString() {
		return "[" + firstName + " " + lastName + ",age=" + age + ",money="
				+ money + "]";
	}

}

public class Example {
	public static void main(String[] args) {
		try {
			String ip = "192.168.222.100";

			int port = 11211;
			XMemcachedClient client = new XMemcachedClient(ip, port);
			// �洢����
			if (!client.set("hello", 0, "dennis")) {
				System.err.println("set error");
			}
			client.add("hello", 0, "dennis");
			client.replace("hello", 0, "dennis");

			// get����
			String name = (String) client.get("hello");
			System.out.println(name);

			// ������ȡ
			List<String> keys = new ArrayList<String>();
			keys.add("hello");
			keys.add("test");
			Map<String, Object> map = client.get(keys);
			System.out.println("map size:" + map.size());

			// delete����
			if (!client.delete("hello", 1000)) {
				System.err.println("delete error");
			}

			// incr,decr
			client.incr("a", 4);
			client.decr("a", 4);

			// version
			String version = client.version();
			System.out.println(version);
			// ��ɾ�Ĳ��Զ������
			Name dennis = new Name("dennis", "zhuang", 26, -1);
			System.out.println("dennis:" + dennis);
			client.set("dennis", 0, dennis);

			Name cachedPerson = (Name) client.get("dennis");
			System.out.println("cachedPerson:" + cachedPerson);
			cachedPerson.money = -10000;

			client.replace("dennis", 0, cachedPerson);
			Name cachedPerson2 = (Name) client.get("dennis");
			System.out.println("cachedPerson2:" + cachedPerson2);

			// delete
			client.delete("dennis");
			System.out.println("after delete:" + client.get("dennis"));

			// ��д����
			map = new HashMap();
			for (int i = 0; i < 1000; i++)
				map.put(String.valueOf(i), i);
			if (!client.set("map", 0, map, 10000)) {
				System.err.println("set map error");
			}
			HashMap cachedMap = (HashMap) client.get("map", 100000);
			if (cachedMap.size() != 1000)
				System.err.println("get map error");
			for (Object key : cachedMap.keySet()) {
				if (!cachedMap.get(key).equals(Integer.parseInt((String) key))) {
					System.err.println("get map error");
				}
			}
			//��ȡ�����ڵ�key
			for (int i = 0; i < 100; i++)
				if (client.get("hello__" + i) != null)
					System.err.println("get error");
			//ɾ�������ڵ�key
			for (int i = 0; i < 100; i++)
				if (client.delete("hello__" + i))
					System.err.println("get error");
			client.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}