package ru.sberbank.counter.service;

import lombok.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.sberbank.counter.model.Counter;
import ru.sberbank.counter.model.CounterSum;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Service
public class CounterService {

    private static final String SUM_TEXT = "Сумма всех счетчиков";
    private static final String NOT_FOUND_TEXT = "Нет значений для счетчика с таким id";

    private static final ConcurrentHashMap<String, LongAdder> counters = new ConcurrentHashMap<>();
    private static final LongAdder sum = new LongAdder();
    private static final LongAdder deletedSum = new LongAdder();

    public List<String> getAll() {
        return counters.keySet().stream()
                        .sorted()
                        .collect(Collectors.toList());
    }

    public Counter getOne(@NonNull String id) {
        return validateExistence(id, counters.get(id));
    }

    public void increment(@NonNull String id) {
        counters.computeIfAbsent(id, key -> new LongAdder()).increment();
        sum.increment();
    }

    public Counter delete(@NonNull String id) {
        LongAdder counter = counters.remove(id);
        return validateExistence(id, counter, () -> deletedSum.add(counter.longValue()));
    }

    public CounterSum summarize() {
        return new CounterSum(SUM_TEXT, String.valueOf(sum.longValue() - deletedSum.longValue()));
    }

    private Counter validateExistence(@NonNull String id,
                                      @Nullable LongAdder counter) {
        return validateExistence(id, counter, null);
    }

    private Counter validateExistence(@NonNull String id,
                                      @Nullable LongAdder counter,
                                      @Nullable Runnable additionalOperations) {
        if (counter != null) {
            long counterValue = counter.longValue();
            if (additionalOperations != null) {
                additionalOperations.run();
            }
            return new Counter(id, String.valueOf(counterValue), true);
        }
        return new Counter(id, NOT_FOUND_TEXT, false);
    }

}
