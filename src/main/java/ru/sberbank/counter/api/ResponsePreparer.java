package ru.sberbank.counter.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.sberbank.counter.model.Counter;

public abstract class ResponsePreparer {

    public static ResponseEntity<Counter> prepareResponse(Counter counter) {
        if (counter.isExist()) {
            return ResponseEntity.status(HttpStatus.OK).body(counter);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(counter);
    }
}
