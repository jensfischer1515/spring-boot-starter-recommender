package org.openended.recommender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "recommender")
public class RecommenderProperties {

    @Value("${itemSimilarity.maxCacheSize:50000}")
    private int maxCacheSize;

    @Value("${endpoint.path:/recommender}")
    private String endpointPath;

    @Value("${endpoint.sensitive:false}")
    private boolean endpointSensitive;

    @Value("${refresher.rate:300}")
    private long refresherRate;

    private int howMany = 10;
}
