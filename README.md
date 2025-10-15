

<p align="center">

  <img width="128" height="128" src="https://github.com/MironovNikita/EasyBuy/blob/main/images/logo.png">

</p>

# 🛍️ Интернет-магазин Easy Buy
Это веб-приложение — интернет-магазин, реализованный на Java 21 с использованием Spring Framework версии 6.1 и выше (со Spring Boot), которое может работать в любом современном сервлет-контейнере (Netty). Проект управляется с помощью системы сборки Gradle. Проект основан на реактивном стеке Spring WebFlux и R2DBC, что позволило реализовать полностью неблокирующее взаимодействие с базой данных и построить высокопроизводительное масштабируемое веб-приложение. Также в проекте предусмотрено развёртывание в контейнерах Docker с применением Liquibase-скриптов для БД. Для улучшенной работы приложения предусмотрено взаимодействие с Redis посредством формирования API с помощтю OpenAPI. Об этом далее.

## 📝 Описание
Приложение состоит из двух модулей:
- 🛒 shop (магазин - основное приложение)
- ⚙️ redis-service (сервис платежей и кеширования)

**Shop** состоит из шести веб-страниц:
- главная страница с отображением товаров (название, картинка, описание, цена и количество в корзине). Также присутствует возможность добавить товар в корзину или убрать его оттуда. Страница поддерживает поиск по названию или описанию товара и имеет ряд сортировок: по алфавиту, по возрастанию и по убыванию цены;
- страница товара с отображением названия, картинки, описания, цены и количества товара в корзине. Также присутствует возможность добавить его в корзину или убрать оттуда;
- страница корзины. Либо отображает, что сама корзина пуста, либо отображает всё те же атрибуты товара с возможности оформить заказ посредством кнопки "Купить";
- страница заказа отображает все товары выбранного заказа с отображением их атрибутов, а также показывает дату и время оформления заказа;
- страница списка заказов отображает краткое описание всех имеющихся заказов: названия товаров, их количество, цена, итоговая сумма, дата и время оформления самого заказа;
- также предусмотрена страница, которая выводит на пользовательский экран сообщение об ошибке с возможностью возврата на предыдущую  или на главную страницу.

Для хранения данных используется БД PostgreSQL 17 версии. Для интеграционного тестирования применяется технология TestContainers, которая позволяет для тестирования поднять БД аналогичную основной.
Приложение покрыто unit и интеграционными тестами с использованием JUnit 5 и Spring TestContext Framework, с применением кэширования контекстов.

**Redis-service** состоит из двух подчастей:
- сервиса платежей - осуществляет проверку баланса, хранящегося в Redis, и списание средств при оформлении заказа;
- сервиса кеширования - осуществляет кеширование товаров главной страницы с параметрами поиска и фильтрации, а также кеширует страницы отдельных товаров.

### 🧩 Основные сущности модуля Shop 📇
- [**Item**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/entity/item/Item.java) - отвечает за содержание основной информации о товаре.
- [**Order**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/entity/order/Order.java) - отвечает за содержание основной информации о заказе.
- [**CartItem**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/entity/cart/CartItem.java) - отвечает за содержание основной информации о количестве товара в корзине.

Структура таблиц базы данных представлена на схеме:

<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/schema.png">

</p>

### ⚠️ Важно ⚠️
Все таблицы в БД приложения создаются и заполняются посредством Liquibase-скриптов. Ознакомиться с ними можно [**здесь**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/resources/db/changelog/liquibase). Заполнение осуществляется только для таблицы товаров items. Соответствующие ресурсы (картинки) для них расположены [**тут**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/resources/db/item.images).

### 🌐 Генерация контроллеров и клиента посредством OpenAPI
Как упоминалось выше, связь модулей осуществляется через RESTful-взаимодействие. При этом основной модуль [**shop**](https://github.com/MironovNikita/EasyBuy/tree/main/shop) взаимодействует с модулем [**redis-service**](https://github.com/MironovNikita/EasyBuy/tree/main/redis-service) посредством клиента со своей стороны и контроллеров ([**PaymentController**](https://github.com/MironovNikita/EasyBuy/blob/main/redis-service/src/main/java/com/shop/easybuy/controller/payment/PaymentController.java) и [**CacheController**](https://github.com/MironovNikita/EasyBuy/blob/main/redis-service/src/main/java/com/shop/easybuy/controller/cache/CacheController.java)) со стороны redis-модуля.

Все классы, включая DTO, генерируются по документации, описанной в [**cache-spec.yaml**](https://github.com/MironovNikita/EasyBuy/blob/main/redis-service/src/main/resources/openapi/cache-spec.yaml) и [**payment-spec.yaml**](https://github.com/MironovNikita/EasyBuy/blob/main/redis-service/src/main/resources/openapi/payment-spec.yaml).

### 🚀 Запуск программы

Запустить программу можно двумя способами.

_**Классический**_:
1) Установить БД [**PostgreSQL**](https://www.postgresql.org/download/);
2) Установить Gradle;
3) Скачать проект;
4) В консоли Gradle выполнить команду **`gradle clean build`**;
5) Запустить Docker Desktop;
6) Через командную строку запустить Redis: **`docker run --name redis-server -p 6379:6379 redis:7.4.2-bookworm `**
7) Запустить оба модуля через IDEA;
8) Перейти на **http://localhost:8080/easy-buy/**;
9) Оофрмлять заказы на товары :)

####
_**С помощью Docker**_:
1) На компьютере должен быть установлен WSL;
2) Также необходимо установить [**Docker Desktop**](https://www.docker.com/products/docker-desktop/);
3) Установить Gradle;
4) Скачать проект;
5) Через командную строку зайти в корень проекта;
6) Открыть Docker Desktop;
7) Ввести команду **docker compose up**;
8) Наблюдать за магией;
9) Оформлять заказы на товары :)

####
После введения команды **docker compose up** получим:
```java
[+] Running 5/5                                                             
✔ Network easybuy_default               Created                                                                   0.1s
✔ Container redis_container             Created                                                                   0.2s
✔ Container eb_database_container       Created                                                                   0.2s
✔ Container eb_redis_service_container  Created                                                                   0.1s
✔ Container eb_shop_container           Created                                                                   0.1s
Attaching to eb_database_container, eb_redis_service_container, eb_shop_container
```

Соответственно Docker Desktop нам отобразит, что наши контейнеры запущены и готовы к бою!
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/docker.png">

</p>

Как можем видеть из логов, наши Liquibase-скрипты отрабатывают успешно:
```java
//Для Redis:
eb_redis_service_container  | 2025-10-15 - 00:53:33.464 (Z)  INFO 1 ---> [main] o.s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode
eb_redis_service_container  | 2025-10-15 - 00:53:33.466 (Z)  INFO 1 ---> [main] o.s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
eb_redis_service_container  | 2025-10-15 - 00:53:33.498 (Z)  INFO 1 ---> [main] o.s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 15 ms. Found 0 Redis repository interfaces.
eb_shop_container           | 2025-10-15 - 00:53:33.608 (Z)  INFO 1 ---> [main] o.s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data R2DBC repositories in DEFAULT mode.
eb_shop_container           | 2025-10-15 - 00:53:33.746 (Z)  INFO 1 ---> [main] o.s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 131 ms. Found 4 R2DBC repository interfaces.
eb_redis_service_container  | 2025-10-15 - 00:53:35.210 (Z)  INFO 1 ---> [main] o.s.b.w.e.netty.NettyWebServer : Netty started on port 8081 (http)
eb_redis_service_container  | 2025-10-15 - 00:53:35.241 (Z)  INFO 1 ---> [main] com.shop.easybuy.RedisApplication : Started RedisApplication in 3.28 seconds (process running for 3.94)
eb_redis_service_container  | 2025-10-15 - 00:53:35.586 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.c.i.RedisDataInitializer : Redis доступен.
eb_redis_service_container  | 2025-10-15 - 00:53:35.600 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.c.i.RedisDataInitializer : Значение баланса в Redis успешно проинициализировано: 15000 руб.

//Для Shop:
eb_shop_container           | 2025-10-15 - 00:53:36.775 (Z)  INFO 1 ---> [main] liquibase.util : UPDATE summary eb_shop_container           | 2025-10-15 - 00:53:36.775 (Z)  INFO 1 ---> [main] liquibase.util : Run:                          5
eb_shop_container           | 2025-10-15 - 00:53:36.776 (Z)  INFO 1 ---> [main] liquibase.util : Previously run:               0
eb_shop_container           | 2025-10-15 - 00:53:36.776 (Z)  INFO 1 ---> [main] liquibase.util : Filtered out:                 0
eb_shop_container           | 2025-10-15 - 00:53:36.776 (Z)  INFO 1 ---> [main] liquibase.util : -------------------------------
eb_shop_container           | 2025-10-15 - 00:53:36.776 (Z)  INFO 1 ---> [main] liquibase.util : Total change sets:            5
eb_shop_container           | 2025-10-15 - 00:53:36.777 (Z)  INFO 1 ---> [main] liquibase.util : Update summary generated
eb_shop_container           | 2025-10-15 - 00:53:36.779 (Z)  INFO 1 ---> [main] liquibase.command : Update command completed successfully.
eb_shop_container           | 2025-10-15 - 00:53:36.779 (Z)  INFO 1 ---> [main] liquibase.ui : Liquibase: Update has been successful. Rows affected: 11
eb_shop_container           | 2025-10-15 - 00:53:36.785 (Z)  INFO 1 ---> [main] liquibase.lockservice : Successfully released change log lock
eb_shop_container           | 2025-10-15 - 00:53:36.786 (Z)  INFO 1 ---> [main] liquibase.command : Command execution complete
eb_shop_container           | 2025-10-15 - 00:53:38.427 (Z)  INFO 1 ---> [main] o.s.b.w.e.netty.NettyWebServer : Netty started on port 8080 (http)
eb_shop_container           | 2025-10-15 - 00:53:38.435 (Z)  INFO 1 ---> [main] com.shop.easybuy.EasyBuyApplication : Started EasyBuyApplication in 6.352 seconds (process running for 6.942)             
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

В случае, если на счёте будет недостаточно средств, в корзине мы увидим:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/notEnoughFunds.png">

</p>

В случае, если пользователь всё-таки сможет зайти в корзину, а значение его баланса изменится на меньшую от заказа сумму, он увидит:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/notEnoughMoney.png">

</p>

И в случае, если наш redis-service будет по каким-либо причинам недоступен:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/paymentServiceUnavailable.png">

</p>


## 🗒️ Логирование 🔍
В приложении также предусмотрено логирование. Логи пишутся непосредственно в консоль. Ниже приведён пример логов:
```java
eb_redis_service_container  | 2025-10-15 - 01:01:50.661 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.service.cache.CacheServiceImpl : Товары не найдены в кеше по параметрам: search - , sort - NONE, pageSize - 10, pageNumber - 0.
eb_redis_service_container  | 2025-10-15 - 01:01:50.676 (Z)  INFO 1 ---> [reactor-http-epoll-2] c.s.e.filter.RequestLoggingFilter : GET-запрос http://redis-service:8081/cache/main/items?search=&sort=NONE&pageSize=10&pageNumber=0 выполнен за 217 мс
eb_shop_container           | 2025-10-15 - 01:01:50.736 (Z)  INFO 1 ---> [reactor-http-epoll-5] c.s.e.service.item.ItemServiceImpl : Данные по параметрам (search: "", sort: NONE, pageSize: 10, pageNumber: 0) не найдены в кеше.
eb_redis_service_container  | 2025-10-15 - 01:01:51.003 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.service.cache.CacheServiceImpl : Товары успешно кешированы с параметрами: search - , sort - NONE, pageSize - 10, pageNumber - 0.
eb_redis_service_container  | 2025-10-15 - 01:01:51.009 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.filter.RequestLoggingFilter : POST-запрос http://redis-service:8081/cache/main/items?search=&sort=NONE&pageSize=10&pageNumber=0 выполнен за 163 мс
eb_shop_container           | 2025-10-15 - 01:01:51.021 (Z)  INFO 1 ---> [reactor-http-epoll-5] c.s.e.service.item.ItemServiceImpl : Товары на главной странице (6 шт.) сохранены в кеш.
eb_shop_container           | 2025-10-15 - 01:01:51.345 (Z)  INFO 1 ---> [parallel-2] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items выполнен за 1406 мс.
eb_shop_container           | 2025-10-15 - 01:01:56.714 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.cart.CartServiceImpl : В корзине обновлено количество товара с ID 1. Текущее количество: 1.
eb_shop_container           | 2025-10-15 - 01:01:56.734 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/main/items/1 выполнен за 83 мс.
eb_redis_service_container  | 2025-10-15 - 01:01:56.763 (Z)  INFO 1 ---> [reactor-http-epoll-2] c.s.e.service.cache.CacheServiceImpl : Товары успешно извлечены из кеша по параметрам: search - , sort - NONE, pageSize - 10, pageNumber - 0.
eb_shop_container           | 2025-10-15 - 01:01:56.766 (Z)  INFO 1 ---> [reactor-http-epoll-5] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_redis_service_container  | 2025-10-15 - 01:01:56.764 (Z)  INFO 1 ---> [reactor-http-epoll-2] c.s.e.filter.RequestLoggingFilter : GET-запрос http://redis-service:8081/cache/main/items?search=&sort=NONE&pageSize=10&pageNumber=0 выполнен за 21 мс
eb_shop_container           | 2025-10-15 - 01:01:56.785 (Z)  INFO 1 ---> [parallel-3] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items?search=&sort=NONE&pageNumber=0&pageSize=10 выполнен за 46 мс.
eb_redis_service_container  | 2025-10-15 - 01:01:58.138 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.service.cache.CacheServiceImpl : Товар с ID 1 не найден в кеше
eb_redis_service_container  | 2025-10-15 - 01:01:58.138 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.filter.RequestLoggingFilter : GET-запрос http://redis-service:8081/cache/item/1 выполнен за 3 мс
eb_redis_service_container  | 2025-10-15 - 01:01:58.208 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.service.cache.CacheServiceImpl : Товар с ID 1 успешно кеширован. Статус: true
eb_shop_container           | 2025-10-15 - 01:01:58.211 (Z)  INFO 1 ---> [reactor-http-epoll-5] c.s.e.service.item.ItemServiceImpl : Товар с ID 1 сохранён в кеш.
eb_redis_service_container  | 2025-10-15 - 01:01:58.210 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.filter.RequestLoggingFilter : POST-запрос http://redis-service:8081/cache/item/1 выполнен за 44 мс
eb_shop_container           | 2025-10-15 - 01:01:58.224 (Z)  INFO 1 ---> [parallel-4] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/items/1 выполнен за 95 мс.
eb_redis_service_container  | 2025-10-15 - 01:02:00.028 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.s.payment.PaymentServiceImpl : Значение баланса успешно извлечено: 15000
eb_shop_container           | 2025-10-15 - 01:02:00.033 (Z)  INFO 1 ---> [reactor-http-epoll-5] c.s.e.service.cart.CartServiceImpl : В корзине найдено 1 товаров.
eb_redis_service_container  | 2025-10-15 - 01:02:00.030 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.filter.RequestLoggingFilter : GET-запрос http://redis-service:8081/balance выполнен за 7 мс
eb_shop_container           | 2025-10-15 - 01:02:00.045 (Z)  INFO 1 ---> [parallel-5] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/cart/items выполнен за 33 мс.
eb_redis_service_container  | 2025-10-15 - 01:02:01.535 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.s.payment.PaymentServiceImpl : Значение баланса успешно извлечено: 15000
eb_shop_container           | 2025-10-15 - 01:02:01.538 (Z)  INFO 1 ---> [reactor-http-epoll-5] c.s.e.service.cart.CartServiceImpl : В корзине найдено 1 товаров.
eb_shop_container           | 2025-10-15 - 01:02:01.570 (Z)  INFO 1 ---> [reactor-http-epoll-5] c.s.e.service.order.OrderServiceImpl : Списание средств успешно произведено. Текущий баланс: 10000. Формируем заказ...
eb_redis_service_container  | 2025-10-15 - 01:02:01.564 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.s.payment.PaymentServiceImpl : Платёж успешно совершён. Остаток средств на балансе: 10000                  
```

Как можно заметить, в логи также пишется и время выполнения запроса. Данную возможность предоставляет [**RequestLoggingFilter**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/common/filter/RequestLoggingFilter.java) - компонент Spring, реализующий **WebFilter**, для логирования времени выполнения HTTP-запросов в shop-модуле и [**RequestLoggingFilter**](https://github.com/MironovNikita/EasyBuy/blob/main/redis-service/src/main/java/com/shop/easybuy/filter/RequestLoggingFilter.java) в redis-service.

1. _Назначение_
Логирует все HTTP-запросы к приложению и время их обработки, кроме ресурсов, которые не нужно трекать (картинки, favicon).

2. _Особенности реализации_
WebFilter: это реактивный аналог Servlet Filter для WebFlux.
ServerWebExchange: содержит данные запроса и ответа.
WebFilterChain: пропускает обработку дальше по цепочке.

## ✅ Тестирование 🐞
Как говорилось ранее, для интеграционного тестирования предусмотрен TestContainer для БД. С его настройками можно ознакомиться [**здесь**](https://github.com/MironovNikita/EasyBuy/tree/main/src/test/java/com/shop/easybuy/testDB).
Также для тестирования предусмотрен TestContainer для Redis. Его настройки находятся [**тут**](https://github.com/MironovNikita/EasyBuy/tree/main/redis-service/src/test/java/com/shop/easybuy/container).

### Для модуля shop:
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

### Для модуля redis-service:
### 1️⃣ CommonRedisContainer
Обёртка над Testcontainers для Redis.
Стартует контейнер с Redis.
Используется для всех интеграционных тестов, чтобы поднимать изолированный сервер для проверки баланса и кеширования товаров.

### 2️⃣ AbstractTestRedisInitialization
Абстрактный базовый класс для тестов, связанных с Redis.
Через @DynamicPropertySource подставляет свойства Redis из CommonRedisContainer (хост и порт).

Во время выполнения тестов можем наблюдать запущенный контейнер БД:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/containerDB.png">

</p>

Стоит отметить, что оба контейнера поднимаются единожды для всех тестов.
Тестовая БД заполняется также посредством использования Liquibase-скриптов.
Тестовый Redis также заполняется параметром баланса пользователя для совершения покупок.

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

Общие зависимости проекта можно найти в [**build.gradle**](https://github.com/MironovNikita/EasyBuy/blob/main/build.gradle).

Зависимости и задачи для генерации OpenAPI модуля **shop** можно найти в его [**build.gradle**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/build.gradle).
Зависимости и задачи для генерации OpenAPI модуля **redis-service** можно найти в его [**build.gradle**](https://github.com/MironovNikita/EasyBuy/blob/main/redis-service/build.gradle).

Также для успешной сборки Docker многомодульного проекта был предусмотрен дополнительный файл в модуле shop:
- [**application-docker.properties**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/resources/application-docker.properties).

Для модуля redis-service:
- [**application-local.properties**](https://github.com/MironovNikita/EasyBuy/blob/main/redis-service/src/main/resources/application-local.properties)
- [**application-docker.properties**](https://github.com/MironovNikita/EasyBuy/blob/main/redis-service/src/main/resources/application-docker.properties)

Это необходимо для корректного подключения к Redis и взаимодействию модулей между собой. Подробнее можно посмотреть в [**docker-compose.yml**](https://github.com/MironovNikita/EasyBuy/blob/main/docker-compose.yml).

Результат сборки проекта:
```java
//Для модуля redis-service
--------------------------------------------------
Тестов всего: 34
Успешно:      34
Провалено:    0
Пропущено:    0
Результат:    SUCCESS
--------------------------------------------------

> Task :redis-service:check
> Task :redis-service:build

//Для модуля shop:
--------------------------------------------------
Тестов всего: 51
Успешно:      51
Провалено:    0
Пропущено:    0
Результат:    SUCCESS
--------------------------------------------------

> Task :shop:check
> Task :shop:build

BUILD SUCCESSFUL in 28s
23 actionable tasks: 23 executed
4:17:18: Execution finished 'clean build'.
```
