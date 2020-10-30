![java-version](https://img.shields.io/badge/Java-11-brightgreen?style=flat-square)
![jitpack-last-release](https://jitpack.io/v/spacious-team/broker-report-parser-api.svg?style=flat-square)

### Назначение
Предоставляет API библиотек для парсинга файловых отчетов брокера о совершенных сделках на финансовых рынках.

### Как использовать в своем проекте
Необходимо подключить репозиторий open source библиотек github [jitpack](https://jitpack.io/#spacious-team/broker-report-parser-api),
например для Apache Maven проекта
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
и добавить зависимость
```xml
<dependency>
    <groupId>com.github.spacious-team</groupId>
    <artifactId>broker-report-parser-api</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```
В качестве версии можно использовать:
- версию [релиза](https://github.com/spacious-team/broker-report-parser-api/releases) на github;
- паттерн `<branch>-SNAPSHOT` для сборки зависимости с последнего коммита выбранной ветки;
- короткий 10-ти значный номер коммита для сборки зависимости с указанного коммита.
