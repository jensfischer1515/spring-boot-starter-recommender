package org.openended.recommender.preference;

import java.util.List;

public interface PreferenceRepository {
    double findPreferenceByUserIdAndItemId(long userId, long itemId);

    List<Preference> findByItemId(long itemId);

    Preference save(Preference preference);

    void delete(Preference preference);
}
