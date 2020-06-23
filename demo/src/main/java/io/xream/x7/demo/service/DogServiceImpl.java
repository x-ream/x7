package io.xream.x7.demo.service;

import io.xream.x7.demo.bean.DogTest;
import io.xream.x7.lock.Lock;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DogServiceImpl implements DogService{

    @Lock(timeout = 2000)
    @Override
    public boolean lock5(DogTest dogTest) {

        try{
            TimeUnit.MILLISECONDS.sleep(5000);
        }catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("unlock after 5000ms");

        return true;
    }

    @Lock(timeout = 7000)
    @Override
    public boolean lock6(DogTest dogTest) {

        try{
            TimeUnit.MILLISECONDS.sleep(6000);
        }catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("unlock after 6000ms");

        return true;
    }

    @Lock(timeout = 2000)
    @Override
    public boolean lock0(DogTest dogTest) {

        System.out.println("unlock after 0ms");

        return true;
    }
}
