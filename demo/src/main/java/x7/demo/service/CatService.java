package x7.demo.service;

import io.xream.sqli.builder.Criteria;
import io.xream.sqli.builder.RefreshCondition;
import io.xream.sqli.core.RowHandler;
import x7.demo.entity.Cat;

import java.util.List;
import java.util.Map;

public interface CatService {

    boolean refresh(RefreshCondition<Cat> refreshCondition);

    void findToHandle(Criteria.ResultMapCriteria ResultMapCriteria, RowHandler<Map<String,Object>> rowHandler);

    void findToHandleC(Criteria criteria, RowHandler<Cat> rowHandler);

    List<Map<String, Object>> listByResultMap(Criteria.ResultMapCriteria criteria);
}
