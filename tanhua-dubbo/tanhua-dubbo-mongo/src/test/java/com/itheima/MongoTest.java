package com.itheima;

import com.itheima.pojo.Person;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = MongoApplication.class)
@RunWith(SpringRunner.class)
public class MongoTest {
    /*
        springData-mongo的操作
            1.导入坐标
            2.编写实体类，添加注解，配置集合和对象间的映射关系
            3.注入mongoTemplate对象
            4.调用对象方法，完成数据库操作
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存操作
     */
    @Test
    public void testSave(){
        for (int i = 0; i < 10; i++) {
            Person person = new Person();
            person.setId(ObjectId.get()); //ObjectId.get()：获取一个唯一主键字符串
            person.setName("张三"+i);
            person.setAddress("北京顺义"+i);
            person.setAge(18+i);
            mongoTemplate.save(person);
        }
    }
    /**
     * 查询所有
     */
    @Test
    public void testAll(){
        List<Person> personList = mongoTemplate.findAll(Person.class);
        for (Person person : personList) {
            System.out.println(person);
        }
    }

    /**
     * 条件查询
     */
    @Test
    public void testFind(){
        //查询条件小于20的人
        Query query = new Query(Criteria.where("age").lt(20));
        //条件查询
        List<Person> personList = mongoTemplate.find(query, Person.class);
        //便利查询
        for (Person person : personList) {
            System.out.println(person);
        }
    }

    /**
     * 分页查询
     */
    @Test
    public void testPage(){
        Criteria criteria = Criteria.where("age").lt(20);
        //查询总数
        Query query = new Query();
        long count = mongoTemplate.count(query, Person.class);
        System.out.println(count);
        //查询当前页的数据列表，查询第二页，每页查询2条
        Query queryLimit = new Query(criteria)
                .limit(2)//设置每页查询条数
                .skip(2);//开启查询的条数 (page-1)*size
        List<Person> personList = mongoTemplate.find(queryLimit, Person.class);
        for (Person person : personList) {
            System.out.println(person);
        }
    }

    /**
     * 更新
     */
    @Test
    public void testUpdate(){
        //条件
        Query query = Query.query(Criteria.where("id").is("626828d7df108c59be031554"));
        //更新的数据
        Update update = new Update();
        update.set("age",20);
        //执行更新操作
        mongoTemplate.updateFirst(query,update,Person.class);
    }

    /**
     * 删除
     */
    @Test
    public void testRemove(){
        //条件
        Query query = Query.query(Criteria.where("id").is("626828d7df108c59be031554"));
        mongoTemplate.remove(query,Person.class);
    }
}
