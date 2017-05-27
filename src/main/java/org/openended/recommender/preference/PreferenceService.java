package org.openended.recommender.preference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PreferenceService {
    void removeByItemUuid(UUID productId);

    List<Preference> saveFromUserUUID(UUID orderId, Map<UUID, Integer> productQuantities);
}
