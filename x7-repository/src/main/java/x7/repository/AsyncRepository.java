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
package x7.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import x7.repository.dao.AsyncDao;


@Repository
public class AsyncRepository {

    @Autowired
    private AsyncDao asyncDao;

    public void create(Object obj) {
        this.asyncDao.create(obj);
    }

    public void refresh(Object obj) {
        this.asyncDao.refresh(obj);
    }

    public void remove(Object obj) {
        this.asyncDao.remove(obj);
    }

}
