# Shareit

Технологии: Java + Spring Boot + Docker + PostgreSQL + Hibernate + Maven + JUnit5 + Mockito + Lombok

---
Сервис решает проблему связанную с необходимостью приобретения вещей для временного использования. Вместо того чтобы покупать новую вещь, пользователи могут найти ее на сервисе и взять в аренду на определенное время. Это позволяет экономить деньги и ресурсы, а также уменьшает нагрузку на окружающую среду.

Функционал сервиса позволяет бронировать вещь на определенные даты и закрывает к ней доступ на время бронирования от других желающих. Если нужной вещи на сервисе нет, пользователи могут оставлять запросы, по которым можно добавлять новые вещи для шеринга. Это обеспечивает удобство и гибкость для пользователей и помогает им находить нужные вещи для временного использования. Так же реализована возможность оставлять отзывы после того как пользователь воспользовался вещью.


## Микросервисная архитектура

Приложение состоит из 2 сервисов:
- Gateway. Принимает запросы от пользователей. Распределяет нагрузку, выполняет первичную проверку и направляет запросы дальше в основной сервис
- Server. Серверная часть приложения. Получает запросы, выполняет операции, отправляет данные клиенту

## Установка и запуск проекта
Необходимо настроенная система виртуализации, установленный Docker Desktop(скачать и установить можно с официального сайта https://www.docker.com/products/docker-desktop/)

1. Клонируйте репозиторий проекта на свою локальную машину:
   ```
   git clone git@github.com:terofitus/java-shareit.git
   ```
2. Запустите командную строку и перейдите в корень директории с проектом.
3. Соберите проект
   ```
   mvn clean package
   ```
4. Введите следующую команду, которая подготовит и запустит приложение на вашей локальной машине
   ```
   $  docker-compose up
   ```
5. Приложение будет запущено на порту 8080. Вы можете открыть свой веб-браузер и перейти по адресу `http://localhost:8080`, чтобы получить доступ к приложению Share It.


Эндпоинты
---
- POST /bookings/ - добавляет запрос на бронирование вещи.
- PATCH /bookings/{bookingId} - обновляет статус бронирования. Подтверждение или отклонение запроса на бронирование.
- GET /bookings/{bookingId} - получение данных о конкретном бронировании (включая его статус).
- GET /bookings?state={state} получение списка всех бронирований текущего пользователя.
- GET /bookings/owner?state={state} - получение списка бронирований для всех вещей текущего пользователя.
---
- GET /items/{id} - получать данные вещи по идентификатору
- GET /items/ - получать данные всех вещей
- POST /items/ - добавление вещи
- PATCH /items/{id} - обновление вещи по id
- DELETE /items/{id} - удаление вещи по id
- POST /items/{itemId}/comment - добавление отзывов на вещь после того, как взяли её в аренду
---
- POST /requests - добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает, какая именно вещь ему нужна
- GET /requests — получить список своих запросов вместе с данными об ответах на них.
- GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
- GET/requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том же формате,
---
- GET /users/{id} - получать пользователя по идентификатору
- GET /users/ - получать всех пользователей
- POST /users/ - добавлять пользователя в память
- PATCH /users/{id} - обновление пользователя по id
- DELETE  /users/{id} - удаление пользователя по id

---
