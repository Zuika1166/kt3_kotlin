# KT3 Kotlin

Backend-приложение на Kotlin и Ktor с CRUD для книг, JWT-аутентификацией, Swagger UI, OpenAPI, централизованной обработкой ошибок и логированием запросов.

## Требования

- JDK 21
- Gradle

## Запуск

```bash
gradle run
```

Приложение запускается по адресу:

```text
http://localhost:8080
```

Для изменения JWT-секрета:

```bash
JWT_SECRET=my-strong-secret gradle run
```

Данные хранятся в памяти и сбрасываются после перезапуска приложения.

## Swagger UI

После запуска Swagger UI доступен по адресу:

```text
http://localhost:8080/swagger
```

OpenAPI-документация доступна по адресу:

```text
http://localhost:8080/openapi
```

Спецификация описывает регистрацию, вход и все CRUD-маршруты книг, включая тела запросов, ответы, коды ошибок и Bearer JWT.

## Тестирование

Запуск всех тестов:

```bash
gradle test
```

Тесты проверяют:

- доступность Swagger и OpenAPI;
- централизованную обработку 400 и 404 через StatusPages;
- регистрацию и вход;
- ошибку при повторной регистрации;
- ошибку при неверном пароле;
- защиту маршрута создания книги без JWT;
- создание, чтение, обновление и удаление книги.

## Логирование

Для логирования HTTP-запросов используется CallLogging. Запросы выводятся через SLF4J и Logback.

## Маршруты

| Метод | Маршрут | Доступ |
| --- | --- | --- |
| POST | /auth/register | публичный |
| POST | /auth/login | публичный |
| GET | /books | публичный |
| GET | /books/{id} | публичный |
| POST | /books | Bearer JWT |
| PUT | /books/{id} | Bearer JWT |
| DELETE | /books/{id} | Bearer JWT |

## Регистрация

```bash
curl -i -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"login":"john","password":"secret123"}'
```

## Вход

```bash
curl -i -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"john","password":"secret123"}'
```

В ответе возвращается JWT. Значение поля `token` используется в защищённых запросах.

## Список книг

```bash
curl -i http://localhost:8080/books
```

## Книга по id

```bash
curl -i http://localhost:8080/books/1
```

## Создание книги

```bash
curl -i -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Clean Code","author":"Robert C. Martin","year":2008}'
```

## Обновление книги

```bash
curl -i -X PUT http://localhost:8080/books/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"title":"Clean Code","author":"Robert C. Martin","year":2009}'
```

## Удаление книги

```bash
curl -i -X DELETE http://localhost:8080/books/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```
