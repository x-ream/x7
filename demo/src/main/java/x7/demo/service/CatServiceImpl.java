package x7.demo.service;

import io.xream.sqli.core.builder.Criteria;
import io.xream.sqli.core.builder.RowHandler;
import io.xream.sqli.core.builder.condition.RefreshCondition;
import x7.demo.CatRepository;
import x7.demo.bean.Cat;
import io.xream.x7.fallback.FallbackOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    @Override
    public void findToHandle(Criteria.ResultMappedCriteria resultMappedCriteria, RowHandler<Map<String, Object>> rowHandler) {
        this.catRepository.findToHandle(resultMappedCriteria,rowHandler);
    }

    @Override
    public void findToHandleC(Criteria criteria, RowHandler<Cat> rowHandler) {
        this.catRepository.findToHandle(criteria,rowHandler);
    }


}
