package com.lht.lhtsharding.strategy;

import java.util.List;
import java.util.Map;

/**
 * @author Leo
 * @date 2024/10/29
 */
public interface ShardingStrategy {

    /**
     * 分库分表的字段名
     * @return
     */
    List<String> getShardingColumns();

    /**
     * 根据传入的参数返回分库或者分表的实际表名或者库名
     * @param availableTargetNames
     * @param logicTableName
     * @param shardingParams
     * @return
     */
    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String,Object> shardingParams);



}
