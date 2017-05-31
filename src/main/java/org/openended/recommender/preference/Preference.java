package org.openended.recommender.preference;

import static lombok.AccessLevel.PACKAGE;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * <pre>
 * CREATE TABLE taste_preferences (
 *   user_id BIGINT NOT NULL,
 *   item_id BIGINT NOT NULL,
 *   preference FLOAT NOT NULL,
 *   PRIMARY KEY (user_id, item_id),
 *   INDEX (user_id),
 *   INDEX (item_id)
 * )
 *
 * <p>The table may optionally have a {@code timestamp} column whose type is compatible with Java
 * {@code long}.
 * </p>
 *
 * See {@link org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel}
 */
@Getter
@ToString(of = {"userId", "itemId"})
@EqualsAndHashCode(of = {"userId", "itemId"}, callSuper = false)
@AllArgsConstructor(access = PACKAGE)
public class Preference {

    private final long userId;

    private final long itemId;

    private final double preference;

    private final long timestamp;

    Preference(long userId, long itemId) {
        this(userId, itemId, 0.0);
    }

    Preference(long userId, long itemId, double preference) {
        this(userId, itemId, preference, System.currentTimeMillis());
    }
}
