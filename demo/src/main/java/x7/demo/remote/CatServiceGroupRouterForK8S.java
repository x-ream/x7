package x7.demo.remote;

import io.xream.internal.util.StringUtil;
import io.xream.internal.util.VerifyUtil;
import io.xream.rey.api.GroupRouter;
import io.xream.sqli.api.Routable;
import x7.demo.entity.Cat;
import x7.demo.ro.CatFindRo;

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
        } else if (obj instanceof CatFindRo) {
            CatFindRo ro = (CatFindRo) obj;
            String key = ro.getCatFriendName();
            if (StringUtil.isNullOrEmpty(key))
                return null;
            String str = VerifyUtil.toMD5(ro.getCatFriendName());
            return str.substring(0, 2);
        } else if (obj instanceof Routable) {
            Object keyObj = ((Routable) obj).getRouteKey();
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
