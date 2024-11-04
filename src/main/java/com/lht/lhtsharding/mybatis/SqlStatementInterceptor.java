package com.lht.lhtsharding.mybatis;

import com.lht.lhtsharding.engine.ShardingContext;
import com.lht.lhtsharding.engine.ShardingResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
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

        ShardingResult shardingResult = ShardingContext.get();
        ShardingContext.remove();
        if (shardingResult != null) {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String sql = boundSql.getSql();
            log.info(" ========> sql statement: " + sql);
            String targetSqlStatement = shardingResult.getTargetSqlStatement();
            if (!sql.equalsIgnoreCase(targetSqlStatement))
                //替换sql
                replaceSql(boundSql, shardingResult.getTargetSqlStatement());
        }
        return invocation.proceed();
    }

    // sql字段在boundleSql中是final的，java里面其实是不允许改变的，这时候可以借用unsafe来修改，这个可以直接拿到指针，跳过检查
    private static void replaceSql(BoundSql boundSql, String sql) throws NoSuchFieldException {
        Field field = boundSql.getClass().getDeclaredField("sql");
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        // 这个偏移量其实就是指针
        long fieldOffset = unsafe.objectFieldOffset(field);
        unsafe.putObject(boundSql, fieldOffset, sql);
    }
}
