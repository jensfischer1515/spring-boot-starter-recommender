package org.openended.recommender.preference;

import org.openended.recommender.ConditionalOnH2;
import org.openended.recommender.ConditionalOnMySQL;
import org.openended.recommender.migration.MigrationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

@Configuration
public class PreferenceAutoConfiguration {

    public static final String PREFERENCE_REPOSITORY_BEAN_NAME = "preferenceRepository";

    @Bean(PREFERENCE_REPOSITORY_BEAN_NAME)
    @ConditionalOnH2
    @ConditionalOnMissingBean(PreferenceRepository.class)
    public PreferenceRepository h2PreferenceRepository(NamedParameterJdbcOperations operations) {
        return new PreferenceRepositoryImpl(operations)
                .withSqlSave("merge into taste_preferences (user_id, item_id, preference, timestamp) key(user_id, item_id) values (:userId, :itemId, :preference, :timestamp)");
    }

    @Bean(PREFERENCE_REPOSITORY_BEAN_NAME)
    @ConditionalOnMySQL
    @ConditionalOnMissingBean(PreferenceRepository.class)
    public PreferenceRepository mysqlPreferenceRepository(NamedParameterJdbcOperations jdbcOperations) {
        return new PreferenceRepositoryImpl(jdbcOperations)
                .withSqlSave("insert ignore into taste_preferences (user_id, item_id, preference, timestamp) values (:userId, :itemId, :preference, :timestamp)");
    }

    @Bean
    @ConditionalOnMissingBean(PreferenceService.class)
    public PreferenceService preferenceService(PreferenceRepository preferenceRepository, MigrationRepository migrationRepository) {
        return new PreferenceServiceImpl(preferenceRepository, migrationRepository);
    }
}
