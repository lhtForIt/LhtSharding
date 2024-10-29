package com.lht.lhtsharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShardingResult {

    private String dataSourceName;
    private String targetSqlStatement;


}
