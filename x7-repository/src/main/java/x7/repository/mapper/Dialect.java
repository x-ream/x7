package x7.repository.mapper;


import x7.core.bean.BeanElement;
import x7.core.bean.Criteria;

import java.util.Collection;
import java.util.Map;

public interface Dialect {

    String DATE = " ${DATE}";
    String BYTE = " ${BYTE}";
    String INT = " ${INT}";
    String LONG = " ${LONG}";
    String BIG = " ${BIG}";
    String STRING = " ${STRING}";
    String TEXT = " ${TEXT}";
    String LONG_TEXT = " ${LONG_TEXT}";
    String INCREAMENT = " ${INCREAMENT}";
    String ENGINE = " ${ENGINE}";


    String match(String sql, long start, long rows);

    String match(String sql, String sqlType);

    String transformAlia(String mapper, Map<String,String> aliaMap, Map<String,String> resultAliaMap) ;

    Object filterValue(Object value);

    String resultKeyAlian(String mapper, Criteria.ResultMappedCriteria criteria);

    Object[] toArr(Collection<Object> list);

    Object mappingToObject( Object obj, BeanElement element);

}