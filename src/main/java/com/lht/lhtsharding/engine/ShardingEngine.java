package com.lht.lhtsharding.engine;

/**
 *
 * Core Sharding Engine
 *
 * @author Leo
 * @date 2024/10/28
 */
public interface ShardingEngine {

    ShardingResult sharding(String sql, Object[] args);

}
