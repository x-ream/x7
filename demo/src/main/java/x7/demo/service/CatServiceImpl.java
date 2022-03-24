package x7.demo.service;

import io.xream.rey.annotation.Fallback;
import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.sqli.core.RowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import x7.demo.entity.Cat;
import x7.demo.repository.CatRepository;

import java.util.List;
import java.util.Map;

@Fallback(ignoreExceptions = {RuntimeException.class}, fallback = CatServiceFallback.class)
@Service
public class CatServiceImpl implements CatService{

    @Autowired
    private CatRepository catRepository;

    @Override
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

    @Override
    public List<Map<String, Object>> listByResultMap(Criteria.ResultMapCriteria criteria) {
        return this.catRepository.list(criteria);
    }


}
