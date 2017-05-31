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
import org.openended.recommender.migration.MigrationRepository;
import org.openended.recommender.preference.ItemPreference;
import org.openended.recommender.preference.PreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

@RecommenderIntegrationTest(properties = {
        "logging.level.jdbc.sqlonly=INFO",
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
    private MigrationRepository migrationRepository;

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

    @Test
    @Transactional
    public void should_recommend_items() {
        // GIVEN
        UUID[] items = {findItemWithHighestPreference()};

        // WHEN
        List<UUID> recommendations = recommenderService.recommend(items, 10);

        // THEN
        then(recommendations.size()).isGreaterThan(0);
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

    private UUID findItemWithHighestPreference() {
        String sql = "select item_id from taste_preferences group by item_id order by sum(preference) desc limit 1";
        ResultSetExtractor<Long> extractor = rs -> rs.next() ? rs.getLong("item_id") : null;
        Long itemId = jdbcTemplate.query(sql, extractor);
        return migrationRepository.lookupUuids(itemId).stream().findFirst().orElseThrow(IllegalStateException::new);
    }
}
