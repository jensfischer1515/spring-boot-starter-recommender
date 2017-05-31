package org.openended.recommender;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RecommenderTestApplication {

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Bean
    public LoggingDataSourcePostProcessor loggingDataSourcePostProcessor() {
        return new LoggingDataSourcePostProcessor();
    }
}
