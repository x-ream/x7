package x7.core.util;

public class ExceptionUtil {

    public static String getMessage(Exception e){
        String msg = e.getMessage();
        msg += "\n";
        StackTraceElement[] eleArr = e.getStackTrace();
        msg += eleArr[0].toString();
        msg += "\n";
        int length = eleArr.length;
        if (eleArr != null && length > 0){
            if (length > 2){
                msg += eleArr[1].toString();
                msg += "\n";
                msg += eleArr[2].toString();
            }else if (length > 1){
                msg += eleArr[1].toString();
            }
        }

        return msg;
    }

    public static String getMessage(Throwable e){
        String msg = e.getMessage();
        msg += "\n";
        StackTraceElement[] eleArr = e.getStackTrace();
        msg += eleArr[0].toString();
        msg += "\n";
        int length = eleArr.length;
        if (eleArr != null && length > 0){
            if (length > 2){
                msg += eleArr[1].toString();
                msg += "\n";
                msg += eleArr[2].toString();
            }else if (length > 1){
                msg += eleArr[1].toString();
            }
        }

        return msg;
    }
}
