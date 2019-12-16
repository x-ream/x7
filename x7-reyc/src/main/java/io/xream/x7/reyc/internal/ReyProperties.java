package io.xream.x7.reyc.internal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReyProperties {

    @Value("${x7.reyc.fallback.remote-exception:'Exception'}")
    private String remoteException;

    public String getRemoteException() {
        return remoteException;
    }

    public void setRemoteException(String remoteException) {
        this.remoteException = remoteException;
    }
}
