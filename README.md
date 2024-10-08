<h1 align="center">Rent&Ride</h1>
<h3 align="center">Содержание:</h3>
1. Описание приложения и функционала<br/>
2. Архитектура приложения<br/>
3. Схема структуры базы данных<br/>
4. Запускаем приложение<br/>
5. API взаимодействие с приложением<br/>
6. Использованные технологии и их версии<br/>


### I. Описание приложения и функционала
Перед вами приложение, предназначенное для для удобной аренды и оплаты транспортных средств.
Кроме базового функционала приложения по принципу получения данных, создания, обновления,
и удаления всех доступных моделей приложения, в приложении предоставлен весьма удобный и практичный на сегодня функционал. 
Приложение включает продвинутые функции управления пользователями и мощные инструменты для администраторов, создавая удобную и эффективную систему аренды, как для пользователя, так и для администратора 

В первую очередь, приложение предлагает удобную систему авторизации и аутентификации по логину и паролю с использованием jwt токенов, 
при которой доступ к приложению осуществляется ограничено пользователю с ролью ROLE_USER и полный доступ ко всем возможностям с ролью ROLE_ADMIN. Для этого я использовал возможности AOP и создал свой аспект для проверки роли пользователя.
При регистрации пользователь автоматически получает роль обычного пользователя.
Для регистрации понадобится логин, пароль, почта (для уведомлений о начале/конце аренды).
После регистрации в базе данных создается запись с данными пользователя, (пароль в бд шифруется).
И при последующем использовании приложения пользователю нужно будет лишь вписать свои логин 
и пароль и если они валидны, то он получит свой личный jwt токен для последующих операций.
Для аренды транспорта ему нужно будет добавить карту для оплаты поездок, карта проверяется на валидность и cvv карты так же надежно шифруется в БД.

После регистрации и аутентификации, пользователю будет доступен основной функционал приложения и
конечно самое главное это возможность взять в аренду конкретный выбранный транспорт. 
Перед арендой пользователь может удобно посмотреть информацию о транспорте. Пользователю, в виде объекта JSON, вернется список из базы данных свободных транспортных средств, которые ему доступны для аренды. 
Кроме этого можно фильтровать перечень транспорта: по модели, уровню топлива, заряду батареи, и др.

Для старта аренды пользователю нужно найти понравившийся "транспорт компании" и ввести его номерной знак в приложении, с его счета спишется начальная сумма за старт поездки и затем согласно поминутному тарифу для конкретного вида транспорта будет считаться время использований транспорта и конечная сумма спишется уже после того как пользователь вернет транспорт на место.

При начале аренды пользователь получит уведомление на электронную почту о старте поездки, а по её завершении — письмо с подтверждением успешного окончания аренды.


### II. Архитектура приложения
![img_2](https://github.com/user-attachments/assets/d9d9711e-cae0-4d22-aeae-0df08630c58f)


Для обнаружения и регистрации микросервисов я выбрал Eureka, которая входит в стек Spring Cloud. Эта система позволяет каждому микросервису регистрироваться в общем реестре, что упрощает динамическое обнаружение доступных сервисов и управление ими.

После настройки Eureka, я настроил маршрутизацию запросов через API Gateway. Это позволяет автоматически перенаправлять запросы к нужным сервисам, обеспечивая гибкость в управлении трафиком и упрощая масштабирование системы. Такая архитектура делает систему более отказоустойчивой и упрощает добавление новых сервисов, без необходимости вручную настраивать маршруты.

###  III. Схема структуры базы данных
![img_1](https://github.com/user-attachments/assets/cd34d217-b335-4053-81dd-341754053701)


###  VI. Запускаем приложение
Запуск без использования docker
1. Клонируем проект в среду разработки
2. В директории gateway в файле .env убеждаемся что адрес для kafka стоит: localhost:9092
3. Запускаем локально zookeeper и kafka (либо же в docker compose файле можно запустить 2 контейнера с ними)
4. Создаем базу postgres с названием app_db
5. Запускаем приложения в таком порядке: 1. Eureka-server -> 2. Auth-service -> 3.Vehicle-service -> 4.Rental-service -> 5. Payment-service -> 6.Notification-service -> 7. Gateway


Запуск с docker
1. Клонируем проект в среду разработки
2. В директории gateway в файле .env убеждаемся что адрес для kafka стоит: kafka:9092
3. Убедитесь что Java 17, docker compose 3.x
4. Запустить приложение можно терминальной командой "cd .\gateway\; docker-compose up -d" (в настройках docker desktop лучше установить параметр Memory limit от 4gb)
5. При успешном запуске приложения, можно пользоваться подключенными API


### Вход в систему
Есть 2 пользователя админ и пользователь

Пользователь с ролью "ROLE_ADMIN"
Логин: admin
Пароль: admin

Пользователь с ролью "ROLE_USER"
Логин: user
Пароль: user


### V. API взаимодействие с приложением

1. **Аутентификация и Регистрация**

1.1 Аутентификация пользователя<br> URL: http://localhost:8080/api/v1/auth/signin<br> Метод: POST<br> Параметры тела запроса: AuthenticationRequest loginRequest (содержит username и password)<br> Доступно для: Неавторизованных пользователей<br> Метод сервиса: authenticateUser()<br> Описание: Выполняет аутентификацию пользователя. В случае успеха возвращает JWT токен.<br>

1.2  Регистрация пользователя<br> URL: http://localhost:8080/api/v1/auth/signup<br> Метод: POST<br> Параметры тела запроса: SignupRequest signUpRequest (содержит username, email, password)<br> Доступно для: Неавторизованных пользователей<br> Метод сервиса: registerUser()<br> Описание: Регистрирует нового пользователя в системе. Возвращает сообщение об успешной регистрации.<br>

2. **Управление пользователями**

2.1 Поиск пользователя по имени пользователя<br> URL: http://localhost:8080/api/v1/users/find/{username}<br> Метод: GET<br> Параметр пути: String username (имя пользователя)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: findUserByUsername()<br> Описание: Возвращает данные пользователя по его имени.<br>

2.2 Назначение роли администратора<br> URL: http://localhost:8080/api/v1/users/grant-admin/{id}<br> Метод: POST<br> Параметр пути: Long id (идентификатор пользователя, которому назначается роль администратора)<br> Доступно для: ADMIN<br> Метод сервиса: grantAdminRole()<br> Описание: Назначает указанному пользователю роль администратора.<br>

2.3 Забанить пользователя (только для админа)<br> URL: http://localhost:8080/api/v1/users/ban/{id}<br> Метод: POST<br> Параметр пути: Long id (идентификатор пользователя)<br> Доступно для: ADMIN<br> Метод сервиса: banUser()<br> Описание: Забанивает пользователя по его идентификатору. Заблокированный пользователь не сможет авторизоваться и использовать систему.<br>

2.4 Разбанить пользователя (только для админа)<br> URL: http://localhost:8080/api/v1/users/unban/{id}<br> Метод: POST<br> Параметр пути: Long id (идентификатор пользователя)<br> Доступно для: ADMIN<br> Метод сервиса: unBanUser()<br> Описание: Разбанивает пользователя по его идентификатору. Разбаненный пользователь сможет снова авторизоваться и использовать систему.<br>

2.5 Обновить данные пользователя<br> URL: http://localhost:8080/api/v1/users/update/{id}<br> Метод: PUT<br> Параметр пути: Long id (идентификатор пользователя)<br> Параметры тела запроса: User updatedUser (обновленные данные пользователя)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: updateUser()<br> Описание: Обновляет данные пользователя по его идентификатору. Возвращает обновленную информацию о пользователе.<br>

2.6 Удалить пользователя (только для админа)<br> URL: http://localhost:8080/api/v1/users/delete/{id}<br> Метод: DELETE<br> Параметр пути: Long id (идентификатор пользователя)<br> Доступно для: ADMIN<br> Метод сервиса: deleteUser()<br> Описание: Удаляет пользователя по его идентификатору. После удаления пользователь больше не сможет авторизоваться и использовать систему.<br>

3. **Управление картами и средствами**

3.1 Добавление карты<br> URL: http://localhost:8080/api/v1/cards<br> Метод: POST<br> Параметры тела запроса: CardRequest cardRequest (содержит информацию о карте)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: addCard()<br> Описание: Добавляет новую карту пользователю.<br>

3.2 Добавление средств на карту<br> URL: http://localhost:8080/api/v1/cards/funds<br> Метод: POST<br> Параметры запроса: BigDecimal amount (сумма для добавления)<br> Доступно для: ADMIN<br> Метод сервиса: addFunds()<br> Описание: Добавляет указанную сумму на карту пользователя.<br>

3.3 Снятие средств с карты<br> URL: http://localhost:8080/api/v1/cards/funds/deduct<br> Метод: POST<br> Параметры запроса: BigDecimal amount (сумма для снятия)<br> Доступно для: ADMIN<br> Метод сервиса: deductFunds()<br> Описание: Снимает указанную сумму с карты пользователя.<br>

3.4 Получение баланса<br> URL: http://localhost:8080/api/v1/cards/balance<br> Метод: GET<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: getBalance()<br> Описание: Возвращает текущий баланс карты пользователя.<br>

4. **Управление арендами**

4.1 Аренда транспортного средства<br> URL: http://localhost:8080/api/v1/rentals/rent<br> Метод: POST<br> Параметры тела запроса: RentalRequest rentalRequest (содержит информацию о транспортном средстве и сроке аренды)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: rentVehicle()<br> Описание: Позволяет пользователю арендовать транспортное средство.<br>

4.2 Возврат транспортного средства<br> URL: http://localhost:8080/api/v1/rentals/return<br> Метод: POST<br> Параметры тела запроса: ReturnRequest returnRequest (содержит информацию о возврате транспортного средства)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: returnVehicle()<br> Описание: Позволяет пользователю вернуть арендованное транспортное средство.<br>

4.3 Получение аренды по идентификатору<br> URL: http://localhost:8080/api/v1/rentals/{rentalId}<br> Метод: GET<br> Параметр пути: Long rentalId (идентификатор аренды)<br> Доступно для: ADMIN<br> Метод сервиса: getRentalById()<br> Описание: Возвращает информацию об аренде по её идентификатору.<br>

4.4 Получение всех аренд пользователя<br> URL: http://localhost:8080/api/v1/rentals/users/{userId}/rentals<br> Метод: GET<br> Параметр пути: Long userId (идентификатор пользователя)<br> Доступно для: ADMIN<br> Метод сервиса: getRentalsByUserId()<br> Описание: Возвращает все аренды, связанные с указанным пользователем.<br>

5. **Управление транспортными средствами**

5.1 Получение всех транспортных средств<br> URL: http://localhost:8080/api/v1/vehicles<br> Метод: GET<br> Доступно для: ADMIN<br> Метод сервиса: getAllVehicles()<br> Описание: Возвращает список всех транспортных средств.<br>

5.2 Получение транспортного средства по идентификатору<br> URL: http://localhost:8080/api/v1/vehicles/{id}<br> Метод: GET<br> Параметр пути: Long id (идентификатор транспортного средства)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: getVehicleById()<br> Описание: Возвращает информацию о транспортном средстве по его идентификатору.<br>

5.3 Создание транспортного средства<br> URL: http://localhost:8080/api/v1/vehicles/create<br> Метод: POST<br> Параметры тела запроса: VehicleCreateDTO vehicleCreateDTO (содержит информацию о транспортном средстве)<br> Доступно для: ADMIN<br> Метод сервиса: createVehicle()<br> Описание: Создаёт новое транспортное средство в системе.<br>

5.4 Удаление транспортного средства<br> URL: http://localhost:8080/api/v1/vehicles/{id}<br> Метод: DELETE<br> Параметр пути: Long id (идентификатор транспортного средства)<br> Доступно для: ADMIN<br> Метод сервиса: deleteVehicle()<br> Описание: Удаляет транспортное средство из системы по его идентификатору.<br>

5.5 Получение транспортного средства по номерному знаку<br> URL: http://localhost:8080/api/v1/vehicles/plate/{plate}<br> Метод: GET<br> Параметр пути: String plate (номерной знак транспортного средства)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: getByPlate()<br> Описание: Возвращает транспортное средство по его номерному знаку.<br>

5.6 Обновление транспортного средства<br> URL: http://localhost:8080/api/v1/vehicles/{id}<br> Метод: PUT<br> Параметры тела запроса: VehicleCreateDTO vehicleCreateDTO (содержит обновлённую информацию о транспортном средстве)<br> Параметр пути: Long id (идентификатор транспортного средства)<br> Доступно для: ADMIN<br> Метод сервиса: updateVehicle()<br> Описание: Обновляет данные о транспортном средстве.<br>

5.7 Получение доступных транспортных средств<br> URL: http://localhost:8080/api/v1/vehicles/available<br> Метод: GET<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: getAvailableVehicles()<br> Описание: Возвращает список всех доступных транспортных средств.<br>

5.8 Изменение статуса транспортного средства<br> URL: http://localhost:8080/api/v1/vehicles/{id}/status/{status}<br> Метод: POST<br> Параметр пути: Long id (идентификатор транспортного средства), Status status (новый статус)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: setVehicleStatusById()<br> Описание: Изменяет статус транспортного средства на указанный.<br>

5.9 Поиск транспортных средств по модели<br> URL: http://localhost:8080/api/v1/vehicles/search/{model}<br> Метод: GET<br> Параметр пути: String model (модель транспортного средства)<br> Доступно для: AUTHENTICATED USER<br> Метод сервиса: searchVehiclesByModel()<br> Описание: Ищет транспортные средства по указанной модели.<br>

### VI. Использованные технологии и их версии

* Spring Boot - 3.3.2 v.
* Spring Security - 3.2. v.
* Spring Cloud - 2023.0.3 v.
* Java - 17 v.
* Kafka - 3.3.1 v.
* Postgresql - 42.7.2 v.
* Liquibase - 4.28.0 v.
* Docker - 3.8 v.
* Eureka 4.1.3 v.
* Spring Boot Starter Mail 3.3.2 v.
