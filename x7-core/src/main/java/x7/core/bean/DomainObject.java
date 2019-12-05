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
package x7.core.bean;


import com.alibaba.fastjson.JSONObject;
import x7.core.util.JsonX;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DomainObject<T,WITH> implements Serializable {

    private static final long serialVersionUID = -1601773516153576783L;

    private Class clzT;
    private Class clzWith;

    private Object mainId;
    private T main;
    private List<WITH> withList;

    public DomainObject(){}
    public DomainObject(Class<T> clzT, Class<WITH> clzWith){
        this.clzT = clzT;
        this.clzWith = clzWith;
    }

    public Class getClzT() {
        return clzT;
    }

    public Class getClzWith() {
        return clzWith;
    }

    public Object getMainId() {
        return mainId;
    }

    public void setMainId(Object mainId) {
        this.mainId = mainId;
    }

    public T getMain() {
        return main;
    }

    @Deprecated
    public void setMain(T main) {
        if (main == null)
            return;
        if (this.clzT == null) {
            this.main = main;
        }else{
            if (main instanceof JSONObject){
                this.main = (T)JsonX.toObject(main,this.clzT);
            }else{
                this.main = main;
            }
        }
    }

    public List<WITH> getWithList() {
        return withList;
    }

    @Deprecated
    public void setWithList(List<WITH> withList) {
        if (withList == null || withList.isEmpty())
            return;
        if (this.clzWith == null){
            this.withList = withList;
        }else {
            List<WITH> list = new ArrayList<>();
            for (Object obj : withList){
                if (obj instanceof JSONObject){
                    WITH with = (WITH)JsonX.toObject(obj, this.clzWith);
                    list.add(with);
                }else{
                    list.add((WITH)obj);
                }
            }
            this.withList = list;
        }
    }

    public void reSetMain(T main) {
        this.main = main;
    }

    public void reSetWithList(List<WITH> withList) {
        this.withList = withList;
    }

    public void setClzT(Class clzT) {
        this.clzT = clzT;
        if (this.main == null )
            return;
        if (this.main instanceof JSONObject){
            this.main = (T)JsonX.toObject(this.main,this.clzT);
        }
    }

    public void setClzWith(Class clzWith) {
        this.clzWith = clzWith;
        if (this.withList == null || this.withList.isEmpty())
            return;
        List list = new ArrayList<>();
        list.addAll(this.withList);
        this.withList.clear();
        setWithList(list);
    }

    @Override
    public String toString() {
        return "DomainObject{" +
                "mainId=" + mainId +
                ", main=" + main +
                ", withList=" + withList +
                '}';
    }
}
