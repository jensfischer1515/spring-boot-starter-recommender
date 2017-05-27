package org.openended.recommender;

import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.UUID;

import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.boot.actuate.endpoint.mvc.AbstractNamedMvcEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnProperty(value = "recommender.endpoint.enabled", havingValue = "true", matchIfMissing = true)
public class RecommenderEndpoint extends AbstractNamedMvcEndpoint {

    @NonNull
    private final RecommenderService recommenderService;

    public RecommenderEndpoint(RecommenderService recommenderService) {
        super("recommender", "/recommender", false);
        this.recommenderService = recommenderService;
    }

    @ExceptionHandler(TasteException.class)
    public ResponseEntity<List<UUID>> handleTasteException(@RequestParam(name = "item") UUID[] itemUuids) {
        log.warn("No item found for {}", newArrayList(itemUuids));
        return ResponseEntity.ok(newArrayList());
    }

    // TODO use @Value
    @GetMapping(path = "/mostSimilarItems", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> mostSimilarItems(@RequestParam(name = "item") UUID[] itemUuids,
                                                       @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        List<UUID> mostSimilarItems = recommenderService.mostSimilarItems(itemUuids, count);
        return ResponseEntity.ok(mostSimilarItems);
    }
}
