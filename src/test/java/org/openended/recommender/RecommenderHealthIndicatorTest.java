package org.openended.recommender;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.boot.actuate.health.Status.OUT_OF_SERVICE;
import static org.springframework.boot.actuate.health.Status.UP;

import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;

import lombok.SneakyThrows;

@RunWith(MockitoJUnitRunner.class)
public class RecommenderHealthIndicatorTest {

    @Mock
    private DataModel dataModel;

    @Mock
    private Recommender recommender;

    @InjectMocks
    private RecommenderHealthIndicator recommenderHealthIndicator;

    @Before
    public void setup() {
        willReturn(dataModel).given(recommender).getDataModel();
    }

    @Test
    @SneakyThrows
    public void should_indicate_up() {
        // GIVEN
        Health.Builder builder = new Health.Builder();
        willReturn(1).given(dataModel).getNumItems();

        // WHEN
        recommenderHealthIndicator.doHealthCheck(builder);
        Health health = builder.build();

        // THEN
        then(health.getStatus()).isEqualTo(UP);
    }

    @Test
    @SneakyThrows
    public void should_indicate_out_of_service() {
        // GIVEN
        Health.Builder builder = new Health.Builder();
        willReturn(0).given(dataModel).getNumItems();

        // WHEN
        recommenderHealthIndicator.doHealthCheck(builder);
        Health health = builder.build();

        // THEN
        then(health.getStatus()).isEqualTo(OUT_OF_SERVICE);
    }
}
