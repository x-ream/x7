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


import io.xream.x7.exception.DistributionLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class DistributionLock {

    private static Logger logger = LoggerFactory.getLogger(DistributionLock.class);

    private static int INTERVAL = 1000;
    protected static int TIMEOUT = 10 * 1000;

    private static LockProvider lockProvider;
    public static void init(LockProvider lp) {
        lockProvider = lp;
    }

    private static void lock(String key, int interval, int timeout, boolean abortingIfNoLock) {

        if (lockProvider == null)
            throw new RuntimeException("No implements of LockProvider, like the project x7-repo/x7-redis-integration");

        int i = 1;
        boolean locked = lockProvider.lock(key,timeout);
        int retryMax = timeout / interval ;
        if (!abortingIfNoLock) {
            while (!locked) {
                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                    locked = lockProvider.lock(key, timeout);
                    i++;
                } catch (Exception e) {
                    break;
                }
                if (i >= retryMax) break;
            }
        }

        if (!locked) {
            logger.info("Get distributed lock failed, lockKey: " + key);
            throw new DistributionLockException();
        }
    }

    private static void unLock( Lock lock){
        lockProvider.unLock(lock);
    }

    private static void unLockAsync( String key){
    }

    public static Lock by(String key){
        Lock ml = new Lock();
        ml.setKey(key+"~LOCK");
        return ml;
    }

    public static class Lock{
        private  String key;

        private Lock(){}

        private void setKey(String key){
            this.key = key;
        }

        public String getKey(){
            return this.key;
        }

        public <T> T lock(Task<T> obj){
            return lock(INTERVAL,TIMEOUT,false,obj);
        }

        public <T> T lock(
                int intervalMS,
                int timeoutMS,
                boolean abortingIfNoLock,
                Task<T> obj){

            DistributionLock.lock(key,intervalMS,timeoutMS,abortingIfNoLock);
            T o = null;
            try {
                o = obj.run(obj);
            }catch (Exception e) {
                DistributionLock.unLock(this);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }else {
                    throw new RuntimeException(e.getMessage());
                }
            }finally {
                DistributionLock.unLock(this);
            }
            return o;
        }

        public <T> T lockAsync(Task<T> obj){
            DistributionLock.lock(key,INTERVAL,TIMEOUT,false);
            T o = null;
            try {
                o = obj.run(obj);
            }catch (Exception e) {
                DistributionLock.unLock(this);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }else {
                    throw new RuntimeException(e.getMessage());
                }
            }finally {
                DistributionLock.unLockAsync(key);
            }
            return o;
        }
    }



    public interface Task<T> {
        T run(Object obj);
    }

}
