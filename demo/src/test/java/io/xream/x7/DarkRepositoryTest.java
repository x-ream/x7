package io.xream.x7;

import io.xream.x7.demo.DarkRepository;
import io.xream.x7.demo.bean.Dark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DarkRepositoryTest {

    @Autowired
    private DarkRepository repository;

    public Dark get(){

        Dark dark = this.repository.get("xxxxxx");

        return dark;
    }
}
