package com.lht.lhtsharding;

import com.lht.lhtsharding.demo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Slf4j
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    public ShardingMapperFactoryBean() {
    }

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        SqlSession sqlSession = getSqlSession();
        Configuration configuration = sqlSession.getConfiguration();
        Class<T> clazz = getMapperInterface();

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (p, method, args) -> {

            String mapperId = clazz.getName() + "." + method.getName();
            MappedStatement mappedStatement = configuration.getMappedStatement(mapperId);
            BoundSql boundSql = mappedStatement.getBoundSql(args);
            log.info(" ========> sql statement: " + boundSql.getSql());
            Object parameterObject = args[0];
            if (parameterObject instanceof User user) {
                ShardingContext.set(new ShardingResult(user.getId() % 2 == 0 ? "db0" : "db1"));
            } else if (parameterObject instanceof Integer id) {
                ShardingContext.set(new ShardingResult(id % 2 == 0 ? "db0" : "db1"));
            }
            return method.invoke(proxy, args);
        });
    }


}
