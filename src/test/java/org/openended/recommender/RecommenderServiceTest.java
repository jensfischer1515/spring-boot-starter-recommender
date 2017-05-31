package org.openended.recommender;

import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.BDDAssertions.then;
import static org.springframework.test.jdbc.JdbcTestUtils.deleteFromTables;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openended.recommender.preference.ItemPreference;
import org.openended.recommender.preference.PreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

@RecommenderIntegrationTest(properties = {
        "logging.level.jdbc.sqlonly=WARN",
        "logging.level.jdbc.resultsettable=WARN",
        "logging.level.org.openended.recommender=INFO"
})
@RunWith(SpringRunner.class)
public class RecommenderServiceTest {

    @Autowired
    private RecommenderService recommenderService;

    @Autowired
    private PreferenceService preferenceService;

    @Autowired
    private RecommenderRefresher recommenderRefresher;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Random random;

    private List<UUID> items;

    @BeforeTransaction
    public void setup() {
        random = new Random();
        givenItems(100);
        givenPreferences(1_000);
        recommenderRefresher.refresh();
    }

    @AfterTransaction
    public void teardown() {
        deleteFromTables(jdbcTemplate, "taste_preferences", "taste_id_migration");
    }

    private void givenItems(int count) {
        items = newArrayListWithCapacity(count);
        IntStream.rangeClosed(1, count)
                .forEach(i -> items.add(randomUUID()));
    }

    private void givenPreferences(int count) {
        IntStream.rangeClosed(1, count)
                .forEach(i -> givenPreference(randomUser(), randomItemPreference(), randomItemPreference()));
    }

    private void givenPreference(UUID user, ItemPreference... itemPreferences) {
        preferenceService.saveFromUser(user, itemPreferences);
    }

    private UUID randomUser() {
        return randomUUID();
    }

    private UUID randomItem() {
        return items.get(random.nextInt(items.size()));
    }

    private int randomQuantity(int max) {
        return random.nextInt(max) + 1;
    }

    private ItemPreference randomItemPreference() {
        return new ItemPreference(randomItem(), randomQuantity(5));
    }

    @Test
    @Transactional
    public void should_recommend_items() {
        // GIVEN
        UUID[] items = {randomItem()};

        // WHEN
        List<UUID> recommendations = recommenderService.recommend(items, 10);

        // THEN
        then(recommendations.size()).isGreaterThan(0);
    }
}
