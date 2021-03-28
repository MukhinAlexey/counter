package ru.sberbank.counter;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CounterApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void clean() throws Exception {
        MvcResult countersIdsJson = mockMvc
                .perform(get("/v2/counters"))
                .andExpect(status().isOk())
                .andReturn();

        JSONArray countersIdsArray =
                JsonPath.read(countersIdsJson.getResponse().getContentAsString(), "$");

        countersIdsArray.forEach(this::deleteCounter);

        createTwoCounters();
    }

    private void deleteCounter(Object counterId) {
        try {
            mockMvc.perform(delete("/v2/counters/".concat((String) counterId)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTwoCounters() throws Exception {
        mockMvc
                .perform(post("/v2/counters/counter_1"))
                .andExpect(status().isOk());
        mockMvc
                .perform(post("/v2/counters/counter_2"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAll() throws Exception {
        mockMvc
                .perform(get("/v2/counters"))
                .andExpect(jsonPath("$", hasItem("counter_1")))
                .andExpect(jsonPath("$", hasItem("counter_2")))
                .andExpect(status().isOk());
    }

    @Test
    public void getOne() throws Exception {
        mockMvc
                .perform(get("/v2/counters/counter_1"))
                .andExpect(jsonPath("$.id").value("counter_1"))
                .andExpect(jsonPath("$.value").value("1"))
                .andExpect(status().isOk());
    }

    @Test
    public void increment() throws Exception {
        mockMvc
                .perform(post("/v2/counters/counter_1"))
                .andExpect(status().isOk());

        mockMvc
                .perform(get("/v2/counters/counter_1"))
                .andExpect(jsonPath("$.id").value("counter_1"))
                .andExpect(jsonPath("$.value").value("2"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCounter() throws Exception {
        mockMvc
                .perform(delete("/v2/counters/counter_1"))
                .andExpect(status().isOk());

        mockMvc
                .perform(get("/v2/counters/counter_1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void sum() throws Exception {
        mockMvc
                .perform(get("/v2/sum"))
                .andExpect(jsonPath("$.sum").value("2"))
                .andExpect(status().isOk());
    }
}
