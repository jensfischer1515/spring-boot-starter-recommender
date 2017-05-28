package org.openended.recommender.preference;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.google.common.collect.ImmutableMap;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

@RequiredArgsConstructor
@AllArgsConstructor(access = PRIVATE) // for @Wither
public class PreferenceRepositoryImpl implements PreferenceRepository {

    @NonNull
    private final NamedParameterJdbcOperations jdbcOperations;

    @Wither
    private String sqlSave = "merge into taste_preferences (user_id, item_id, preference, timestamp) key(user_id, item_id) values (:userId, :itemId, :preference, :timestamp)";

    @Wither
    private String sqlDelete = "delete from taste_preferences where user_id = :userId and item_id = :itemId";

    @Wither
    private String sqlFindPreferenceByUserIdAndItemId = "select preference from taste_preferences where user_id = :userId and item_id = :itemId";

    @Wither
    private String sqlFindByItemId = "select user_id, item_id, preference, timestamp from taste_preferences where item_id = :itemId";

    @Override
    public double findPreferenceByUserIdAndItemId(long userId, long itemId) {
        Map<String, Object> params = ImmutableMap.of(
                "userId", userId,
                "itemId", itemId
        );

        ResultSetExtractor<Double> extractor = rs -> rs.next() ? rs.getDouble("preference") : 0.0;
        return jdbcOperations.query(sqlFindPreferenceByUserIdAndItemId, params, extractor);
    }

    @Override
    public List<Preference> findByItemId(long itemId) {
        Map<String, Object> params = ImmutableMap.of("itemId", itemId);

        return jdbcOperations.queryForList(sqlFindByItemId, params).stream()
                .map(row -> {
                    long userId = Long.parseLong(row.get("user_id").toString());
                    double preference = Double.parseDouble(row.get("preference").toString());
                    long timestamp = Long.parseLong(row.get("timestamp").toString());
                    return new Preference(userId, itemId, preference, timestamp);
                })
                .collect(toList());
    }

    @Override
    public Preference save(Preference preference) {
        Map<String, Object> params = ImmutableMap.of(
                "userId", preference.getUserId(),
                "itemId", preference.getItemId(),
                "preference", preference.getPreference(),
                "timestamp", preference.getTimestamp()
        );
        jdbcOperations.update(sqlSave, params);
        return preference;
    }

    @Override
    public void delete(Preference preference) {
        Map<String, Object> params = ImmutableMap.of(
                "userId", preference.getUserId(),
                "itemId", preference.getItemId()
        );
        jdbcOperations.update(sqlDelete, params);
    }
}
