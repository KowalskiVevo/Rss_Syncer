##Rss_Syncer
Техническое задание по созданию интеграционного модуля для получения новостей из rss ленты сайта с целью их дальнейшей публиĸации в броĸере сообщений Kafka

#Инструкция по запуску:
1. В файле application.properties можно изменить настройки бд и кафки.
  Настройки БД:
```YML
      spring.datasource.driver-class-name=org.postgresql.Driver
      spring.datasource.url=jdbc:postgresql://localhost:5432/test
      spring.datasource.name=rss_syncer
      spring.datasource.password=123
      spring.flyway.url=jdbc:postgresql://localhost:5432/test
      spring.flyway.user=postgres
      spring.flyway.password=123
```
  Настройки kafka:
```YML
      spring.kafka.consumer.group-id=app.1
      spring.kafka.bootstrap-servers=localhost:9092
```

2. Очистите и соберите Maven:
```shell
$ mvn clean install
```
