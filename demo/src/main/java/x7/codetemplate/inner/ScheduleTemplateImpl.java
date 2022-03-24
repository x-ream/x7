package x7.codetemplate.inner;

import io.xream.internal.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x7.codetemplate.ScheduleTemplate;

import java.util.Date;
import java.util.concurrent.Callable;


public class ScheduleTemplateImpl implements ScheduleTemplate {

    public boolean schedule(Class scheduleClazz, Callable<Boolean> callable) {

        Logger logger = LoggerFactory.getLogger(scheduleClazz);

        String loggingName = scheduleClazz.getSimpleName();

        boolean flag = false;
        long startTime = System.currentTimeMillis();
        String taskId = loggingName + "_" + startTime;

        logger.info("Executing " + loggingName + " At: " + new Date() + ", id: " + taskId);

        try {
            flag = callable.call();
        }catch (Exception e) {
            logger.info("Exception Occured: " + ExceptionUtil.getMessage(e));
        }

        long endTime = System.currentTimeMillis();

        String result = flag == true?"OK," :  "FAILED,";
        logger.info("Executing " + loggingName +", " + result +" Cost: " + (endTime - startTime) + "ms" + ", id: " + taskId);

        return flag;
    }
}
