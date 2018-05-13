package org.openended.recommender;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;

import java.util.List;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.common.LongPair;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Before;
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
    private RecommenderProperties recommenderProperties;

    @Mock
    private ItemBasedRecommender itemBasedRecommender;

    @Mock
    private Rescorer<LongPair> rescorer;

    @Mock
    private MigrationRepository migrationRepository;

    @Before
    public void setup() {
        willReturn(10).given(recommenderProperties).getHowMany();
    }

    @Test
    public void should_recommend_nothing_on_empty_item_preferences() {
        // GIVEN
        long[] itemIds = {};

        // WHEN
        List<RecommendedItem> recommendations = recommenderService.mostSimilarItems(itemIds);

        // THEN
        then(recommendations).isEmpty();
    }

    @Test
    @SneakyThrows
    public void should_recommend_nothing_on_missing_item() {
        // GIVEN
        long[] itemIds = {1L};
        willThrow(NoSuchItemException.class).given(itemBasedRecommender).mostSimilarItems(itemIds, 10, rescorer, false);

        // WHEN
        List<RecommendedItem> recommendations = recommenderService.mostSimilarItems(itemIds);

        // THEN
        then(recommendations).isEmpty();
    }

    @Test
    @SneakyThrows
    public void should_throw_on_TasteException() {
        // GIVEN
        long[] itemIds = {1L};
        willThrow(TasteException.class).given(itemBasedRecommender).mostSimilarItems(itemIds, 10, rescorer, false);

        // WHEN
        ThrowingCallable throwable = () -> recommenderService.mostSimilarItems(itemIds);

        // THEN
        thenThrownBy(throwable).isInstanceOf(RecommenderException.class);
    }
}
