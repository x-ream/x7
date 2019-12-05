package x7.core.util;

import com.alibaba.fastjson.JSON;
import x7.core.web.ViewEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by Sim on 2018/1/31.
 */
public class ViewEntityUtil {

    public static <T> T parseObject(ViewEntity bean, Class<T> clz) {
        T t = JSON.toJavaObject((JSON)bean.getBody(),clz);
        return t;
    }

    public static <T> List<T> parseList(ViewEntity bean, Class<T> clz) {
        JSON jsonObject = (JSON)bean.getBody();
        String text = jsonObject.toJSONString();
        List<T> list = JSON.parseArray(text, clz);
        return list;
    }

    public static Map<String,Object> parseMap(ViewEntity bean) {
        JSON jsonObject = (JSON)bean.getBody();
        return (Map)jsonObject;
    }

    public static boolean parseBoolean(ViewEntity bean) {
        return Boolean.valueOf(bean.getBody().toString());
    }

    public static long parseLong(ViewEntity bean) {
        return Long.valueOf(bean.getBody().toString());
    }
}
