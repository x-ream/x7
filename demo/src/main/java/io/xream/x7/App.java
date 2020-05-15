package io.xream.x7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 *
 * Demo
 *
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableReadOnly
@EnableX7L2Caching(timeSeconds = 120)
@EnableX7Repository(mappingPrefix = "t_",mappingSpec = "_")
@EnableReyClient
@EnableTracingServlet
@EnableDateToLongForJackson
@EnableX7L3Caching(waitTimeMills = 1000)
@EnableDistributionLock
@EnableFallbackOnly
@EnableCorsConfig("${access.domain}")
public class App {

    public static void main( String[] args )
    {
    	SpringApplication.run(App.class);
    }

}