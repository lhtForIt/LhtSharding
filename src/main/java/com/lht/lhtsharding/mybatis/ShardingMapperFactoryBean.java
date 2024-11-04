package com.lht.lhtsharding.mybatis;

import com.lht.lhtsharding.engine.ShardingContext;
import com.lht.lhtsharding.engine.ShardingEngine;
import com.lht.lhtsharding.engine.ShardingResult;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.apache.ibatis.ognl.OgnlRuntime.getFieldValue;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Slf4j
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {


    @Setter
    ShardingEngine shardingEngine;

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

            Object[] params = getParams(boundSql, args);
            ShardingResult shardingResult = shardingEngine.sharding(boundSql.getSql(), params);
            ShardingContext.set(shardingResult);

            return method.invoke(proxy, args);
        });
    }

    private Object[] getParams(BoundSql boundSql, Object[] args) {
        Object[] params = args;
        if (args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            Object arg = args[0];
            List<String> cols = boundSql.getParameterMappings().stream().map(ParameterMapping::getProperty).toList();
            Object[] newParams = new Object[cols.size()];
            for (int i = 0; i < cols.size(); i++) {
                newParams[i] = getFieldValue(arg, cols.get(i));
            }
            params = newParams;
        }
        return params;
    }

    @SneakyThrows
    private Object getFieldValue(Object o, String f) {
        Field field = o.getClass().getDeclaredField(f);
        field.setAccessible(true);
        return field.get(o);
    }


}
