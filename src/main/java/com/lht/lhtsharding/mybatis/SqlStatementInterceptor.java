package com.lht.lhtsharding.mybatis;

import com.lht.lhtsharding.demo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Slf4j
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class SqlStatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        log.info(" ========> sql statement: " + boundSql.getSql());
        Object parameterObject = boundSql.getParameterObject();
        log.info(" ========> sql parameterObj: " + boundSql.getParameterObject());
        log.info(" ========> sql parameterMapping: " + boundSql.getParameterMappings());
        //TODO 修改sql， user -> user1
        return invocation.proceed();
    }
}
