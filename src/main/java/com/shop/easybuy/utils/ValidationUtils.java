package com.shop.easybuy.utils;

import com.shop.easybuy.common.entity.ActionEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ValidationUtils {

    public String validateSearch(String search) {
        int maxSearchLength = 20;

        if (search == null || search.length() > maxSearchLength) {
            log.error("Строка поиска заполнена некорректно, либо превышен лимит символов для строки поиска: {}", maxSearchLength);
            throw new IllegalArgumentException("Строка поиска заполнена некорректно, либо превышен лимит символов для строки поиска: %d"
                    .formatted(maxSearchLength));
        }
        return search;
    }

    public ActionEnum validateAction(String action) {
        if (action == null) {
            log.error("Параметр action отсутствует");
            throw new IllegalArgumentException("Параметр action отсутствует");
        }

        ActionEnum actionEnum;
        try {
            actionEnum = ActionEnum.valueOf(action);
        } catch (IllegalArgumentException e) {
            log.error("Некорректное значение action при добавлении в корзину: {}", action);
            throw new IllegalArgumentException("Некорректное значение action при добавлении в корзину: " + action);
        }
        return actionEnum;
    }
}
