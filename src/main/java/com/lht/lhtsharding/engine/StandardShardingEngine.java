package com.lht.lhtsharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.lht.lhtsharding.config.ShardingProperties;
import com.lht.lhtsharding.strategy.HashShardingStrategy;
import com.lht.lhtsharding.strategy.ShardingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Leo
 * @date 2024/10/28
 */
@Slf4j
public class StandardShardingEngine implements ShardingEngine{

    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    private final MultiValueMap<String,String> actualTableNames = new LinkedMultiValueMap<>();
    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
    private final Map<String,ShardingStrategy> tableStrategies = new HashMap<>();


    public StandardShardingEngine(ShardingProperties shardingProperties){

        shardingProperties.getTables().forEach((table, tableProperties)->{
            tableProperties.getActualDataNodes().forEach(d->{
                String[] s = d.split("\\.");
                String databaseName = s[0], tableName = s[1];
                actualDatabaseNames.add(databaseName,tableName);
                actualTableNames.add(tableName, databaseName);
            });
            databaseStrategies.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrategies.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));
        });


    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {

        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        String table;
        Map<String, Object> shardingColumnsMap;
        //这里插入的statement和其他的statement是不一样的，需要单独处理
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {

            table = sqlInsertStatement.getTableName().getSimpleName();
            shardingColumnsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i = 0; i < columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr= (SQLIdentifierExpr) column;
                String columnName = columnExpr.getSimpleName();
                shardingColumnsMap.put(columnName, args[i]);
            }

        } else {

            // select/update/delete 找到库和表
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);

            LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if (sqlNames.size() > 1) throw new RuntimeException("not support multi table sharding: " + sqlNames);

            table = sqlNames.iterator().next().getSimpleName();
            log.info(" ========>>> visitor.getOriginalTables = " + table);
            shardingColumnsMap = visitor.getConditions().stream()
                    .collect(Collectors.toMap(c -> c.getColumn().getName(), c -> c.getValues().get(0)));
            log.info(" ========>>> visitor.getConditions = " + table);

        }

        ShardingStrategy databaseShardingStrategy = databaseStrategies.get(table);
        String targetDatabase = databaseShardingStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);
        ShardingStrategy tableShardingStrategy = tableStrategies.get(table);
        String targetTable = tableShardingStrategy.doSharding(actualTableNames.get(table), table, shardingColumnsMap);

        log.info(" ========>>>");
        log.info(" ========>>> target db.table = " + targetDatabase + "." + targetTable);
        log.info(" ========>>>");

        return new ShardingResult(targetDatabase, sql.replace(table, targetTable));
    }
}
