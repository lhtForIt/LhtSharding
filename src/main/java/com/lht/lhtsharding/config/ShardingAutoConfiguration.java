package com.lht.lhtsharding.config;

import com.lht.lhtsharding.datasource.ShardingDataSource;
import com.lht.lhtsharding.engine.ShardingEngine;
import com.lht.lhtsharding.engine.StandardShardingEngine;
import com.lht.lhtsharding.mybatis.SqlStatementInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties shardingProperties) {
        return new ShardingDataSource(shardingProperties);
    }

    @Bean
    public ShardingEngine shardingEngine(ShardingProperties shardingProperties) {
        return new StandardShardingEngine(shardingProperties);
    }

    @Bean
    public SqlStatementInterceptor sqlStatementInterceptor() {
        return new SqlStatementInterceptor();
    }


}
