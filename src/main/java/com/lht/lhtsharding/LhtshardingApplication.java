package com.lht.lhtsharding;

import com.lht.lhtsharding.config.ShardingAutoConfiguration;
import com.lht.lhtsharding.demo.mapper.OrderMapper;
import com.lht.lhtsharding.demo.model.Order;
import com.lht.lhtsharding.demo.model.User;
import com.lht.lhtsharding.demo.mapper.UserMapper;
import com.lht.lhtsharding.mybatis.ShardingMapperFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "com.lht.lhtsharding.demo", factoryBean = ShardingMapperFactoryBean.class)
public class LhtshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LhtshardingApplication.class, args);
    }

    @Autowired
    UserMapper userMapper;

    @Autowired
    OrderMapper orderMapper;

    @Bean
    ApplicationRunner runner() {
        return x -> {

            System.out.println(" ================> ================> ================>");
            System.out.println(" ================> test user sharding ================>");
            System.out.println(" ================> ================> ================>");
            for (int i = 1; i <= 60; i++) {
                testUser(i);
            }

            System.out.println("\n\n\n\n");

            System.out.println(" ================> ================> ================>");
            System.out.println(" ================> test order sharding ================>");
            System.out.println(" ================> ================> ================>");

            for (int i = 1; i <= 60; i++) {
                testOrder(i);
            }


        };
    }

    private void testUser(int id) {
        System.out.println(" ======> 1. test insert....");
        int insert = userMapper.insert(new User(id, "leo" + id, 18));
        System.out.println(" ======> insert =" + insert);
        System.out.println(" ======> 2. test find....");
        User user = userMapper.findById(id);
        System.out.println(" ======> result =" + user);
        System.out.println(" ======> 3. test update....");
        user.setName("loo" + id);
        int update = userMapper.update(user);
        System.out.println(" ======> update =" + update);
        System.out.println(" ======> 4. test new find....");
        User user1 = userMapper.findById(id);
        System.out.println(" ======> result =" + user1);
        System.out.println(" ======> 5. test delete....");
        int delete = userMapper.deleteById(id);
        System.out.println(" ======> delete =" + delete);
    }

    private void testOrder(int id) {
        int id2 = id + 100;
        System.out.println("\n\n ====================>> id = " + id);
        System.out.println(" ======> 1. test insert....");
        int insert = orderMapper.insert(new Order(id, 1, 18));
        System.out.println(" ======> insert =" + insert);
        int inserted = orderMapper.insert(new Order(id2, 2, 18));
        System.out.println(" ======> insert =" + inserted);


        System.out.println(" ======> 2. test find....");
        Order order = orderMapper.findById(id, 1);
        System.out.println(" ======> result =" + order);
        Order order1 = orderMapper.findById(id2, 2);
        System.out.println(" ======> result =" + order1);


        System.out.println(" ======> 3. test update....");
        order.setPrice(11d);
        int update = orderMapper.update(order);
        System.out.println(" ======> update =" + update);
        order1.setPrice(22d);
        int updated = orderMapper.update(order1);
        System.out.println(" ======> update =" + updated);


        System.out.println(" ======> 4. test new find....");
        Order order11 = orderMapper.findById(id, 1);
        System.out.println(" ======> result =" + order11);
        Order order22 = orderMapper.findById(id2, 2);
        System.out.println(" ======> result =" + order22);


        System.out.println(" ======> 5. test delete....");
        int delete = orderMapper.deleteById(id, 1);
        System.out.println(" ======> delete =" + delete);
        int deleted = orderMapper.deleteById(id2, 2);
        System.out.println(" ======> delete =" + deleted);
    }

}
