package org.openended.recommender.migration;

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
public class MigrationRepositoryTest {

    @Autowired
    private MigrationRepository migrationRepository;

    @Test
    public void should_find_uuids_by_ids() {
        // GIVEN
        long id1 = migrationRepository.save(randomUUID()).getId();
        long id2 = migrationRepository.save(randomUUID()).getId();
        long id3 = migrationRepository.save(randomUUID()).getId();

        // WHEN
        List<UUID> migrations = migrationRepository.lookupUuids(id1, id2, id3);

        // THEN
        then(migrations).hasSize(3);
    }
}
