package com.lht.lhtsharding.engine;

/**
 * @author Leo
 * @date 2024/10/24
 */
public class ShardingContext {

    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();


    public static void set(ShardingResult shardingResult) {
        LOCAL.set(shardingResult);
    }

    public static ShardingResult get() {
        return LOCAL.get();
    }

    public static void remove() {
        LOCAL.remove();
    }


}
