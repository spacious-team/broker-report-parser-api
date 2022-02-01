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
        <id>central</id>
        <name>Central Repository</name>
        <url>https://repo.maven.apache.org/maven2</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
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

Если брокер предоставляет отчет в формате excel файла, можете воспользоваться существующим excel
[парсером](https://github.com/spacious-team/table-wrapper-excel-impl)
```xml
<dependency>
    <groupId>com.github.spacious-team</groupId>
    <artifactId>table-wrapper-excel-impl</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```
если в формате xml, то xml [парсером](https://github.com/spacious-team/table-wrapper-xml-impl)
 ```xml
 <dependency>
     <groupId>com.github.spacious-team</groupId>
     <artifactId>table-wrapper-xml-impl</artifactId>
     <version>master-SNAPSHOT</version>
 </dependency>
 ```
или напишите библиотеку парсера для другого формата, используя
[Table Wrapper API](https://github.com/spacious-team/table-wrapper-api).

### Документация по реализации API
Главным объектом расширения является объект, реализующий интерфейс `BrokerReport`. Этот класс является оберткой
над файлом-отчетом брокера, из которого в дальнейшем будет получена информация. Создайте этот класс, например
`MyBrokerReport`, и предоставьте его через фабрику
```java
public class MyBrokerReportFactory extends AbstractBrokerReportFactory {
    
    private final Pattern expectedFileNamePattern = Pattern.compile("^My_broker_[0-9()\\-_]+\\.xml$");

    public String getBrokerName() {
        return "MyBroker";
    }

    @Override
    public boolean canCreate(String reportFileName, InputStream is) {
        return super.canCreate(expectedFileNamePattern, reportFileName, is);
    }
    
    @Override
    public BrokerReport create(String reportFileName, InputStream is) {
        return  super.create(reportFileName, is, MyBrokerReport::new);
    }
}
```
Далее реализуйте интерфейс `ReportTables`, который предоставит информацию из отчета брокера в виде объектов `ReportTable`.
Пример реализации класса `ReportTables` доступен по
[ссылке](https://github.com/spacious-team/investbook/blob/develop/src/main/java/ru/investbook/parser/psb/foreignmarket/PsbForeignMarketReportTables.java)
```java
public class MyReportTables extends AbstractReportTables<MyBrokerReport> {

    public MyReportTables(MyBrokerReport report) {
        super(report);
    }

    @Override
    public ReportTable<Security> getSecuritiesTable() {
        return emptyTable();
    }
    // другие методы ...
```
Пример реализации `ReportTable` также в свободном
[доступе](https://github.com/spacious-team/investbook/blob/develop/src/main/java/ru/investbook/parser/psb/SecuritiesTable.java).

Обратите внимание, ответ брокера может не содержать всей информации, например если брокер не предоставляет информации
о котировках, можно вернуть заглушку `emptyTable()`. На первом этапе вы можете парсить из отчета
брокера только часть информации, для информации, которую не парсите просто верните заглушку.

Когда интерфейс `ReportTables` реализован, нужно создать для него фабричный класс
```java
public class MyReportTablesFactory implements ReportTablesFactory {
    @Override
    public boolean canCreate(BrokerReport brokerReport) {
        return (brokerReport instanceof ByBrokerReport);
    }

    @Override
    public ReportTables create(BrokerReport brokerReport) {
        return new MyReportTables(brokerReport);
    }
}
```
Парсер отчетов брокера готов. Соберите jar-архив
```shell script
mvn clean install
```
