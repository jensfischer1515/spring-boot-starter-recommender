package org.openended.recommender.preference;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openended.recommender.RecommenderIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RecommenderIntegrationTest
@RunWith(SpringRunner.class)
public class PreferenceRepositoryTest {

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void should_find_by_item_id() {
        // GIVEN
        preferenceRepository.save(new Preference(47L, 10L));
        preferenceRepository.save(new Preference(47L, 11L));
        preferenceRepository.save(new Preference(48L, 11L));

        // WHEN
        List<Preference> preferences = preferenceRepository.findByItemId(11L);

        // THEN
        then(preferences)
                .extracting(Preference::getUserId)
                .contains(47L, 48L);
    }

    @Test
    public void should_find_preference_by_user_and_item() {
        // GIVEN
        preferenceRepository.save(new Preference(47L, 11L, 99.0));

        // WHEN
        double preference = preferenceRepository.findPreferenceByUserIdAndItemId(47L, 11L);

        // THEN
        then(preference).isEqualTo(99.0);
    }

    @Test
    public void should_not_find_preference_by_user_and_item() {
        // GIVEN
        preferenceRepository.save(new Preference(47L, 11L, 99.0));

        // WHEN
        double preference = preferenceRepository.findPreferenceByUserIdAndItemId(0L, 11L);

        // THEN
        then(preference).isZero();
    }
}
