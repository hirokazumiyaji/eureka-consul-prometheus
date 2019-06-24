package com.github.hirokazumiyaji.eureka.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Item<T> {
    T item;
    private long index;
}
