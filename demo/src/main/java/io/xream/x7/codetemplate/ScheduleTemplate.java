package io.xream.x7.codetemplate;

import java.util.concurrent.Callable;

public interface ScheduleTemplate {

    boolean schedule(Class scheduleClazz, Callable<Boolean> callable);
}
