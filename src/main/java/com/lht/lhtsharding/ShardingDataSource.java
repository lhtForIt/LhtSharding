package com.lht.lhtsharding;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Slf4j
public class ShardingDataSource extends AbstractRoutingDataSource {



    public ShardingDataSource(ShardingProperties shardingProperties) {

        Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
        shardingProperties.getDataSource().forEach((k,v)->{
            try {
                dataSourceMap.put(k, DruidDataSourceFactory.createDataSource(v));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        setTargetDataSources(dataSourceMap);
        setDefaultTargetDataSource(dataSourceMap.values().iterator().next());

    }
    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult shardingResult = ShardingContext.get();
        Object key = shardingResult == null ? null : shardingResult.getDataSourceName();
        log.info(" ========> determineCurrentLookupKey = " + key);
        return key;
    }
}
