package org.openended.recommender.preference;

import static java.util.stream.Collectors.toList;
import static org.openended.recommender.migration.Migration.toId;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

import java.util.List;
import java.util.Map;
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
    public void removeByItemUuid(UUID itemUuid) {
        long itemId = toId(itemUuid);
        log.debug("Deleting item '{}'", itemUuid);
        migrationRepository.delete(itemId);
        preferenceRepository.findByItemId(itemId).forEach(preference -> {
            long userId = preference.getUserId();
            log.debug("Deleting user '{}'", userId);
            migrationRepository.delete(userId);
            log.debug("Deleting preference '{}'", preference);
            preferenceRepository.delete(preference);
        });
    }

    @Override
    @Transactional(propagation = REQUIRED)
    public List<Preference> saveFromUserUUID(UUID userUuid, Map<UUID, Integer> itemQuantities) {
        log.debug("Creating user '{}'", userUuid);
        migrationRepository.save(userUuid);
        return itemQuantities.entrySet().stream()
                .map(entry -> save(userUuid, entry.getKey(), entry.getValue()))
                .collect(toList());
    }

    private Preference save(UUID userUuid, UUID itemUuid, double toAdd) {
        long userId = toId(userUuid);
        long itemId = toId(itemUuid);

        double current = preferenceRepository.findPreferenceByUserIdAndItemId(userId, itemId);
        Preference preference = preferenceRepository.save(new Preference(userId, itemId, current + toAdd));
        log.debug("Creating item '{}'", itemUuid);
        migrationRepository.save(itemUuid);
        return preference;
    }
}
