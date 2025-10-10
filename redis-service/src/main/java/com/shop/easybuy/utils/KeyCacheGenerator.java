package com.shop.easybuy.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@UtilityClass
public class KeyCacheGenerator {

    public static String generateKey(String search,
                                     String sort,
                                     Integer pageSize,
                                     Integer pageNumber) {
        String key = String.format("main:%s:%s:%d:%d",
                search == null ? "" : search.trim().toLowerCase(),
                sort == null ? "NO" : sort,
                pageSize,
                pageNumber
        );

        return DigestUtils.md5DigestAsHex(key.getBytes(StandardCharsets.UTF_8));
    }
}
