package ru.sberbank.counter.model;

import lombok.Value;

@Value
public class Counter {
    String id;
    String value;
    boolean isExist;
}
