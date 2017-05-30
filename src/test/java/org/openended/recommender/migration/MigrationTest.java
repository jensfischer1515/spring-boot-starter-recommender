package org.openended.recommender.migration;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;
import java.util.UUID;

import org.junit.Test;

public class MigrationTest {

    @Test
    public void should_test_toString() {
        // GIVEN
        Migration migration = new Migration(new UUID(1L, 1L));

        // WHEN
        String toString = migration.toString();

        // THEN
        then(toString).contains("id=1").contains("uuid=00000000-0000-0001-0000-000000000001");
    }

    @Test
    public void should_test_equals_and_hashcode() {
        // GIVEN
        Migration migration1 = new Migration(new UUID(0L, 0L));
        Migration migration2 = new Migration(new UUID(0L, 0L));

        // WHEN
        Set<Migration> migrations = newHashSet(migration1, migration2);

        // THEN
        then(migrations).hasSize(1);
    }
}
