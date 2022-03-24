package x7;


import io.xream.x7.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
//@EnableX7L2Caching(timeSeconds = 300)
@EnableX7Repository(mappingPrefix = "t_",mappingSpec = "_")
@EnableReyClient
@EnableDateToLongForJackson
@EnableDistributionLock
@EnableCorsConfig("${access.domain}")
@EnableRateLimiter
@EnableLogWithTraceId
public class App {

    public static void main( String[] args )
    {
    	SpringApplication.run(App.class);
    }

}