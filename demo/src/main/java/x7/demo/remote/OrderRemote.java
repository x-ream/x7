package x7.demo.remote;

import io.xream.x7.annotation.ReyClient;
import org.springframework.web.bind.annotation.RequestMapping;

@ReyClient(value = "http://${web.demo}/order",circuitBreaker = "")
public interface OrderRemote {

    @RequestMapping("/verify")
    boolean verify();
}
