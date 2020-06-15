package io.xream.x7.demo.service;

import io.xream.x7.common.bean.condition.RefreshCondition;
import io.xream.x7.demo.CatRepository;
import io.xream.x7.demo.bean.Cat;
import io.xream.x7.fallback.FallbackOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatServiceImpl implements CatService{

    @Autowired
    private CatRepository catRepository;
    @Override
    @FallbackOnly(exceptions = {RuntimeException.class}, fallback = CatServiceFallback.class)
    public boolean refresh(RefreshCondition<Cat> refreshCondition) {
        if (true){
            throw new RuntimeException("test fall back");
        }
        return this.catRepository.refresh(refreshCondition);
    }
}
