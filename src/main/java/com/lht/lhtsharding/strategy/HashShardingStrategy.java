package com.lht.lhtsharding.strategy;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author Leo
 * @date 2024/10/29
 */
public class HashShardingStrategy implements ShardingStrategy {

    private final String shardingColumn;
    private final String algorithmExpression;


    public HashShardingStrategy(Properties properties) {
        this.shardingColumn = properties.getProperty("shardingColumn");
        this.algorithmExpression = properties.getProperty("algorithmExpression");
    }

    @Override
    public List<String> getShardingColumns() {
        return List.of(shardingColumn);
    }

    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        String expression = InlineExpressionParser.handlePlaceHolder(algorithmExpression);
        InlineExpressionParser parser=new InlineExpressionParser(expression);
        Closure<?> closure = parser.evaluateClosure();
        closure.setProperty(shardingColumn,shardingParams.get(shardingColumn));
        return closure.call().toString();
    }
}
