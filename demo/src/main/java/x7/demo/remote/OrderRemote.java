package x7.demo.remote;

import io.xream.rey.annotation.ReyClient;
import org.springframework.web.bind.annotation.RequestMapping;

@ReyClient(value = "http://${web.demo}/order")
public interface OrderRemote {

    @RequestMapping("/verify")
    boolean verify();
}
