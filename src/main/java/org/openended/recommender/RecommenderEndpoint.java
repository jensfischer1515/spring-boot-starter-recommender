package org.openended.recommender;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.stream;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.actuate.endpoint.mvc.AbstractNamedMvcEndpoint;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecommenderEndpoint extends AbstractNamedMvcEndpoint {

    @NonNull
    private final RecommenderService recommenderService;

    public RecommenderEndpoint(RecommenderProperties recommenderProperties, RecommenderService recommenderService) {
        super("recommender", recommenderProperties.getEndpointPath(), recommenderProperties.isEndpointSensitive());
        this.recommenderService = checkNotNull(recommenderService);
    }

    @ExceptionHandler(RecommenderException.class)
    @ResponseBody
    public Recommendations handleRecommenderException(WebRequest request) {
        UUID[] items = stream(request.getParameterValues("item"))
                .map(UUID::fromString)
                .toArray(UUID[]::new);
        log.warn("No recommendations found for {}", Arrays.asList(items));
        return new Recommendations(items, newArrayList());
    }

    @GetMapping(path = "/recommendations", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Recommendations recommendations(@RequestParam(name = "item") UUID[] items,
                                           @RequestParam(name = "count", defaultValue = "10") int count
    ) {
        List<UUID> recommendations = recommenderService.recommend(items, count);
        return new Recommendations(items, recommendations);
    }

    @Getter
    @RequiredArgsConstructor
    private static class Recommendations {
        private final List<UUID> items;

        private final List<UUID> recommendations;

        public Recommendations(UUID[] items, List<UUID> recommendations) {
            this(Arrays.asList(items), recommendations);
        }
    }
}
