package org.openended.recommender;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyVararg;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import lombok.SneakyThrows;

@AutoConfigureMockMvc
@RecommenderIntegrationTest
@RunWith(SpringRunner.class)
public class RecommenderEndpointTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RecommenderService recommenderService;

    @Test
    @SneakyThrows
    public void should_render_recommendations() {
        // GIVEN
        UUID item = UUID.fromString("91d2e84f-6eca-472f-8954-ade8486ad44f");
        UUID reco1 = UUID.fromString("e0826ddb-f20b-4092-a958-595949db611f");
        UUID reco2 = UUID.fromString("1aab4ed2-6608-422f-8724-a242ff2411a1");
        given(recommenderService.recommend(anyVararg()))
                .willReturn(newArrayList(reco1, reco2));

        // WHEN
        ResultActions resultActions = mvc.perform(get("/recommender/recommendations")
                .param("item", item.toString())
                .accept(APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(content().json("{\"items\":[\"91d2e84f-6eca-472f-8954-ade8486ad44f\"],\"recommendations\":[\"e0826ddb-f20b-4092-a958-595949db611f\",\"1aab4ed2-6608-422f-8724-a242ff2411a1\"]}"));
    }

    @Test
    @SneakyThrows
    public void should_handle_recommender_exception() {
        // GIVEN
        UUID item = UUID.fromString("91d2e84f-6eca-472f-8954-ade8486ad44f");
        given(recommenderService.recommend(anyVararg()))
                .willThrow(new RecommenderException("test", new TasteException()));

        // WHEN
        ResultActions resultActions = mvc.perform(get("/recommender/recommendations")
                .param("item", item.toString())
                .accept(APPLICATION_JSON))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(content().json("{\"items\":[\"91d2e84f-6eca-472f-8954-ade8486ad44f\"],\"recommendations\":[]}"));
    }
}
