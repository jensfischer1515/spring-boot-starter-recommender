package org.openended.recommender.migration;

import org.openended.recommender.ConditionalOnH2;
import org.openended.recommender.ConditionalOnMySQL;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

@Configuration
public class MigrationAutoConfiguration {

    public static final String MIGRATION_REPOSITORY_BEAN_NAME = "migrationRepository";

    @Bean(MIGRATION_REPOSITORY_BEAN_NAME)
    @ConditionalOnH2
    @ConditionalOnMissingBean(MigrationRepository.class)
    public MigrationRepository h2MigrationRepository(NamedParameterJdbcOperations operations) {
        return new MigrationRepositoryImpl(operations)
                .withSqlSave("merge into taste_id_migration (long_id, string_id) key(long_id) values (:id, :uuid)");
    }

    @Bean(MIGRATION_REPOSITORY_BEAN_NAME)
    @ConditionalOnMissingBean(MigrationRepository.class)
    @ConditionalOnMySQL
    public MigrationRepository mysqlMigrationRepository(NamedParameterJdbcOperations operations) {
        return new MigrationRepositoryImpl(operations)
                .withSqlSave("insert ignore into taste_id_migration (long_id, string_id) values (:id, :uuid)");
    }
}
