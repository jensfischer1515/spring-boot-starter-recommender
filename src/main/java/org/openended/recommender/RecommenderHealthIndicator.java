package org.openended.recommender;

import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RecommenderHealthIndicator extends AbstractHealthIndicator {

    @NonNull
    private final Recommender recommender;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        DataModel dataModel = recommender.getDataModel();
        int numItems = dataModel.getNumItems();
        int numUsers = dataModel.getNumUsers();

        builder
                .withDetail("numItems", numItems)
                .withDetail("numUsers", numUsers)
                .withDetail("minPreference", dataModel.getMinPreference())
                .withDetail("maxPreference", dataModel.getMaxPreference())
                .withDetail("hasPreferenceValues", dataModel.hasPreferenceValues())
        ;

        if (numItems <= 0) {
            builder.outOfService();
        } else {
            builder.up();
        }
    }
}
