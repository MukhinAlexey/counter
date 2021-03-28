package ru.sberbank.counter.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.counter.model.Counter;
import ru.sberbank.counter.model.CounterSum;
import ru.sberbank.counter.service.CounterService;

import java.util.List;

import static ru.sberbank.counter.api.ResponsePreparer.prepareResponse;

@RestController
@RequestMapping("/v2")
@RequiredArgsConstructor
public class CounterController {

    private static final String ALL_COUNTERS_URL = "counters";
    private static final String ONE_COUNTER_URL = "counters/{id}";
    private static final String SUM_URL = "sum";
    private static final String ID = "id";

    private final CounterService counterService;

    @GetMapping(ALL_COUNTERS_URL)
    public List<String> getAll() {
        return counterService.getAll();
    }

    @GetMapping(ONE_COUNTER_URL)
    public ResponseEntity<Counter> getOne(@PathVariable(ID) String id) {
        return prepareResponse(counterService.getOne(id));
    }

    @PostMapping(ONE_COUNTER_URL)
    public void increment(@PathVariable(ID) String id) {
        counterService.increment(id);
    }

    @DeleteMapping(ONE_COUNTER_URL)
    public ResponseEntity<Counter> delete(@PathVariable(ID) String id) {
        return prepareResponse(counterService.delete(id));
    }

    @GetMapping(SUM_URL)
    public CounterSum summarize() {
        return counterService.summarize();
    }

}
