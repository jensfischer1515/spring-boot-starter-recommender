package org.openended.recommender.preference;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.openended.recommender.migration.Migration.toId;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.util.List;
import java.util.UUID;

import org.openended.recommender.migration.MigrationRepository;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

    @NonNull
    private final PreferenceRepository preferenceRepository;

    @NonNull
    private final MigrationRepository migrationRepository;

    @Override
    @Transactional(propagation = REQUIRED)
    public void removeByItem(UUID item) {
        long itemId = toId(item);
        log.debug("Deleting item '{}'", item);
        migrationRepository.delete(itemId);
        preferenceRepository.findByItemId(itemId).forEach(preference -> {
            long userId = preference.getUserId();
            log.debug("Deleting userId '{}'", userId);
            migrationRepository.delete(userId);
            log.debug("Deleting preference '{}'", preference);
            preferenceRepository.delete(preference);
        });
    }

    @Override
    @Transactional(propagation = REQUIRED)
    public List<Preference> saveFromUser(UUID user, ItemPreference... itemPreferences) {
        log.debug("Creating user '{}'", user);
        migrationRepository.save(user);
        return stream(itemPreferences)
                .map(itemPreference -> save(user, itemPreference.getItem(), itemPreference.getPreference()))
                .collect(toList());
    }

    private Preference save(UUID user, UUID item, double toAdd) {
        long userId = toId(user);
        long itemId = toId(item);

        double current = preferenceRepository.findPreferenceByUserIdAndItemId(userId, itemId);
        Preference preference = preferenceRepository.save(new Preference(userId, itemId, current + toAdd));
        log.debug("Creating item '{}'", item);
        migrationRepository.save(item);
        return preference;
    }
}
