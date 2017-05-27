package org.openended.recommender.migration;

import java.util.List;
import java.util.UUID;

public interface MigrationRepository {
    List<UUID> lookupUuids(long... ids);

    Migration save(UUID uuid);

    void delete(long id);
}
