package org.openended.recommender.preference;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openended.recommender.RecommenderIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableMap;

@RecommenderIntegrationTest
@RunWith(SpringRunner.class)
public class PreferenceServiceTest {

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Test
    public void should_save_from_user() {
        // GIVEN
        UUID userUuid = randomUUID();
        Map<UUID, Integer> itemQuantities = ImmutableMap.of(
                randomUUID(), 1,
                randomUUID(), 3,
                randomUUID(), 5
        );

        // WHEN
        List<Preference> preferences = preferenceService.saveFromUser(userUuid, itemQuantities);

        // THEN
        then(preferences).hasSize(3);
        then(preferences)
                .extracting(Preference::getItemId)
                .isNotNull();
        then(preferences)
                .extracting(Preference::getUserId)
                .isNotNull();
    }

    @Test
    public void should_remove_by_item_uuid() {
        // GIVEN
        UUID userUuid = randomUUID();
        UUID itemUuid = randomUUID();
        long itemId = preferenceService.saveFromUser(userUuid, ImmutableMap.of(itemUuid, 1))
                .iterator().next().getItemId();

        // WHEN
        preferenceService.removeByItem(itemUuid);
        List<Preference> preferences = preferenceRepository.findByItemId(itemId);

        // THEN
        then(preferences).isEmpty();
    }
}
