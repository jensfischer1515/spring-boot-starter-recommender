package org.openended.recommender.preference;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class PreferenceServiceTest {

    @Autowired
    private PreferenceService preferenceService;

    @Test
    public void should_save_from_order() {
        // GIVEN
        UUID orderId = randomUUID();
        Map<UUID, Integer> productQuantities = ImmutableMap.of(
                randomUUID(), 1,
                randomUUID(), 3,
                randomUUID(), 5
        );

        // WHEN
        List<Preference> preferences = preferenceService.saveFromUserUUID(orderId, productQuantities);

        // THEN
        then(preferences).hasSize(3);
        then(preferences)
                .extracting(Preference::getItemId)
                .isNotNull();
        then(preferences)
                .extracting(Preference::getUserId)
                .isNotNull();
    }
}