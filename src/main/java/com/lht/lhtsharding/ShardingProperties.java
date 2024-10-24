package com.lht.lhtsharding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Data
@ConfigurationProperties(prefix = "spring.sharding")
public class ShardingProperties {

    private Map<String, Properties> dataSource = new LinkedHashMap<>();

}
