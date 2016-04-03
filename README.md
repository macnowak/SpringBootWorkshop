<h2> Spring Boot Workshop </h2>

- Tworzymy projekt SpringBootClient
- Omówić application.properties, pom, parent pom, dependencies pom - pokazać że każda wersja przychodzi z określonymi            wersjami zależności. Dzięki temu mamy pewność że wszystko działa i jest przetestowane ze sobą. 
- Wystartować aplikację - omówić co widać przy starcie. 
-  Tworzymy klase HolidayRequest 
- Tworzymy klasę holidayRequestRepo
- Tworzymy enpoint + get, add
- Odpalamy aplikację  - http://localhost:8080/request
- Odpalamy postmana robimy POST na /request - {"number":"1"}
- Co widzimy? nie musieliśmy konfigurować DS, transaction managera, wszytko jest skonfigurowane za nas przez springa
- jak to działa, wchodzimy w DataSourceAutoConfiguration, potem w EmbeddedDataSourceConfiguration 
- spring.h2.console.enabled=true    jdbc:h2:mem:testdb  
- pokazać jak on class condition i on bean condition
- ServerProperties, DataSourceProperties
- Dodajemy baner.txt + http://www.network-science.de/ascii/
- Instalujemy ActiveMq 
- > activemq.bat start
- http://localhost:8161/admin/ - admin admin
- dodajemy do pom : 

```
     <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-broker</artifactId>
        </dependency>
```

```
     spring.activemq.broker-url=tcp://localhost:61616
     spring.activemq.user=admin
     spring.activemq.password=admin
```

- dodajemy jmsclient

```
     @Component
     public class JmsClient {
          private JmsTemplate jmsTemplate;
          private ObjectMapper objectMapper;
     
          @Autowired
         public JmsClient(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
             this.jmsTemplate = jmsTemplate;
             this.objectMapper = objectMapper;
         }
         public void send(HolidayRequest request) throws JsonProcessingException {
             String message = objectMapper.writeValueAsString(request);
             MessageCreator messageCreator = new MessageCreator() {
                 @Override
                 public Message createMessage(Session session) throws JMSException {
                     return session.createTextMessage(message);
                 }
             };
             System.out.println("Sending a new message.");
             jmsTemplate.send("mailbox-destination", messageCreator);
         }
     }
```

- Dodajemy @EnableJMS
- Tworzymy projekt server
- Instalujemy mongo i robomongo 
- Tworzymy własną reprezentacje holidayRequest
- public interface HolidayRequestRepo extends MongoRepository<HolidayRequest, String> {
- tworzymy jms receiver

```
@Component
public class JmsReceiver {
    private ObjectMapper objectMapper;
    private HolidayRequestRepo holidayRequestRepo;


    @Autowired
    public JmsReceiver(ObjectMapper objectMapper, HolidayRequestRepo holidayRequestRepo) {
        this.objectMapper = objectMapper;
        this.holidayRequestRepo = holidayRequestRepo;
    }


    @JmsListener(destination = "mailbox-destination")
    public void receiveMessage(String message) throws IOException {
        HolidayRequest request = objectMapper.readValue(message, HolidayRequest.class);
        System.out.println("Received <" + request + ">");
        holidayRequestRepo.save(request);
    }
}
```

- Podłączamy się do mongo baza test
- dodajemy HolidayRequestService z metodą count

```
@Component
public class HolidayRequestServiceImpl implements HolidayRequestService {
    private final HolidayRequestRepo holidayRequestRepo;


    @Autowired
    public HolidayRequestServiceImpl(HolidayRequestRepo holidayRequestRepo) {
        this.holidayRequestRepo = holidayRequestRepo;
    }


    @Override
    public Long getRequestCount() {
        return holidayRequestRepo.count();
    }
}
```
- DODAJEMY ACURATOR
```
     <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
     </dependency>
```
- acurator 
        1. /health
        2. /env
        3. /mappings
        4. /autoconfig
        5. /trace
        6. /beans
        7. /metrics

- Dodajemy własny healthIndicator :
```
@Component
public class MyHealthIndicator implements HealthIndicator{
    @Override
    public Health health() {
        return Health.up().status(Status.UNKNOWN).build();
    }
}
```

- Dodajemy opis aplikacji :

http://localhost:8080/info

info.app.name=Server
info.app.description=My awesome service
info.app.version=1.0.0

- Dodajemy własne metryki :

http://localhost:8080/metrics
counterService.increment("services.system.holidayService.invoked");

- DODAJEMY SSH

http://www.crashub.org/1.3/reference.html

```
     <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-remote-shell</artifactId>
     </dependency>
```

- putty >> user@localhost 2000 

- help
- beans
- jvm heap
- shell.auth.simple.user.password=123
- właśny command
```
package commands

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory
import pl.decerto.workshop.HolidayRequestService

class requestCounterCommand {


    @Usage("count requests")
    @Command
    def main(InvocationContext context) {
        BeanFactory beans = context.attributes['spring.beanfactory']
        def requestEndpoint = beans.getBean(HolidayRequestService)
        return requestEndpoint.getRequestCount();
    }
}
```
- spring dev tools 
```
     <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
     </dependency>
```

- testowanie aplikacji :
```
     @RunWith(SpringJUnit4ClassRunner.class)
     @SpringApplicationConfiguration(classes = ClientApplication.class)
     @WebAppConfiguration
     public class HolidayRequestEndpointTest {
     
         @Autowired
         private WebApplicationContext webApplicationContext;
         private MockMvc mockMvc;
     
         @Before
         public void setUp() throws Exception {
             mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
         }
     
     
         @Test
         public void testGetEndpoint() throws Exception {
             mockMvc.perform(MockMvcRequestBuilders.get("/request")).andExpect(MockMvcResultMatchers.status().isOk());
         }
     }
```
