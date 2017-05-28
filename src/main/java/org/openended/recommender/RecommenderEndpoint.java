package org.openended.recommender;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.UUID;

import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.boot.actuate.endpoint.mvc.AbstractNamedMvcEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecommenderEndpoint extends AbstractNamedMvcEndpoint {

    private final RecommenderService recommenderService;

    public RecommenderEndpoint(RecommenderProperties recommenderProperties, RecommenderService recommenderService) {
        super("recommender", recommenderProperties.getEndpointPath(), recommenderProperties.isEndpointSensitive());
        this.recommenderService = checkNotNull(recommenderService);
    }

    @ExceptionHandler(TasteException.class)
    public ResponseEntity<List<UUID>> handleTasteException(@RequestParam(name = "item") UUID[] itemUuids) {
        log.warn("No item found for {}", newArrayList(itemUuids));
        return ResponseEntity.ok(newArrayList());
    }

    @GetMapping(path = "/recommendations", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> itemRecommendations(@RequestParam(name = "item") UUID[] itemUuids,
                                                          @RequestParam(name = "count", defaultValue = "10") int count
    ) {
        List<UUID> mostSimilarItems = recommenderService.recommend(itemUuids, count);
        return ResponseEntity.ok(mostSimilarItems);
    }
}
