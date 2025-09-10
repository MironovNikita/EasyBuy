package com.shop.easybuy.utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Utils {

    public <T> List<List<T>> splitList(List<T> listToSplit, int rowSize) {
        if (rowSize <= 0) {
            throw new IllegalArgumentException("Передан некорректный параметр длины строки массива: %s".formatted(rowSize));
        }

        List<List<T>> result = new ArrayList<>();

        for (int i = 0; i < listToSplit.size(); i += rowSize) {
            result.add(new ArrayList<>(listToSplit.subList(i, Math.min(listToSplit.size(), i + rowSize))));
        }
        return result;
    }
}
