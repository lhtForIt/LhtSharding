package com.lht.lhtsharding;

import com.lht.lhtsharding.demo.User;
import com.lht.lhtsharding.demo.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "com.lht.lhtsharding.demo",factoryBean = ShardingMapperFactoryBean.class)
public class LhtshardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LhtshardingApplication.class, args);
    }

    @Autowired
    UserMapper userMapper;

    @Bean
    ApplicationRunner runner() {
        return x->{


            for (int i = 1; i <= 10; i++) {
                test(i);
            }

        };
    }

    private void test(int id) {
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
//            System.out.println(" ======> 5. test delete....");
//            int delete = userMapper.deleteById(1);
//            System.out.println(" ======> delete =" + delete);
    }

}
