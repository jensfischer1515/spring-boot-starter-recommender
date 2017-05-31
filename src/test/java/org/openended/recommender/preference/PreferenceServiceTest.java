package org.openended.recommender.preference;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openended.recommender.RecommenderIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

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
        UUID user = randomUUID();
        ItemPreference[] itemPreferences = {
                new ItemPreference(randomUUID(), 1.0),
                new ItemPreference(randomUUID(), 3.0),
                new ItemPreference(randomUUID(), 5.0)
        };

        // WHEN
        List<Preference> preferences = preferenceService.saveFromUser(user, itemPreferences);

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
    public void should_remove_by_item() {
        // GIVEN
        UUID user = randomUUID();
        UUID item = randomUUID();
        long itemId = preferenceService.saveFromUser(user, new ItemPreference(item, 1.0))
                .iterator().next().getItemId();

        // WHEN
        preferenceService.removeByItem(item);
        List<Preference> preferences = preferenceRepository.findByItemId(itemId);

        // THEN
        then(preferences).isEmpty();
    }
}
