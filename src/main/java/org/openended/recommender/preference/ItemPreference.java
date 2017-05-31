package org.openended.recommender.preference;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ItemPreference {
    private final UUID item;

    private final double preference;
}
