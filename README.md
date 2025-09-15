

<p align="center">

  <img width="128" height="128" src="https://github.com/MironovNikita/EasyBuy/blob/main/images/logo.png">

</p>

# 🛍️ Интернет-магазин Easy Buy
Это веб-приложение — интернет-магазин, реализованный на Java 21 с использованием Spring Framework версии 6.1 и выше (со Spring Boot), которое может работать в любом современном сервлет-контейнере (Jetty или Tomcat). Проект управляется с помощью системы сборки Gradle. Также в проекте предусмотрено развёртывание в контейнерах Docker с применением Liquibase-скриптов для БД. Об этом далее.

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
eb_app_container       | 2025-09-15 - 19:11:25.960 (Z)  INFO 1 ---> [main] liquibase.util : UPDATE SUMMARY
eb_app_container       | 2025-09-15 - 19:11:25.960 (Z)  INFO 1 ---> [main] liquibase.util : Run:                          5
eb_app_container       | 2025-09-15 - 19:11:25.960 (Z)  INFO 1 ---> [main] liquibase.util : Previously run:               0
eb_app_container       | 2025-09-15 - 19:11:25.960 (Z)  INFO 1 ---> [main] liquibase.util : Filtered out:                 0
eb_app_container       | 2025-09-15 - 19:11:25.960 (Z)  INFO 1 ---> [main] liquibase.util : -------------------------------
eb_app_container       | 2025-09-15 - 19:11:25.960 (Z)  INFO 1 ---> [main] liquibase.util : Total change sets:            5
eb_app_container       | 2025-09-15 - 19:11:25.961 (Z)  INFO 1 ---> [main] liquibase.util : Update summary generated
eb_app_container       | 2025-09-15 - 19:11:25.963 (Z)  INFO 1 ---> [main] liquibase.command : Update command completed successfully.
eb_app_container       | 2025-09-15 - 19:11:25.963 (Z)  INFO 1 ---> [main] liquibase.ui : Liquibase: Update has been successful. Rows affected: 11
eb_app_container       | 2025-09-15 - 19:11:25.967 (Z)  INFO 1 ---> [main] liquibase.lockservice : Successfully released change log lock
eb_app_container       | 2025-09-15 - 19:11:25.968 (Z)  INFO 1 ---> [main] liquibase.command : Command execution complete
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
В приложении также предусмотрено логирование. Логи пишутся непосредственно в консоль Tomcat, встроенного в наш Spring Boot. Ниже приведён пример логов:
```java
eb_app_container       | 2025-09-15 - 19:26:43.044 (Z)  INFO 1 ---> [http-nio-8080-exec-3] c.s.e.service.cart.CartServiceImpl : В корзине найдено 2 товаров.
eb_app_container       | 2025-09-15 - 19:26:43.052 (Z)  INFO 1 ---> [http-nio-8080-exec-3] c.s.e.c.i.RequestLoggingInterceptor : Запрос GET /easy-buy/cart/items выполнен за 19 мс.
eb_app_container       | 2025-09-15 - 19:27:50.460 (Z)  INFO 1 ---> [http-nio-8080-exec-5] c.s.e.service.cart.CartServiceImpl : В корзине найдено 2 товаров.
eb_app_container       | 2025-09-15 - 19:27:50.488 (Z)  INFO 1 ---> [http-nio-8080-exec-5] c.s.e.service.cart.CartServiceImpl : Корзина была очищена.
eb_app_container       | 2025-09-15 - 19:27:50.488 (Z)  INFO 1 ---> [http-nio-8080-exec-5] c.s.e.service.order.OrderServiceImpl : Сформирован заказ с ID 1 и количеством товаров 2.
eb_app_container       | 2025-09-15 - 19:27:50.515 (Z)  INFO 1 ---> [http-nio-8080-exec-5] c.s.e.c.i.RequestLoggingInterceptor : Запрос POST /easy-buy/buy выполнен за 59 мс.
eb_app_container       | 2025-09-15 - 19:27:50.532 (Z)  INFO 1 ---> [http-nio-8080-exec-6] c.s.e.service.order.OrderServiceImpl : Найден заказ с ID 1 и количеством товаров 2.
eb_app_container       | 2025-09-15 - 19:27:50.545 (Z)  INFO 1 ---> [http-nio-8080-exec-6] c.s.e.c.i.RequestLoggingInterceptor : Запрос GET /easy-buy/orders/1 выполнен за 25 мс.
eb_app_container       | 2025-09-15 - 19:29:18.092 (Z)  INFO 1 ---> [http-nio-8080-exec-9] c.s.e.service.order.OrderServiceImpl : Найдено 1 заказов.
eb_app_container       | 2025-09-15 - 19:29:18.096 (Z)  INFO 1 ---> [http-nio-8080-exec-9] c.s.e.c.i.RequestLoggingInterceptor : Запрос GET /easy-buy/orders выполнен за 9 мс.
```

Как можно заметить, в логи также пишется и время выполнения запроса. Данную возможность предоставляет [**RequestLoggingInterceptor**](https://github.com/MironovNikita/EasyBuy/blob/main/src/main/java/com/shop/easybuy/common/interceptor/RequestLoggingInterceptor.java) - компонент Spring, реализующий **HandlerInterceptor**, для логирования времени выполнения HTTP-запросов.

Особенности:
- Сохраняет время начала запроса в ThreadLocal.
- После завершения запроса выводит в лог метод, URI и время выполнения в миллисекундах.
- Автоматически очищает ThreadLocal после завершения запроса, чтобы избежать утечек памяти.

## ✅ Тестирование 🐞
Как говорилось ранее, для интеграционного тестирования предусмотрен TestContainer для БД. С его настройками можно ознакомиться [**здесь**](https://github.com/MironovNikita/EasyBuy/tree/main/src/test/java/com/shop/easybuy/testDB).

Во время выполнения тестов можем наблюдать запущенный контейнер БД:
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/containerDB.png">

</p>

####
Также для интеграционного тестирования предусмотрено два контекста:
1. [**BaseIntegrationTest**](https://github.com/MironovNikita/EasyBuy/blob/main/src/test/java/com/shop/easybuy/controller/BaseIntegrationTest.java) - настройка для полного интеграционного тестирования от контроллера до БД.
2. [**JpaTestConfig**](https://github.com/MironovNikita/EasyBuy/blob/main/src/test/java/com/shop/easybuy/annotation/JpaTestConfig.java) - настройка для интеграционного тестирования только слоя репозиториев с БД.

Стоит отметить, что контейнер поднимается единожды для всех тестов.
Тестовая БД заполняется также посредством использования Liquibase-скриптов. Но, что важно, для тестирования слоя репозитроиев таблица item не заполняется. Данная настройка достигается за счёт указания контекста применения:
```java
context="prod,controller-test"
```

Помимо заполнения данных через Liquibase-скрипты в некоторых тестах предусмотрено заполнение данных непосредственно для определённого теста. Пример представлен ниже:
```java
@Sql(statements = {
            "INSERT INTO orders(id, total, created_at) VALUES(1, 5000, '2025-09-14 21:56:39.047928')",
            "INSERT INTO order_items(id, order_id, item_id, count) VALUES(1, 1, 1, 1)",
            "INSERT INTO orders(id, total, created_at) VALUES(2, 10000, '2025-09-15 11:56:39.047928')",
            "INSERT INTO order_items(id, order_id, item_id, count) VALUES(2, 2, 1, 2)"
    })
```
<p align="center">

  <img src="https://github.com/MironovNikita/EasyBuy/blob/main/images/tests.png">

</p>

В результате тестирования завершается три контекста: 
```java
2025-09-15 - 22:41:23.194 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] o.s.o.j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-09-15 - 22:41:23.197 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] com.zaxxer.hikari.HikariDataSource : HikariPool-1 - Shutdown initiated...
2025-09-15 - 22:41:23.203 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] com.zaxxer.hikari.HikariDataSource : HikariPool-1 - Shutdown completed.
2025-09-15 - 22:41:23.206 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] o.s.o.j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-09-15 - 22:41:23.207 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] com.zaxxer.hikari.HikariDataSource : HikariPool-2 - Shutdown initiated...
2025-09-15 - 22:41:23.212 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] com.zaxxer.hikari.HikariDataSource : HikariPool-2 - Shutdown completed.
2025-09-15 - 22:41:23.215 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] o.s.o.j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2025-09-15 - 22:41:23.216 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] com.zaxxer.hikari.HikariDataSource : HikariPool-3 - Shutdown initiated...
2025-09-15 - 22:41:23.223 (+03:00)  INFO 18356 ---> [SpringApplicationShutdownHook] com.zaxxer.hikari.HikariDataSource : HikariPool-3 - Shutdown completed.
```
Третий контекст появляется из-за самого простого теста SpringBoot - **contextLoads()**.

## 🛠️ Зависимости проекта ⚙️
**Spring Boot Starters:**
- **``spring-boot-starter-web``** – для создания REST-контроллеров и веб-приложения.
- **``spring-boot-starter-thymeleaf``** – для рендеринга HTML-шаблонов с Thymeleaf.
- **``spring-boot-starter-data-jpa``** – для работы с базой данных через JPA.
- **``spring-boot-starter-validation``** – для валидации входящих данных.

**Базы данных и миграции:**
- **``postgresql``** – драйвер PostgreSQL.
- **``liquibase-core``** – для управления миграциями БД.

**Маппинг DTO:**
- **``mapstruct``** – генерация мапперов между DTO и сущностями.

**Ломбок:**
- **``lombok``** – сокращение шаблонного кода (геттеры, сеттеры, конструкторы и др.).

**Тестирование:**
- **``spring-boot-starter-test``** – тестирование Spring-приложений (JUnit, Mockito, Assertions).
- **``testcontainers и testcontainers-postgresql``** – интеграционные тесты с контейнерной БД PostgreSQL.

Компиляция и аннотации:
- **``annotationProcessor и compileOnly``** для Lombok и MapStruct – генерация кода на этапе компиляции.

Все указанные зависимости можно найти в [**build.gradle**](https://github.com/MironovNikita/EasyBuy/blob/main/build.gradle).

Результат сборки проекта:
```java
> Task :test
--------------------------------------------------
Тестов всего: 42
Успешно:      42
Провалено:    0
Пропущено:    0
Результат:    SUCCESS
--------------------------------------------------

> Task :check
> Task :build

BUILD SUCCESSFUL in 16s
9 actionable tasks: 9 executed
22:57:57: Execution finished 'clean build'.
```
