package org.openended.recommender.migration;

import static lombok.AccessLevel.PACKAGE;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * <pre>
 * CREATE TABLE taste_id_migration (
 *   long_id BIGINT NOT NULL PRIMARY KEY,
 *   string_id VARCHAR(36) NOT NULL UNIQUE
 * )
 * </pre>
 * <p>
 * See {@link org.apache.mahout.cf.taste.impl.model.MySQLJDBCIDMigrator}.
 */
@Getter
@ToString(of = {"id", "uuid"})
@EqualsAndHashCode(of = "id", callSuper = false)
@AllArgsConstructor(access = PACKAGE)
public class Migration {

    private final long id;

    private final UUID uuid;

    Migration(UUID uuid) {
        this(toId(uuid), uuid);
    }

    /**
     * reducing 128-bit UUID to 64-bit long could lead to id clashes. For item recommendations
     * this is acceptable, because it will only slightly impact the recommendation quality.
     */
    public static long toId(UUID uuid) {
        return uuid.getMostSignificantBits();
    }
}
