

<p align="center">

  <img width="128" height="128" src="https://github.com/MironovNikita/EasyBuy/blob/main/images/logo.png">

</p>

# 🛍️ Интернет-магазин Easy Buy
Это веб-приложение — интернет-магазин, реализованный на Java 21 с использованием Spring Framework версии 6.1 и выше (со Spring Boot), которое может работать в любом современном сервлет-контейнере (Netty). Проект управляется с помощью системы сборки Gradle. Проект основан на реактивном стеке Spring WebFlux и R2DBC, что позволило реализовать полностью неблокирующее взаимодействие с базой данных и построить высокопроизводительное масштабируемое веб-приложение. Также в проекте предусмотрено развёртывание в контейнерах Docker с применением Liquibase-скриптов для БД. Об этом далее.

## 📝 Описание
Магазин состоит из шести веб-страниц:
- главная страница с отображением товаров (название, картинка, описание, цена и количество в корзине). Также присутствует возможность добавить товар в корзину или убрать его оттуда. Страница поддерживает поиск по названию или описанию товара и имеет ряд сортировок: по алфавиту, по возрастанию и по убыванию цены;
- страница товара с отображением названия, картинки, описания, цены и количества товара в корзине. Также присутствует возможность добавить его в корзину или убрать оттуда;
- страница корзины. Либо отображает, что сама корзина пуста, либо отображает всё те же атрибуты товара с возможности оформить заказ посредством кнопки "Купить";
- страница заказа отображает все товары выбранного заказа с отображением их атрибутов, а также показывает дату и время оформления заказа;
- страница списка заказов отображает краткое описание всех имеющихся заказов: названия товаров, их количество, цена, итоговая сумма, дата и время оформления самого заказа;
- также предусмотрена страница, которая выводит на пользовательский экран сообщение об ошибке с возможностью возврата на предыдущую  или на главную страницу.

Для хранения данных используется БД PostgreSQL 17 версии. Для интеграционного тестирования применяется технология TestContainers, которая позволяет для тестирования поднять БД аналогичную основной.
Приложение покрыто unit и интеграционными тестами с использованием JUnit 5 и Spring TestContext Framework, с применением кэширования контекстов.

### 🧩 Основные сущности 📇
- [**Item**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/entity/item/Item.java) - отвечает за содержание основной информации о товаре.
- [**Order**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/entity/order/Order.java) - отвечает за содержание основной информации о заказе.
- [**CartItem**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/entity/cart/CartItem.java) - отвечает за содержание основной информации о количестве товара в корзине.

Структура таблиц базы данных представлена на схеме:

<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/schema.png">

</p>

### ⚠️ Важно ⚠️
Все таблицы в БД приложения создаются и заполняются посредством Liquibase-скриптов. Ознакомиться с ними можно [**здесь**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/resources/db/changelog/liquibase). Заполнение осуществляется только для таблицы товаров items. Соответствующие ресурсы (картинки) для них расположены [**тут**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/resources/db/item.images).

### 🚀 Запуск программы

Запустить программу можно двумя способами.

_**Классический**_:
1) Установить БД [**PostgreSQL**](https://www.postgresql.org/download/);
2) Установить Gradle;
3) Скачать проект;
4) В консоли Gradle выполнить команду **`gradle clean build`**;
5) Перейти на **http://localhost:8080/easy-buy/**;
6) Оофрмлять заказы на товары :)

####
_**С помощью Docker**_:
1) На компьютере должен быть установлен WSL;
2) Также необходимо установить [**Docker Desktop**](https://www.docker.com/products/docker-desktop/);
3) Установить Gradle;
4) Скачаьб проект;
5) Через командную строку зайти в корень проекта;
6) Открыть Docker Desktop;
7) Ввести команду **docker compose up**;
8) Наблюдать за магией;
9) Оформлять заказы на товары :)

####
После введения команды **docker compose up** получим:
```java
[+] Running 3/3
 ✔ Network easybuy_default          Created                                                                                                                                                            0.1s
 ✔ Container eb_database_container  Created                                                                                                                                                            0.1s
 ✔ Container eb_app_container       Created
```

Соответственно Docker Desktop нам отобразит, что наши контейнеры запущены и готовы к бою!
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/docker.png">

</p>

Как можем видеть из логов, наши Liquibase-скрипты отрабатывают успешно:
```java
eb_app_container       | 2025-09-30 - 22:55:09.650 (Z)  INFO 1 ---> [main] liquibase.changelog : ChangeSet db/changelog/liquibase/fill-in-items.xml::fill_data::MironovNikita ran successfully in 19ms
eb_app_container       | 2025-09-30 - 22:55:09.659 (Z)  INFO 1 ---> [main] liquibase.util : UPDATE SUMMARY
eb_app_container       | 2025-09-30 - 22:55:09.659 (Z)  INFO 1 ---> [main] liquibase.util : Run:                          5
eb_app_container       | 2025-09-30 - 22:55:09.659 (Z)  INFO 1 ---> [main] liquibase.util : Previously run:               0
eb_app_container       | 2025-09-30 - 22:55:09.659 (Z)  INFO 1 ---> [main] liquibase.util : Filtered out:                 0
eb_app_container       | 2025-09-30 - 22:55:09.659 (Z)  INFO 1 ---> [main] liquibase.util : -------------------------------
eb_app_container       | 2025-09-30 - 22:55:09.659 (Z)  INFO 1 ---> [main] liquibase.util : Total change sets:            5
eb_app_container       | 2025-09-30 - 22:55:09.660 (Z)  INFO 1 ---> [main] liquibase.util : Update summary generated
eb_app_container       | 2025-09-30 - 22:55:09.663 (Z)  INFO 1 ---> [main] liquibase.command : Update command completed successfully.
eb_app_container       | 2025-09-30 - 22:55:09.664 (Z)  INFO 1 ---> [main] liquibase.ui : Liquibase: Update has been successful. Rows affected: 11
eb_app_container       | 2025-09-30 - 22:55:09.668 (Z)  INFO 1 ---> [main] liquibase.lockservice : Successfully released change log lock
eb_app_container       | 2025-09-30 - 22:55:09.670 (Z)  INFO 1 ---> [main] liquibase.command : Command execution complete
eb_app_container       | 2025-09-30 - 22:55:11.320 (Z)  INFO 1 ---> [main] o.s.b.w.e.netty.NettyWebServer : Netty started on port 8080 (http)
```

Теперь можем перейти непосредственно к самому магазину. Главная страница выглядит так: 
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/main.png">

</p>

Отсортируем товары по алфавиту:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/mainSortAlpha.png">

</p>

Проверим сортировку по убыванию цены и поиску по заголовку/описанию:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/mainSortPrice.png">

</p>

Как видим, сразу же добавили часть товаров в корзину: 3 майки и 1 кг конфет. Перейдём в карточку товара:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/item.png">

</p>

Теперь перейдём в корзину. Как видим, сумма нашего заказа считается верно.
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/cart.png">

</p>

Оформим наш заказ.
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/order.png">

</p>

Перейдём на страницу с заказами.
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/orders.png">

</p>

Пример таблицы для сущности [**Item**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/entity/item/Item.java):
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/itemDB.png">

</p>


## 🗒️ Логирование 🔍
В приложении также предусмотрено логирование. Логи пишутся непосредственно в консоль Netty, встроенного в наш реактивный контекст. Ниже приведён пример логов:
```java
eb_app_container       | 2025-09-30 - 22:55:29.943 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.service.item.ItemServiceImpl : Запрошенный по ID 1 товар "Майка чёрная М" был найден.
eb_app_container       | 2025-09-30 - 22:55:29.948 (Z)  INFO 1 ---> [parallel-4] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/items/1 выполнен за 9 мс.
eb_app_container       | 2025-09-30 - 22:55:32.155 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.service.cart.CartServiceImpl : В корзине найдено 1 товаров.
eb_app_container       | 2025-09-30 - 22:55:32.169 (Z)  INFO 1 ---> [parallel-5] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/cart/items выполнен за 25 мс.
eb_app_container       | 2025-09-30 - 22:55:33.197 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.service.cart.CartServiceImpl : В корзине найдено 1 товаров.
eb_app_container       | 2025-09-30 - 22:55:33.220 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.service.cart.CartServiceImpl : Корзина была очищена.
eb_app_container       | 2025-09-30 - 22:55:33.221 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.service.order.OrderServiceImpl : Сформирован заказ с ID 1 и количеством товаров 1.
eb_app_container       | 2025-09-30 - 22:55:33.228 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/buy выполнен за 37 мс.
eb_app_container       | 2025-09-30 - 22:55:33.246 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.service.order.OrderServiceImpl : Найден заказ с ID 1 и количеством товаров 1.
eb_app_container       | 2025-09-30 - 22:55:33.264 (Z)  INFO 1 ---> [parallel-6] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/orders/1?newOrder=true выполнен за 30 мс.
eb_app_container       | 2025-09-30 - 22:55:34.634 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.service.order.OrderServiceImpl : Найдено 1 заказов.
eb_app_container       | 2025-09-30 - 22:55:34.643 (Z)  INFO 1 ---> [parallel-7] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/orders выполнен за 15 мс.
eb_app_container       | 2025-09-30 - 22:55:36.160 (Z)  INFO 1 ---> [reactor-tcp-epoll-10] c.s.e.service.order.OrderServiceImpl : Найден заказ с ID 1 и количеством товаров 1.
eb_app_container       | 2025-09-30 - 22:55:36.167 (Z)  INFO 1 ---> [parallel-8] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/orders/1 выполнен за 12 мс.
```

Как можно заметить, в логи также пишется и время выполнения запроса. Данную возможность предоставляет [**RequestLoggingFilter**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/common/interceptor/RequestLoggingFilter.java) - компонент Spring, реализующий **WebFilter**, для логирования времени выполнения HTTP-запросов.

1. _Назначение_
Логирует все HTTP-запросы к приложению и время их обработки, кроме ресурсов, которые не нужно трекать (картинки, favicon).

2. _Особенности реализации_
WebFilter: это реактивный аналог Servlet Filter для WebFlux.
ServerWebExchange: содержит данные запроса и ответа.
WebFilterChain: пропускает обработку дальше по цепочке.

## ✅ Тестирование 🐞
Как говорилось ранее, для интеграционного тестирования предусмотрен TestContainer для БД. С его настройками можно ознакомиться [**здесь**](https://github.com/MironovNikita/EasyBuy/tree/main/src/test/java/com/shop/easybuy/testDB).

### 1️⃣ CommonPostgresContainer
Обёртка над Testcontainers для PostgreSQL.
Стартует контейнер с Postgres и даёт доступ к URL/логину/паролю для JDBC и R2DBC.
Используется для всех интеграционных тестов, чтобы поднимать изолированную базу.

### 2️⃣ AbstractTestDatabaseInitialization
Абстрактный базовый класс для тестов, связанных с БД.
Через @DynamicPropertySource подставляет свойства R2DBC из CommonPostgresContainer.
Метод initializeLiquibase() гарантирует, что миграции Liquibase выполняются один раз перед тестами.

### 3️⃣ LiquibaseTestConfig
Конфигурация для запуска Liquibase в тестах.
Метод runLiquibase() открывает JDBC-соединение к контейнерной БД и применяет все миграции.
AtomicBoolean initialized → миграции выполняются только один раз за всю сессию тестов.

### 4️⃣ AbstractRepositoryTest
Базовый класс для тестов репозиториев R2DBC.
Аннотация @DataR2dbcTest поднимает только нужные репозитории.
Перед каждым тестом таблицы очищаются (TRUNCATE) через DatabaseClient.
Ограничение ALLOWED_TABLES → не трогать лишние таблицы.

### 5️⃣ BaseIntegrationTest
Базовый класс для интеграционных тестов через WebTestClient (т.е. полный путь контроллер → сервис → БД).
Поднимает весь контекст Spring Boot (@SpringBootTest).
Автоматически конфигурирует WebTestClient для тестирования HTTP-запросов.
Перед каждым тестом очищает нужные таблицы.
Инжектит репозитории и сервисы, чтобы можно было проверять данные напрямую после действий контроллера.

Во время выполнения тестов можем наблюдать запущенный контейнер БД:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/containerDB.png">

</p>

Стоит отметить, что контейнер поднимается единожды для всех тестов.
Тестовая БД заполняется также посредством использования Liquibase-скриптов.

Помимо заполнения данных через Liquibase-скрипты в некоторых тестах предусмотрено заполнение данных непосредственно для определённого теста. За это отвечает утилитный класс [**DataInserter**](https://github.com/MironovNikita/EasyBuy/blob/main/src/test/java/com/shop/easybuy/DataInserter.java) Пример одного из методов представлен ниже:
```java
public static Mono<Void> insertIntoCartTable(DatabaseClient client, List<CartItem> itemsInCart) {
        return Flux.fromIterable(itemsInCart)
                .flatMap(item -> client.sql(
                                        "INSERT INTO cart(item_id, quantity, added_at) VALUES(:itemId, :quantity, :addedAt)"
                                )
                                .bind("itemId", item.getItemId())
                                .bind("quantity", item.getQuantity())
                                .bind("addedAt", item.getAddedAt())
                                .fetch()
                                .rowsUpdated()
                )
                .then();
    }
```
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/tests.png">

</p>

Все зависимости проекта можно найти в [**build.gradle**](https://github.com/MironovNikita/EasyBuy/blob/main/build.gradle).

Результат сборки проекта:
```java
--------------------------------------------------
Тестов всего: 51
Успешно:      51
Провалено:    0
Пропущено:    0
Результат:    SUCCESS
--------------------------------------------------

> Task :check
> Task :build

BUILD SUCCESSFUL in 14s
9 actionable tasks: 9 executed
2:05:21: Execution finished 'clean build'.
```
