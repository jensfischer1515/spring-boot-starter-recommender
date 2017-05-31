package org.openended.recommender.preference;

import java.util.List;
import java.util.UUID;

public interface PreferenceService {
    void removeByItem(UUID item);

    List<Preference> saveFromUser(UUID user, ItemPreference... itemPreferences);
}
