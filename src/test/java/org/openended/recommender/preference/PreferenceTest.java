package org.openended.recommender.preference;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.BDDAssertions.then;

import java.util.Set;

import org.junit.Test;

public class PreferenceTest {

    @Test
    public void should_test_toString() {
        // GIVEN
        Preference preference = new Preference(47L, 11L);

        // WHEN
        String toString = preference.toString();

        // THEN
        then(toString).contains("userId=47").contains("itemId=11");
    }

    @Test
    public void should_test_equals_and_hashcode() {
        // GIVEN
        Preference preference1 = new Preference(47L, 11L);
        Preference preference2 = new Preference(47L, 11L);

        // WHEN
        Set<Preference> preferences = newHashSet(preference1, preference2);

        // THEN
        then(preferences).hasSize(1);
    }
}
