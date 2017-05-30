package org.openended.recommender;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.stream;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.util.List;
import java.util.UUID;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.common.LongPair;
import org.openended.recommender.migration.Migration;
import org.openended.recommender.migration.MigrationRepository;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RecommenderServiceImpl implements RecommenderService {

    @NonNull
    private final ItemBasedRecommender itemBasedRecommender;

    @NonNull
    private final Rescorer<LongPair> rescorer;

    @NonNull
    private final MigrationRepository migrationRepository;

    @SneakyThrows
    private boolean hasPreferences(long itemId) {
        return itemBasedRecommender.getDataModel().getPreferencesForItem(itemId).length() > 0;
    }

    @Override
    @Transactional(propagation = REQUIRED)
    public List<UUID> recommend(UUID[] itemUuids, int count) {
        long[] itemIdsWithPreferences = stream(itemUuids)
                .mapToLong(Migration::toId)
                .filter(this::hasPreferences)
                .toArray();

        long[] recommendedItemIds = mostSimilarItems(itemIdsWithPreferences, count).stream()
                .mapToLong(RecommendedItem::getItemID)
                .toArray();

        return migrationRepository.lookupUuids(recommendedItemIds);
    }

    private List<RecommendedItem> mostSimilarItems(long[] itemIds, int count) {
        if (itemIds.length == 0) {
            return newArrayList();
        }

        try {
            return itemBasedRecommender.mostSimilarItems(itemIds, count, rescorer, false);
        } catch (NoSuchItemException e) {
            log.warn("items {} not found", itemIds, e);
            return newArrayList();
        } catch (TasteException e) {
            throw new RecommenderException("Error fetching most similar items", e);
        }
    }
}
