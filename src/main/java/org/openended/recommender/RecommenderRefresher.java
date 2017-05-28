package org.openended.recommender;

import static com.google.common.collect.Lists.newArrayList;

import org.apache.mahout.cf.taste.recommender.Recommender;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RecommenderRefresher implements ApplicationListener<ApplicationReadyEvent> {

    @NonNull
    private final Recommender recommender;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        refresh();
    }

    public void refresh() {
        log.info("refreshing recommender");
        recommender.refresh(newArrayList());
    }
}
