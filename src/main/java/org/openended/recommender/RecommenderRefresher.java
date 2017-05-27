package org.openended.recommender;

import static com.google.common.collect.Lists.newArrayList;

import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RecommenderRefresher implements ApplicationListener<ApplicationReadyEvent> {

    // TODO use @Value
    private static final int FIVE_MINUTES = 5 * 60 * 1_000;

    @NonNull
    private final ItemBasedRecommender itemBasedRecommender;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        refresh();
    }

    @Scheduled(initialDelay = FIVE_MINUTES, fixedRate = FIVE_MINUTES)
    public void refresh() {
        log.info("refreshing recommender");
        itemBasedRecommender.refresh(newArrayList());
    }
}
