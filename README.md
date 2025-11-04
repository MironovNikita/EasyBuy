

<p align="center">

  <img width="128" height="128" src="https://github.com/MironovNikita/EasyBuy/blob/main/images/logo.png">

</p>

# 🛍️ Интернет-магазин Easy Buy
Это веб-приложение — интернет-магазин, реализованный на Java 21 с использованием Spring Framework версии 6.1 и выше (со Spring Boot), которое может работать в любом современном сервлет-контейнере (Netty). Проект управляется с помощью системы сборки Gradle. Проект основан на реактивном стеке Spring WebFlux с применением Security и R2DBC, что позволило реализовать полностью неблокирующее взаимодействие с базой данных и построить высокопроизводительное масштабируемое веб-приложение, предусматривающее аутентификацию и ограничение доступа к API (логин/пароль). Также в проекте предусмотрено развёртывание в контейнерах Docker с применением Liquibase-скриптов для БД. Для улучшенной работы приложения предусмотрено взаимодействие с Redis посредством формирования API с помощью OpenAPI. Об этом далее.

## 📝 Описание
Приложение состоит из двух модулей:
- 🛒 shop (магазин - основное приложение);
- ⚙️ payment-service (сервис платежей);
а также двух вспомогательных модулей:
- redis как модуль для кеширования и хранения информации о балансах пользователей;
- keycloak - модуль аутентификации между основным приложением и сервисом платежей (OAuth2 протокол разделяет роли клиента, владельца ресурса, сервера авторизации и сервера ресурсов).

**Shop** состоит из шести веб-страниц:
- главная страница с отображением товаров (название, картинка, описание, цена и количество в корзине). Также присутствует возможность добавить товар в корзину или убрать его оттуда. Страница поддерживает поиск по названию или описанию товара и имеет ряд сортировок: по алфавиту, по возрастанию и по убыванию цены;
- страница товара с отображением названия, картинки, описания, цены и количества товара в корзине. Также присутствует возможность добавить его в корзину или убрать оттуда;
- страница корзины. Либо отображает, что сама корзина пуста, либо отображает всё те же атрибуты товара с возможности оформить заказ посредством кнопки "Купить";
- страница заказа отображает все товары выбранного заказа с отображением их атрибутов, а также показывает дату и время оформления заказа;
- страница списка заказов отображает краткое описание всех имеющихся заказов: названия товаров, их количество, цена, итоговая сумма, дата и время оформления самого заказа;
- страница, которая выводит на пользовательский экран сообщение об ошибке с возможностью возврата на предыдущую  или на главную страницу;
- кеширование - осуществляет кеширование товаров главной страницы с параметрами поиска и фильтрации, а также кеширует страницы отдельных товаров.

Стоит отметить, что благодаря интеграции Security стало возможным реализовать отдельную корзину, пул заказов и взаимодействие с сервисом платежей для каждого пользователя.

Для хранения данных используется БД PostgreSQL 17 версии. Для интеграционного тестирования применяется технология TestContainers, которая позволяет для тестирования поднять БД аналогичную основной.
Приложение покрыто unit и интеграционными тестами с использованием JUnit 5 и Spring TestContext Framework, с применением кэширования контекстов.

**Payment-service** отвечает за платежи:
- осуществляет проверку индивидуального для каждого пользователя баланса, хранящегося в Redis, и списание средств при оформлении заказа;
- в рамках регистрации каждого пользователя предусмотрено пополнение его баланса на 20000 рублей по ключу **balance:userId**

### 🧩 Основные сущности модуля Shop 📇
- [**Item**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/entity/item/Item.java) - отвечает за содержание основной информации о товаре.
- [**Order**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/entity/order/Order.java) - отвечает за содержание основной информации о заказе.
- [**CartItem**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/entity/cart/CartItem.java) - отвечает за содержание основной информации о количестве товара в корзине.
- [**User**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/entity/user/User.java) - отвечает за содержание основной информации о пользователе.

Структура таблиц базы данных представлена на схеме:

<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/schema.png">

</p>

### ⚠️ Важно ⚠️
Все таблицы в БД приложения создаются и заполняются посредством Liquibase-скриптов. Ознакомиться с ними можно [**здесь**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/resources/db/changelog/liquibase). Заполнение осуществляется только для таблицы товаров items. Соответствующие ресурсы (картинки) для них расположены [**тут**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/resources/db/item.images).

### 🌐 Генерация контроллеров и клиента посредством OpenAPI
Как упоминалось выше, связь модулей осуществляется через RESTful-взаимодействие. При этом основной модуль [**shop**](https://github.com/MironovNikita/EasyBuy/tree/main/shop) взаимодействует с модулем [**payment-service**](https://github.com/MironovNikita/EasyBuy/tree/main/payment-service) посредством клиента со своей стороны и контроллера ([**PaymentController**](https://github.com/MironovNikita/EasyBuy/blob/main/payment-service/src/main/java/com/shop/easybuy/controller/payment/PaymentController.java) со стороны redis-модуля для осуществления платежей.

Все классы, включая DTO, генерируются по документации, описанной в [**payment-spec.yaml**](https://github.com/MironovNikita/EasyBuy/blob/main/payment-service/src/main/resources/openapi/payment-spec.yaml).

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
[+] Running 6/6
 ✔ Network easybuy_default                            Created                          0.0s
 ✔ Container keycloak_container                       Created                          0.1s
 ✔ Container eb_database_container                    Created                          0.2s
 ✔ Container redis_container                          Created                          0.2s
 ✔ Container eb_shop_container                        Created                          0.1s
 ✔ Container eb_payment_service_container             Created                          0.1s
Attaching to eb_database_container, eb_payment_service_container, eb_shop_container, keycloak_container, redis_container
```

Соответственно Docker Desktop нам отобразит, что наши контейнеры запущены и готовы к бою!
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/docker.png">

</p>

Логи модулей:
```java
//Для Payment-Service:
eb_payment_service_container  | 2025-10-30 - 17:57:46.747 (Z)  INFO 1 ---> [main] o.s.b.w.e.netty.NettyWebServer : Netty started on port 8081 (http)
eb_payment_service_container  | 2025-10-30 - 17:57:46.780 (Z)  INFO 1 ---> [main] com.shop.easybuy.PaymentServiceApplication : Started PaymentServiceApplication in 4.741 seconds (process running for 5.414)
eb_payment_service_container  | 2025-10-30 - 17:57:47.490 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.c.i.RedisDataInitializer : Redis доступен.
eb_payment_service_container  | 2025-10-30 - 17:57:47.492 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.c.i.RedisDataInitializer : Redis успешно инициализирован!

//Для Keycloak
keycloak_container            | 2025-10-30 19:05:34,084 INFO  [org.keycloak.exportimport.singlefile.SingleFileImportProvider] (main) Full importing from file /opt/keycloak/bin/../data/import/realm.json
keycloak_container            | 2025-10-30 19:05:35,202 INFO  [org.keycloak.exportimport.util.ImportUtils] (main) Realm 'easybuy' imported
keycloak_container            | 2025-10-30 19:05:35,202 INFO  [org.keycloak.services] (main) KC-SERVICES0030: Full model import requested. Strategy: IGNORE_EXISTING
keycloak_container            | 2025-10-30 19:05:35,203 INFO  [org.keycloak.services] (main) KC-SERVICES0032: Import finished successfully
keycloak_container            | 2025-10-30 19:05:35,311 INFO  [org.keycloak.services] (main) KC-SERVICES0077: Created temporary admin user with username admin
keycloak_container            | 2025-10-30 19:05:35,386 WARN  [io.agroal.pool] (main) Datasource '<default>': JDBC resources leaked: 1 ResultSet(s) and 0 Statement(s)
keycloak_container            | 2025-10-30 19:05:35,506 INFO  [io.quarkus] (main) Keycloak 26.1.3 on JVM (powered by Quarkus 3.15.3.1) started in 11.588s. Listening on: http://0.0.0.0:8080
keycloak_container            | 2025-10-30 19:05:35,507 INFO  [io.quarkus] (main) Profile dev activated.
keycloak_container            | 2025-10-30 19:05:35,507 INFO  [io.quarkus] (main) Installed features: [agroal, cdi, hibernate-orm, jdbc-h2, keycloak, narayana-jta, opentelemetry, reactive-routes, rest, rest-jackson,
smallrye-context-propagation, vertx]

//Для Shop наши Liquibase-скрипты отрабатывают успешно:
eb_shop_container             | 2025-10-30 - 19:05:40.561 (Z)  INFO 1 ---> [main] liquibase.ui : Running Changeset: db/changelog/liquibase/fill-in-items.xml::fill_data::MironovNikita
eb_shop_container             | 2025-10-30 - 19:05:40.566 (Z)  INFO 1 ---> [main] liquibase.changelog : New row inserted into items
eb_shop_container             | 2025-10-30 - 19:05:40.567 (Z)  INFO 1 ---> [main] liquibase.changelog : New row inserted into items
eb_shop_container             | 2025-10-30 - 19:05:40.569 (Z)  INFO 1 ---> [main] liquibase.changelog : New row inserted into items
eb_shop_container             | 2025-10-30 - 19:05:40.571 (Z)  INFO 1 ---> [main] liquibase.changelog : New row inserted into items
eb_shop_container             | 2025-10-30 - 19:05:40.572 (Z)  INFO 1 ---> [main] liquibase.changelog : New row inserted into items
eb_shop_container             | 2025-10-30 - 19:05:40.573 (Z)  INFO 1 ---> [main] liquibase.changelog : New row inserted into items
eb_shop_container             | 2025-10-30 - 19:05:40.575 (Z)  INFO 1 ---> [main] liquibase.changelog : ChangeSet db/changelog/liquibase/fill-in-items.xml::fill_data::MironovNikita ran successfully in 14ms
eb_shop_container             | 2025-10-30 - 19:05:40.583 (Z)  INFO 1 ---> [main] liquibase.util : UPDATE SUMMARY
eb_shop_container             | 2025-10-30 - 19:05:40.583 (Z)  INFO 1 ---> [main] liquibase.util : Run:                          6
eb_shop_container             | 2025-10-30 - 19:05:40.583 (Z)  INFO 1 ---> [main] liquibase.util : Previously run:               0
eb_shop_container             | 2025-10-30 - 19:05:40.583 (Z)  INFO 1 ---> [main] liquibase.util : Filtered out:                 0
eb_shop_container             | 2025-10-30 - 19:05:40.584 (Z)  INFO 1 ---> [main] liquibase.util : -------------------------------
eb_shop_container             | 2025-10-30 - 19:05:40.584 (Z)  INFO 1 ---> [main] liquibase.util : Total change sets:            6
eb_shop_container             | 2025-10-30 - 19:05:40.584 (Z)  INFO 1 ---> [main] liquibase.util : Update summary generated
eb_shop_container             | 2025-10-30 - 19:05:40.586 (Z)  INFO 1 ---> [main] liquibase.command : Update command completed successfully.
eb_shop_container             | 2025-10-30 - 19:05:40.586 (Z)  INFO 1 ---> [main] liquibase.ui : Liquibase: Update has been successful. Rows affected: 12
eb_shop_container             | 2025-10-30 - 19:05:40.590 (Z)  INFO 1 ---> [main] liquibase.lockservice : Successfully released change log lock
eb_shop_container             | 2025-10-30 - 19:05:40.591 (Z)  INFO 1 ---> [main] liquibase.command : Command execution complete
eb_shop_container             | 2025-10-30 - 19:05:41.937 (Z)  INFO 1 ---> [main] o.s.b.w.e.netty.NettyWebServer : Netty started on port 8080 (http)
eb_shop_container             | 2025-10-30 - 19:05:41.945 (Z)  INFO 1 ---> [main] com.shop.easybuy.EasyBuyApplication : Started EasyBuyApplication in 6.134 seconds (process running for 6.588)
```

Для корректной работы с keycloak-контейнером была предусмотрена конфигурация [**realm.json**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/resources/realm.json), которая устанавливает клиента для корректного взаимодействия по OAuth2.0.

Так как в приложение была добавлена авторизация, то теперь, чтобы полноценно воспользоваться функционалом, необходимо зарегистрироваться. Каждая учётная запись подлежит ограничениям, ознакомиться с которыми можно в [**OrderCreateDto**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/entity/user/UserCreateDto.java).

При входе в приложение нас встречает страница авторизации:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/login.png">

</p>

Если попытаемся зайти просто так, программа нас не пропустит:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/loginFailed.png">

</p>

Перейдём на главную страницу без регистрации. Теперь мы определяемся как анонимный пользователь. И можем просто просматривать товары вместе и по отдельности.
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/main.png">

</p>

Теперь можем перейти к регистрации:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/register.png">

</p>

### ❗️ Пользовательские данные
Стоит отдельно отметить, что часть пользовательских данных шифруется для хранения в БД. Это реализовано с помощью классов **PasswordEncoder** - для пароля (не предусматривает расшифровки обратно) и [**SecureBase64Converter**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/common/security/SecureBase64Converter.java), который отвечает за шифрование email и номера телефона пользователя.

<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/usersDB.png">

</p>

После регистрации можем смело заходить по нашим данным в приложение:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/registerSuccess.png">

</p>

Теперь мы можем воспользоваться полным функционалом. Стало возможным добавление товаров в корзину, открылись кнопки корзины и заказов:
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

Оформим пару заказов.
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

В случае, значение баланса пользователя изменится на меньшую от заказа сумму, он увидит:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/notEnoughMoney.png">

</p>

И в случае, если наш payment-service будет по каким-либо причинам недоступен:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/paymentServiceUnavailable.png">

</p>


## 🗒️ Логирование 🔍
В приложении также предусмотрено логирование. Логи пишутся непосредственно в консоль. Ниже приведён пример логов:
```java
eb_shop_container             | 2025-10-30 - 19:28:36.458 (Z)  INFO 1 ---> [lettuce-epollEventLoop-7-1] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_shop_container             | 2025-10-30 - 19:28:36.467 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items?search=&sort=NONE&pageNumber
=0&pageSize=10 выполнен за 12 мс.
eb_shop_container             | 2025-10-30 - 19:28:40.850 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.cart.CartServiceImpl : В корзине обновлено количество товара с ID 2 для пользователя с ID 1. Текущее коли
чество: 2.
eb_shop_container             | 2025-10-30 - 19:28:40.855 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/main/items/2 выполнен за 9 мс.
eb_shop_container             | 2025-10-30 - 19:28:40.862 (Z)  INFO 1 ---> [lettuce-epollEventLoop-7-1] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_shop_container             | 2025-10-30 - 19:28:40.871 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items?search=&sort=NONE&pageNumber
=0&pageSize=10 выполнен за 12 мс.
eb_payment_service_container  | 2025-10-30 - 19:28:42.203 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.s.payment.PaymentServiceImpl : Значение баланса успешно извлечено: 9569
eb_shop_container             | 2025-10-30 - 19:28:42.206 (Z)  INFO 1 ---> [reactor-http-epoll-12] c.s.e.service.cart.CartServiceImpl : В корзине пользователя с ID 1 найдено 3 товаров.
eb_shop_container             | 2025-10-30 - 19:28:42.209 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/cart/items выполнен за 17 мс.
eb_payment_service_container  | 2025-10-30 - 19:28:42.204 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.filter.RequestLoggingFilter : GET-запрос http://payment-service:8081/balance?userId=1 выполнен за 3 мс
eb_payment_service_container  | 2025-10-30 - 19:28:45.281 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.s.payment.PaymentServiceImpl : Значение баланса успешно извлечено: 9569
eb_shop_container             | 2025-10-30 - 19:28:45.283 (Z)  INFO 1 ---> [reactor-http-epoll-12] c.s.e.service.cart.CartServiceImpl : В корзине пользователя с ID 1 найдено 3 товаров.
eb_payment_service_container  | 2025-10-30 - 19:28:45.282 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.filter.RequestLoggingFilter : GET-запрос http://payment-service:8081/balance?userId=1 выполнен за 3 мс
eb_payment_service_container  | 2025-10-30 - 19:28:45.291 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.s.payment.PaymentServiceImpl : Платёж успешно совершён. Остаток средств на балансе: 6250
eb_shop_container             | 2025-10-30 - 19:28:45.294 (Z)  INFO 1 ---> [reactor-http-epoll-12] c.s.e.service.order.OrderServiceImpl : Списание средств успешно произведено. Текущий баланс пользователя с ID 1: 6250
. Формируем заказ...
eb_payment_service_container  | 2025-10-30 - 19:28:45.293 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.filter.RequestLoggingFilter : POST-запрос http://payment-service:8081/pay выполнен за 5 мс
eb_shop_container             | 2025-10-30 - 19:28:45.301 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.cart.CartServiceImpl : Корзина пользователя с ID 1 была очищена.
eb_shop_container             | 2025-10-30 - 19:28:45.301 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.order.OrderServiceImpl : Сформирован заказ с ID 2 и количеством товаров 3.
eb_shop_container             | 2025-10-30 - 19:28:45.305 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/buy выполнен за 36 мс.
eb_shop_container             | 2025-10-30 - 19:28:45.314 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.order.OrderServiceImpl : Для пользователя с ID 1 найден заказ с ID 2 и количеством товаров 3.
eb_shop_container             | 2025-10-30 - 19:28:45.328 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/orders/2?newOrder=true выполнен за 18 м
с.
eb_shop_container             | 2025-10-30 - 19:28:47.332 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.order.OrderServiceImpl : Найдено 2 заказов для пользователя с ID 1.
eb_shop_container             | 2025-10-30 - 19:28:47.340 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/orders выполнен за 16 мс.
eb_shop_container             | 2025-10-30 - 19:29:46.727 (Z)  INFO 1 ---> [lettuce-epollEventLoop-7-1] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_shop_container             | 2025-10-30 - 19:29:46.735 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items выполнен за 11 мс.
eb_shop_container             | 2025-10-30 - 19:29:48.028 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.cart.CartServiceImpl : В корзине обновлено количество товара с ID 1 для пользователя с ID 1. Текущее коли
чество: 1.
eb_shop_container             | 2025-10-30 - 19:29:48.033 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/main/items/1 выполнен за 8 мс.
eb_shop_container             | 2025-10-30 - 19:29:48.041 (Z)  INFO 1 ---> [lettuce-epollEventLoop-7-1] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_shop_container             | 2025-10-30 - 19:29:48.049 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items?search=&sort=NONE&pageNumber
=0&pageSize=10 выполнен за 11 мс.
eb_shop_container             | 2025-10-30 - 19:29:48.959 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.cart.CartServiceImpl : В корзине обновлено количество товара с ID 1 для пользователя с ID 1. Текущее коли
чество: 2.
eb_shop_container             | 2025-10-30 - 19:29:48.965 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/main/items/1 выполнен за 10 мс.
eb_shop_container             | 2025-10-30 - 19:29:48.972 (Z)  INFO 1 ---> [lettuce-epollEventLoop-7-1] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_shop_container             | 2025-10-30 - 19:29:48.981 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items?search=&sort=NONE&pageNumber
=0&pageSize=10 выполнен за 12 мс.
eb_shop_container             | 2025-10-30 - 19:29:49.420 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.cart.CartServiceImpl : В корзине обновлено количество товара с ID 1 для пользователя с ID 1. Текущее коли
чество: 3.
eb_shop_container             | 2025-10-30 - 19:29:49.425 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/main/items/1 выполнен за 9 мс.
eb_shop_container             | 2025-10-30 - 19:29:49.433 (Z)  INFO 1 ---> [lettuce-epollEventLoop-7-1] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_shop_container             | 2025-10-30 - 19:29:49.441 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items?search=&sort=NONE&pageNumber
=0&pageSize=10 выполнен за 11 мс.
eb_shop_container             | 2025-10-30 - 19:29:49.711 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.cart.CartServiceImpl : В корзине обновлено количество товара с ID 1 для пользователя с ID 1. Текущее коли
чество: 4.
eb_shop_container             | 2025-10-30 - 19:29:49.717 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/main/items/1 выполнен за 12 мс.
eb_shop_container             | 2025-10-30 - 19:29:49.725 (Z)  INFO 1 ---> [lettuce-epollEventLoop-7-1] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_shop_container             | 2025-10-30 - 19:29:49.735 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items?search=&sort=NONE&pageNumber
=0&pageSize=10 выполнен за 14 мс.
eb_shop_container             | 2025-10-30 - 19:29:49.891 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.service.cart.CartServiceImpl : В корзине обновлено количество товара с ID 1 для пользователя с ID 1. Текущее коли
чество: 5.
eb_shop_container             | 2025-10-30 - 19:29:49.897 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : POST-запрос http://localhost:8080/easy-buy/main/items/1 выполнен за 10 мс.
eb_shop_container             | 2025-10-30 - 19:29:49.905 (Z)  INFO 1 ---> [lettuce-epollEventLoop-7-1] c.s.e.service.item.ItemServiceImpl : Главная страница с товарами (6 шт.) получена из кеша.
eb_shop_container             | 2025-10-30 - 19:29:49.913 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/main/items?search=&sort=NONE&pageNumber
=0&pageSize=10 выполнен за 11 мс.
eb_payment_service_container  | 2025-10-30 - 19:29:51.407 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.s.payment.PaymentServiceImpl : Значение баланса успешно извлечено: 6250
eb_shop_container             | 2025-10-30 - 19:29:51.409 (Z)  INFO 1 ---> [reactor-http-epoll-12] c.s.e.service.cart.CartServiceImpl : В корзине пользователя с ID 1 найдено 1 товаров.
eb_payment_service_container  | 2025-10-30 - 19:29:51.408 (Z)  INFO 1 ---> [lettuce-epollEventLoop-5-1] c.s.e.filter.RequestLoggingFilter : GET-запрос http://payment-service:8081/balance?userId=1 выполнен за 3 мс
eb_shop_container             | 2025-10-30 - 19:29:51.413 (Z)  INFO 1 ---> [reactor-tcp-epoll-1] c.s.e.c.filter.RequestLoggingFilter : GET-запрос http://localhost:8080/easy-buy/cart/items выполнен за 17 мс.
```

Как можно заметить, в логи также пишется и время выполнения запроса. Данную возможность предоставляет [**RequestLoggingFilter**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/common/filter/RequestLoggingFilter.java) - компонент Spring, реализующий **WebFilter**, для логирования времени выполнения HTTP-запросов в shop-модуле и [**RequestLoggingFilter**](https://github.com/MironovNikita/EasyBuy/blob/main/payment-service/src/main/java/com/shop/easybuy/filter/RequestLoggingFilter.java) в payment-service.

1. _Назначение_
Логирует все HTTP-запросы к приложению и время их обработки, кроме ресурсов, которые не нужно трекать (картинки, favicon).

2. _Особенности реализации_
WebFilter: это реактивный аналог Servlet Filter для WebFlux.
ServerWebExchange: содержит данные запроса и ответа.
WebFilterChain: пропускает обработку дальше по цепочке.

### 🔐 Безопасности мало не бывает
Как говорилось выше, в приложении предусмотрена фильтрация запросов, куда пользователь может получать доступ без аутентификации, а куда нет. За эти настройки отвечает конфигурация [**SecurityConfig**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/common/config/SecurityConfig.java).
Но также предусмотрена валидация на уровне методов сервисов. Чтобы уже аутентифицированный пользователь не мог попасть в чужие заказы или корзину. За это отвечает класс [**SecurityUserContextHandler**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/java/com/shop/easybuy/common/security/SecurityUserContextHandler.java).

## ✅ Тестирование 🐞
Как говорилось ранее, для интеграционного тестирования предусмотрен TestContainer для БД. С его настройками можно ознакомиться [**здесь**](https://github.com/MironovNikita/EasyBuy/tree/main/shop/src/test/java/com/shop/easybuy/testDB).
Также для тестирования предусмотрен TestContainer для Redis. Его настройки находятся [**тут**](https://github.com/MironovNikita/EasyBuy/tree/main/payment-service/src/test/java/com/shop/easybuy/container).

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

### Для модуля payment-service:
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

Общие зависимости проекта можно найти в [**build.gradle**](https://github.com/MironovNikita/EasyBuy/blob/main/build.gradle).

Зависимости и задачи для генерации OpenAPI модуля **shop** можно найти в его [**build.gradle**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/build.gradle).
Зависимости и задачи для генерации OpenAPI модуля **payment-service** можно найти в его [**build.gradle**](https://github.com/MironovNikita/EasyBuy/blob/main/payment-service/build.gradle).

Также для успешной сборки Docker многомодульного проекта был предусмотрен дополнительный файл в модуле shop:
- [**application-docker.properties**](https://github.com/MironovNikita/EasyBuy/blob/main/shop/src/main/resources/application-docker.properties).
Для модуля payment-service:
- [**application-local.properties**](https://github.com/MironovNikita/EasyBuy/blob/main/payment-service/src/main/resources/application-local.properties)
- [**application-docker.properties**](https://github.com/MironovNikita/EasyBuy/blob/main/payment-service/src/main/resources/application-docker.properties)

Это необходимо для корректного подключения к Redis и взаимодействию модулей между собой. Подробнее можно посмотреть в [**docker-compose.yml**](https://github.com/MironovNikita/EasyBuy/blob/main/docker-compose.yml).

Результат сборки проекта:
```java
//Для модуля payment-service
--------------------------------------------------
Тестов всего: 13
Успешно:      13
Провалено:    0
Пропущено:    0
Результат:    SUCCESS
--------------------------------------------------

> Task :payment-service:check
> Task :payment-service:build

//Для модуля shop:
--------------------------------------------------
Тестов всего: 92
Успешно:      92
Провалено:    0
Пропущено:    0
Результат:    SUCCESS
--------------------------------------------------

> Task :shop:check
> Task :shop:build

BUILD SUCCESSFUL in 32s
21 actionable tasks: 21 executed
22:00:58: Execution finished 'clean build'.
```
