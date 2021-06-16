package x7.config;

import io.xream.sqli.support.EnumSupport;

/**
 * @Author Sim
 */
public class MyEnumSupport implements EnumSupport {
    @Override
    public Object serialize(Enum obj) {
        if (obj instanceof EnumCodeable) {
            EnumCodeable enumCodeable = (EnumCodeable) obj;
            return enumCodeable.getCode();
        }
        return obj.name();
    }

    @Override
    public Enum deserialize(Class<Enum> clzz, Object obj) {

        for (Enum ee : clzz.getEnumConstants()) {
            if (ee instanceof EnumCodeable) {
                EnumCodeable ec = (EnumCodeable) ee;
                if (ec.getCode().equals(obj.toString()))
                    return ee;
            }
        }

        return Enum.valueOf(clzz, obj.toString());
    }
}
