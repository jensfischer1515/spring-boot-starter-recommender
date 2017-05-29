package org.openended.recommender.preference;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PreferenceService {
    void removeByItemUuid(UUID itemUuid);

    List<Preference> saveFromUserUuid(UUID userUuid, Map<UUID, Integer> itemQuantities);
}
