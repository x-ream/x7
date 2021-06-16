package x7.demo.service;

import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.sqli.core.RowHandler;
import io.xream.x7.fallback.FallbackOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import x7.demo.entity.Cat;
import x7.demo.repository.CatRepository;

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
    public void findToHandle(Criteria.ResultMapCriteria ResultMapCriteria, RowHandler<Map<String, Object>> rowHandler) {
        this.catRepository.findToHandle(ResultMapCriteria,rowHandler);
    }

    @Override
    public void findToHandleC(Criteria criteria, RowHandler<Cat> rowHandler) {
        this.catRepository.findToHandle(criteria,rowHandler);
    }


}
