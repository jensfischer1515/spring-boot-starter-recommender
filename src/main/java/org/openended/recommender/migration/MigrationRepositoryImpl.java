package org.openended.recommender.migration;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Longs;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

/**
 * CREATE TABLE taste_id_migration (
 * long_id BIGINT NOT NULL PRIMARY KEY,
 * string_id VARCHAR(255) NOT NULL UNIQUE
 * );
 */
@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE) // for @Wither
public class MigrationRepositoryImpl implements MigrationRepository {

    private final NamedParameterJdbcOperations operations;

    @Wither
    private String sqlSave = "merge into taste_id_migration (long_id, string_id) key(long_id) values (:id, :uuid)";

    @Wither
    private String sqlDelete = "delete from taste_id_migration where long_id = :id";

    @Wither
    private String sqlLookupUuids = "select string_id from taste_id_migration where long_id in (:ids)";

    @Override
    public List<UUID> lookupUuids(long... ids) {
        if (ids.length == 0) {
            return newArrayList();
        }

        Map<String, Object> params = ImmutableMap.of("ids", Longs.asList(ids));
        return operations.queryForList(sqlLookupUuids, params, String.class).stream()
                .map(UUID::fromString)
                .collect(toList());
    }

    @Override
    public Migration save(UUID uuid) {
        Migration migration = new Migration(uuid);
        Map<String, Object> params = ImmutableMap.of("id", migration.getId(), "uuid", migration.getUuid());
        operations.update(sqlSave, params);
        return migration;
    }

    @Override
    public void delete(long id) {
        Map<String, Object> params = ImmutableMap.of("id", id);
        operations.update(sqlDelete, params);
    }
}
