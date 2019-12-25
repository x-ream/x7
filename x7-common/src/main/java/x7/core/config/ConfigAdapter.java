package x7.core.config;

public class ConfigAdapter {

    private static boolean isShowSql = false;

    public static boolean isIsShowSql() {
        return isShowSql;
    }

    public static void setIsShowSql(boolean isShowSql) {
        ConfigAdapter.isShowSql = isShowSql;
    }
}
