package com.lht.lhtsharding;

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


}
