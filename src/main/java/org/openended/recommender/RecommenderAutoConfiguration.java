package org.openended.recommender;

import javax.sql.DataSource;

import org.apache.mahout.cf.taste.common.TasteException;
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
import org.openended.recommender.migration.MigrationAutoConfiguration;
import org.openended.recommender.preference.PreferenceAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MigrationAutoConfiguration.class, PreferenceAutoConfiguration.class, RecommenderEndpoint.class})
public class RecommenderAutoConfiguration {

    public static final String DATA_MODEL_BEAN_NAME = "dataModel";

    @Bean(DATA_MODEL_BEAN_NAME)
    @ConditionalOnH2
    @ConditionalOnMissingBean(DataModel.class)
    public DataModel h2DataModel(DataSource dataSource) throws TasteException {
        return new ReloadFromJDBCDataModel(new SQL92JDBCDataModel(dataSource));
    }

    @Bean(DATA_MODEL_BEAN_NAME)
    @ConditionalOnMySQL
    @ConditionalOnMissingBean(DataModel.class)
    public DataModel mysqlDataModel(DataSource dataSource) throws TasteException {
        return new ReloadFromJDBCDataModel(new MySQLJDBCDataModel(dataSource));
    }

    @Bean
    @ConditionalOnMissingBean(ItemSimilarity.class)
    public ItemSimilarity itemSimilarity(DataModel dataModel) {
        return new CachingItemSimilarity(new LogLikelihoodSimilarity(dataModel), 50_000);
    }

    @Bean
    @ConditionalOnMissingBean(ItemBasedRecommender.class)
    public ItemBasedRecommender itemBasedRecommender(DataModel dataModel, ItemSimilarity itemSimilarity) {
        return new GenericItemBasedRecommender(dataModel, itemSimilarity);
    }

    @Bean
    @ConditionalOnMissingBean(Rescorer.class)
    public Rescorer<LongPair> rescorer() {
        return NullRescorer.getItemItemPairInstance();
    }

    @Bean
    @ConditionalOnMissingBean(RecommenderRefresher.class)
    public RecommenderRefresher recommenderRefresher(ItemBasedRecommender itemBasedRecommender) {
        return new RecommenderRefresher(itemBasedRecommender);
    }
}
