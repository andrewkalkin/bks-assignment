Данный проект является решением задачи https://gist.github.com/mzezin/bdc774839157f4d63e8bf22757cbc34f

### Сборка проекта

Перед сборкой положьте файл `credentials.properties` в директории:
- src/main/resources
- src/test/resources

В нем пропишите ключ `iexcloud.user.publishableToken` с публичным токеном к api https://iexcloud.io/.

Пример: `iexcloud.user.publishableToken=pk_23jhjdh32j43h443h43h43h4h13h44`

Проект собирается запуском команды `gradlew bootJar` в директории с проектом. После выполнения команды сборку можно найти по пути `build/libs/bks-assignment-x.x.x.jar`.

### Запуск приложения

Запускается командой `java -jar build/libs/bks-assignment-x.x.x.jar`. 
По умолчанию приложение стартует на порту 8080. Поэтому api можно дергать по пути http://localhost:8080/assets.

### Тестирование

Тесты запускаются командной `gradle test`
