
    1. Tworzymy projekt SpringBootClient
    2. Omówić application.properties, pom, parent pom, dependencies pom - pokazać że każda wersja przychodzi z określonymi wersjami zależności. Dzięki temu mamy pewność że wszystko działa i jest przetestowane ze sobą. 
    3. Wystartować aplikację - omówić co widać przy starcie. 
    4. Tworzymy klase HolidayRequest 
    5. Tworzymy klasę holidayRequestRepo
    6. Tworzymy enpoint + get, add
    7. Odpalamy aplikację  - http://localhost:8080/request
    8. Odpalamy postmana robimy POST na /request - {"number":"1"}
    9. Co widzimy? nie musieliśmy konfigurować DS, transaction managera, wszytko jest skonfigurowane za nas przez springa
    10. jak to działa, wchodzimy w DataSourceAutoConfiguration, potem w EmbeddedDataSourceConfiguration 
    11. spring.h2.console.enabled=true    jdbc:h2:mem:testdb  
    12. pokazać jak on class condition i on bean condition
    13. ServerProperties, DataSourceProperties
    14. Dodajemy baner.txt + http://www.network-science.de/ascii/
    15. Instalujemy ActiveMq 
    16. > activemq.bat start
    17. http://localhost:8161/admin/ - admin admin
    18. dodajemy do pom : 


     <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jms</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.activemq</groupId>
            <artifactId>activemq-broker</artifactId>
        </dependency>


     spring.activemq.broker-url=tcp://localhost:61616
     spring.activemq.user=admin
     spring.activemq.password=admin



    1. dodajemy jmsclient



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



    1. Dodajemy @EnableJMS




    1. Tworzymy projekt server
    2. Instalujemy mongo i robomongo 



    1. Tworzymy własną reprezentacje holidayRequest

    2. public interface HolidayRequestRepo extends MongoRepository<HolidayRequest, String> {
    3. tworzymy jms receiver


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


    1. Podłączamy się do mongo baza test
    2. dodajemy HolidayRequestService z metodą count


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



    1. DODAJEMY ACURATOR



     <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
     </dependency>


    1. acurator 

        1. /health
        2. /env
        3. /mappings
        4. /autoconfig
        5. /trace
        6. /beans
        7. /metrics




    1. Dodajemy własny healthIndicator :



@Component
public class MyHealthIndicator implements HealthIndicator{
    @Override
    public Health health() {
        return Health.up().status(Status.UNKNOWN).build();
    }
}



    1. Dodajemy opis aplikacji :



http://localhost:8080/info

info.app.name=Server
info.app.description=My awesome service
info.app.version=1.0.0



    1. Dodajemy własne metryki :


http://localhost:8080/metrics
counterService.increment("services.system.holidayService.invoked");



    1. DODAJEMY SSH



http://www.crashub.org/1.3/reference.html

     <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-remote-shell</artifactId>
     </dependency>


    1. putty >> user@localhost 2000 

    2. help
    3. beans
    4. jvm heap



    1. shell.auth.simple.user.password=123




    1. właśny command



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



    1. spring dev tools 

     <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
     </dependency>


    1. testowanie aplikacji :



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






