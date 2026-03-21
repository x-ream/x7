/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.xream.x7.lock;


import io.xream.internal.util.JsonX;
import io.xream.x7.base.exception.DistributionLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class DistributionLock {

    private static Logger LOGGER = LoggerFactory.getLogger(DistributionLock.class);

    private static int INTERVAL = 1000;
    protected static int TIMEOUT = 10 * 1000;

    private static LockProvider lockProvider;
    public static void init(LockProvider lp) {
        if (lp != null) {
            lockProvider = lp;
        }
    }


    private static Lock tryToLock(String key, String value, int interval, int timeout, boolean abortingIfNoLock) {

        if (lockProvider == null)
            throw new RuntimeException("No implements of LockProvider");

        int i = 1;
        boolean locked = lockProvider.lock(key,value,timeout);
        int retryMax = timeout / interval + 10;
        if (!abortingIfNoLock) {
            while (!locked) {
                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                    locked = lockProvider.lock(key, value, timeout);
                    i++;
                } catch (Exception e) {
                    break;
                }
                if (i >= retryMax) break;
            }
        }

        if (!locked) {
            LOGGER.info("Get distributed lock failed, lockKey: " + key);
            throw new DistributionLockException();
        }
        Lock lock = new Lock();
        lock.setKey(key);
        lock.setValue(value);
        lock.setExpireMs(timeout);
        lock.setIntervalMs(interval);
        return lock;
    }

    private static void unLock( Lock lock){
        lockProvider.unLock(lock);
    }


    public static Lock by(String key){
        Lock ml = new Lock();
        ml.setKey(key);
        ml.setValue(UUID.randomUUID()+"~LOCK");
        return ml;
    }

    public static class Lock {
        private String key;
        private String value;
        private long intervalMs;
        private long expireMs;

        private Lock() {
        }

        private void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public long getIntervalMs() {
            return intervalMs;
        }

        public void setIntervalMs(long intervalMs) {
            this.intervalMs = intervalMs;
        }

        public long getExpireMs() {
            return expireMs;
        }

        public void setExpireMs(long expireMs) {
            this.expireMs = expireMs;
        }

        public <T> T lock(Task<T> obj) {
            return lock(INTERVAL, TIMEOUT, false, obj);
        }

        public <T> T lock(
                int intervalMS,
                int timeoutMS,
                boolean abortingIfNoLock,
                Task<T> obj) {

            final Lock lock = DistributionLock.tryToLock(key, value, intervalMS, timeoutMS, abortingIfNoLock);
            T o = null;
            Timer timer = new Timer();
            try {

                long delay = lock.expireMs - lock.intervalMs;

                if (delay < lock.intervalMs) {
                    throw new DistributionLockException("expireMs should times than intervalMs");
                }

                if (delay > 1900) {
                    delay -= 100;
                }

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        LOGGER.info(lock.toString());
                        boolean delayed = lockProvider.delayExpire(lock);

                        if (!delayed) {
                            cancel();
                        }
                    }
                }, delay, delay);

                o = obj.run(obj);
            } catch (Exception e) {
                DistributionLock.unLock(lock);
                if (e instanceof RuntimeException re) {
                    throw re;
                } else {
                    throw new RuntimeException(e.getMessage());
                }
            } finally {
                DistributionLock.unLock(lock);
                if (timer != null) {
                    timer.cancel();
                }
            }
            return o;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName() + ": " + JsonX.toJson(this);
        }
    }

    public interface Task<T> {
        T run(Object obj);
    }

}
