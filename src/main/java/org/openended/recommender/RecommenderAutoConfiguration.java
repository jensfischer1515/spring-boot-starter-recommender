package org.openended.recommender;

import java.util.concurrent.ScheduledExecutorService;

import javax.sql.DataSource;

import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.ReloadFromJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.SQL92JDBCDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.NullRescorer;
import org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.LongPair;
import org.openended.recommender.migration.MigrationRepository;
import org.openended.recommender.migration.MigrationRepositoryImpl;
import org.openended.recommender.preference.PreferenceRepository;
import org.openended.recommender.preference.PreferenceRepositoryImpl;
import org.openended.recommender.preference.PreferenceService;
import org.openended.recommender.preference.PreferenceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Configuration
@EnableConfigurationProperties(RecommenderProperties.class)
@RequiredArgsConstructor
public class RecommenderAutoConfiguration {

    @NonNull
    private final RecommenderProperties recommenderProperties;

    @NonNull
    private final DatabaseConfiguration databaseConfiguration;

    @Bean
    @ConditionalOnMissingBean
    public ItemSimilarity itemSimilarity() {
        ItemSimilarity similarity = new LogLikelihoodSimilarity(databaseConfiguration.dataModel());
        return new CachingItemSimilarity(similarity, recommenderProperties.getMaxCacheSize());
    }

    @Bean
    @ConditionalOnMissingBean
    public ItemBasedRecommender recommender() {
        return new GenericItemBasedRecommender(databaseConfiguration.dataModel(), itemSimilarity());
    }

    @Bean
    @ConditionalOnMissingBean
    public Rescorer<LongPair> rescorer() {
        return NullRescorer.getItemItemPairInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public RecommenderRefresher recommenderRefresher() {
        return new RecommenderRefresher(recommender());
    }

    @Bean
    @ConditionalOnMissingBean
    public PreferenceService preferenceService() {
        return new PreferenceServiceImpl(databaseConfiguration.preferenceRepository(), databaseConfiguration.migrationRepository());
    }

    @Bean
    @ConditionalOnMissingBean
    public RecommenderService recommenderService() {
        return new RecommenderServiceImpl(recommender(), rescorer(), databaseConfiguration.migrationRepository());
    }

    @Bean
    @ConditionalOnProperty(value = "recommender.endpoint.enabled", havingValue = "true", matchIfMissing = true)
    public RecommenderEndpoint recommenderEndpoint() {
        return new RecommenderEndpoint(recommenderProperties, recommenderService());
    }

    @Bean
    @ConditionalOnClass(HealthIndicator.class)
    public RecommenderHealthIndicator recommenderHealthIndicator() {
        return new RecommenderHealthIndicator(recommender());
    }

    interface DatabaseConfiguration {
        DataModel dataModel();

        MigrationRepository migrationRepository();

        PreferenceRepository preferenceRepository();
    }

    @Configuration
    @ConditionalOnClass(name = "com.mysql.jdbc.Driver")
    @RequiredArgsConstructor
    static class MySQLConfiguration implements DatabaseConfiguration {

        @NonNull
        private final DataSource dataSource;

        @Bean
        @ConditionalOnMissingBean(DataModel.class)
        @SneakyThrows
        @Override
        public DataModel dataModel() {
            return new ReloadFromJDBCDataModel(new MySQLJDBCDataModel(dataSource));
        }

        @Bean
        @ConditionalOnMissingBean(MigrationRepository.class)
        @Override
        public MigrationRepository migrationRepository() {
            return new MigrationRepositoryImpl(new NamedParameterJdbcTemplate(dataSource))
                    .withSqlSave("insert ignore into taste_id_migration (long_id, string_id) values (:id, :uuid)");
        }

        @Bean
        @ConditionalOnMissingBean(PreferenceRepository.class)
        @Override
        public PreferenceRepository preferenceRepository() {
            return new PreferenceRepositoryImpl(new NamedParameterJdbcTemplate(dataSource))
                    .withSqlSave("insert ignore into taste_preferences (user_id, item_id, preference, timestamp) values (:userId, :itemId, :preference, :timestamp)");
        }
    }

    @Configuration
    @ConditionalOnClass(name = "org.h2.Driver")
    @RequiredArgsConstructor
    static class H2Configuration implements DatabaseConfiguration {

        @NonNull
        private final DataSource dataSource;

        @Bean
        @ConditionalOnMissingBean(DataModel.class)
        @SneakyThrows
        @Override
        public DataModel dataModel() {
            return new ReloadFromJDBCDataModel(new SQL92JDBCDataModel(dataSource));
        }

        @Bean
        @ConditionalOnMissingBean(MigrationRepository.class)
        @Override
        public MigrationRepository migrationRepository() {
            return new MigrationRepositoryImpl(new NamedParameterJdbcTemplate(dataSource))
                    .withSqlSave("merge into taste_id_migration (long_id, string_id) key(long_id) values (:id, :uuid)");
        }

        @Bean
        @ConditionalOnMissingBean(PreferenceRepository.class)
        @Override
        public PreferenceRepository preferenceRepository() {
            return new PreferenceRepositoryImpl(new NamedParameterJdbcTemplate(dataSource))
                    .withSqlSave("merge into taste_preferences (user_id, item_id, preference, timestamp) key(user_id, item_id) values (:userId, :itemId, :preference, :timestamp)");
        }
    }

    @Configuration
    @RequiredArgsConstructor
    static class RefresherConfiguration implements SchedulingConfigurer {

        @NonNull
        private final RecommenderProperties recommenderProperties;

        @NonNull
        private final RecommenderRefresher recommenderRefresher;

        @Autowired(required = false)
        private ScheduledExecutorService scheduledExecutorService;

        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            if (scheduledExecutorService != null) {
                taskRegistrar.setScheduler(scheduledExecutorService);
                taskRegistrar.addFixedRateTask(refreshTask());
            }
        }

        private IntervalTask refreshTask() {
            long interval = recommenderProperties.getRefresherRate() * 1_000L;
            return new IntervalTask(recommenderRefresher::refresh, interval, interval);
        }
    }
}
