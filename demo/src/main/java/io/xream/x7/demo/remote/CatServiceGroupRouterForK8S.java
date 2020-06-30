package io.xream.x7.demo.remote;

import io.xream.x7.common.bean.Routeable;
import io.xream.x7.common.util.StringUtil;
import io.xream.x7.common.util.VerifyUtil;
import io.xream.x7.demo.ro.CatRO;
import io.xream.x7.demo.bean.Cat;
import io.xream.x7.reyc.api.GroupRouter;

import java.util.Objects;

public class CatServiceGroupRouterForK8S implements GroupRouter {

    private final static int DIVIDOR = 4;

    @Override
    public String replaceHolder() {
        return "#key#";
    }

    @Override
    public String replaceValue(Object obj) {
        String value = doValue(obj);
        if (StringUtil.isNotNull(value))
            return "_"+value;
        return "";
    }

    private String doValue(Object obj) {
        if (obj == null)
            return null;
        if (obj instanceof Cat) {
            Cat cat = (Cat) obj;
            long mod = cat.getId() / DIVIDOR;
            return String.valueOf(mod);
        } else if (obj instanceof Long) {
            long mod = ((Long) obj) / DIVIDOR;
            return String.valueOf(mod);
        } else if (obj instanceof CatRO) {
            CatRO ro = (CatRO) obj;
            String key = ro.getCatFriendName();
            if (StringUtil.isNullOrEmpty(key))
                return null;
            String str = VerifyUtil.toMD5(ro.getCatFriendName());
            return str.substring(0, 2);
        } else if (obj instanceof Routeable) {
            Object keyObj = ((Routeable) obj).getRouteKey();
            if (Objects.isNull(keyObj))
                return null;
            if (keyObj instanceof String) {
                return String.valueOf(keyObj.hashCode() / DIVIDOR);
            } else if (keyObj instanceof Long) {
                return String.valueOf((Long) keyObj / DIVIDOR);
            }
        }
        return null   ;

    }
}
