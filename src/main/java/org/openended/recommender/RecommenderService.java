package org.openended.recommender;

import java.util.List;
import java.util.UUID;

public interface RecommenderService {
    List<UUID> recommend(UUID[] itemUuids, int count);
}
