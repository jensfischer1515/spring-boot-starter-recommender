package org.openended.recommender;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.willThrow;

import java.util.List;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.common.LongPair;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openended.recommender.migration.MigrationRepository;

import lombok.SneakyThrows;

@RunWith(MockitoJUnitRunner.class)
public class RecommenderServiceImplTest {

    @InjectMocks
    private RecommenderServiceImpl recommenderService;

    @Mock
    private ItemBasedRecommender itemBasedRecommender;

    @Mock
    private Rescorer<LongPair> rescorer;

    @Mock
    private MigrationRepository migrationRepository;

    @Test
    public void should_recommend_nothing_on_empty_item_preferences() {
        // GIVEN
        int howMany = 1;
        long[] itemIds = {};

        // WHEN
        List<RecommendedItem> recommendations = recommenderService.mostSimilarItems(itemIds, howMany);

        // THEN
        then(recommendations).isEmpty();
    }

    @Test
    @SneakyThrows
    public void should_recommend_nothing_on_missing_item() {
        // GIVEN
        int howMany = 1;
        long[] itemIds = {1L};
        willThrow(NoSuchItemException.class).given(itemBasedRecommender).mostSimilarItems(itemIds, howMany, rescorer, false);

        // WHEN
        List<RecommendedItem> recommendations = recommenderService.mostSimilarItems(itemIds, howMany);

        // THEN
        then(recommendations).isEmpty();
    }

    @Test
    @SneakyThrows
    public void should_throw_on_TasteException() {
        // GIVEN
        int howMany = 1;
        long[] itemIds = {1L};
        willThrow(TasteException.class).given(itemBasedRecommender).mostSimilarItems(itemIds, howMany, rescorer, false);

        // WHEN
        ThrowingCallable throwable = () -> recommenderService.mostSimilarItems(itemIds, howMany);

        // THEN
        thenThrownBy(throwable).isInstanceOf(RecommenderException.class);
    }
}
