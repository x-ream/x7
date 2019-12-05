package x7.repository.util;

import x7.core.bean.BeanElement;
import x7.core.bean.Criteria;
import x7.core.bean.KV;
import x7.core.bean.Parsed;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultSortUtil {

    /**
     * by orderIn0
     * @param list
     * @param criteria
     * @param parsed
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public  static <T> void sort(List<T> list, Criteria criteria, Parsed parsed) throws InvocationTargetException, IllegalAccessException {

        if (list.isEmpty())
            return;

        List<KV> fixedSortList = criteria.getFixedSortList();

        if (fixedSortList.isEmpty())
            return;

        KV kv0 = fixedSortList.get(0);

        List<T> tempList = new ArrayList<>();
        tempList.addAll(list);

        list.clear();

        Class clz = parsed.getClz();

        String property = kv0.k;

        for (Object para : (List<Object>)kv0.v){
            for (T result: tempList){
                BeanElement be = parsed.getElement(property);
                Object o = be.getMethod.invoke(result);
                if (String.valueOf(para).equals(String.valueOf(o))){
                    list.add(result);
                }
            }
        }

    }

    public static void sort(List<Map<String, Object>> list, Criteria.ResultMappedCriteria criteria) {

        if (list.isEmpty())
            return;

        List<KV> fixedSortList = criteria.getFixedSortList();

        if (fixedSortList.isEmpty())
            return;

        KV kv0 = fixedSortList.get(0);
        String property = kv0.k;

        Map<String,Object> test = list.get(0);
        if (!test.containsKey(property))
            return;

        List<Map<String, Object>> tempList = new ArrayList<>();
        tempList.addAll(list);

        list.clear();

        for (Object para : (List<Object>)kv0.v){
            for (Map<String,Object> mapResult: tempList){
                Object o = mapResult.get(property);
                if (String.valueOf(para).equals(String.valueOf(o))){
                    list.add(mapResult);
                }
            }
        }

    }
}
