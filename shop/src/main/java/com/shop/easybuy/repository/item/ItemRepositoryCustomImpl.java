package com.shop.easybuy.repository.item;

import com.shop.easybuy.entity.item.ItemRsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final DatabaseClient client;

    @Override
    public Flux<ItemRsDto> findAllByTitleOrDescription(String search, int limit, long offset, Sort sort, Long userId) {
        String baseQuery = """
                SELECT i.id,
                i.title,
                i.description,
                i.image,
                COALESCE(c.quantity, 0) AS count,
                i.price
                FROM items i
                LEFT JOIN cart c ON i.id = c.item_id AND c.user_id = :userId
                WHERE i.title ILIKE CONCAT('%', :search, '%') OR i.description ILIKE CONCAT('%', :search, '%')
                """;

        String orderBy = "";

        if (sort != null && sort.stream().iterator().hasNext()) {
            Sort.Order order = sort.iterator().next();
            String property = order.getProperty();
            String direction = order.isAscending() ? "ASC" : "DESC";

            switch (property) {
                case "title" -> orderBy = "ORDER BY i.title " + direction;
                case "price" -> orderBy = "ORDER BY i.price " + direction;
                default -> orderBy = "";
            }
        }

        String pagination = " LIMIT :limit OFFSET :offset";

        String finalQuery = baseQuery + orderBy + pagination;

        return client.sql(finalQuery)
                .bind("search", search)
                .bind("limit", limit)
                .bind("offset", offset)
                .bind("userId", userId)
                .map((row, metadata) -> new ItemRsDto(
                                row.get("id", Long.class),
                                row.get("title", String.class),
                                row.get("description", String.class),
                                row.get("image", String.class),
                                row.get("count", Long.class),
                                row.get("price", Long.class)
                        )
                ).all();
    }
}
