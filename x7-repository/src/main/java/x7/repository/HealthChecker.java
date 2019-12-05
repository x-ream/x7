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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x7.core.bean.Parsed;
import x7.core.bean.Parser;
import x7.core.repository.X;
import x7.core.util.StringUtil;
import x7.repository.mapper.Mapper;
import x7.repository.mapper.MapperFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HealthChecker {

    private final static Logger logger = LoggerFactory.getLogger(HealthChecker.class);

    private static List<BaseRepository> repositoryList = new ArrayList<BaseRepository>();


    public static List<BaseRepository> getRepositoryList(){
        return repositoryList;
    }

    public static void onStarted() {

        for (BaseRepository repository : repositoryList) {
            logger.info("Parsing " + repository.getClz());
            Parser.get(repository.getClz());
        }


        String sql = "CREATE TABLE IF NOT EXISTS `idGenerator` ( "
                + "`clzName` varchar(120) NOT NULL, "
                + "`maxId` bigint(13) DEFAULT NULL, "
                + "PRIMARY KEY (`clzName`) "
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 ";

        try {
            ManuRepository.execute(new IdGenerator(), sql);
        } catch (Exception e) {

        }

        System.out.println("-------------------------------------------------");

        boolean flag = false;

        for (BaseRepository repository : repositoryList) {

            try {
                Class clz = repository.getClz();
                String createSql = MapperFactory.tryToCreate(clz);
                String test = MapperFactory.getSql(clz, Mapper.CREATE);
                if (StringUtil.isNullOrEmpty(test)) {
                    logger.info("FAILED TO START X7-REPOSITORY, check Bean: " + clz);
                    System.exit(1);
                }

                if (DbType.value.equals(DbType.MYSQL)) {
                    if (StringUtil.isNotNull(createSql)) {
                        ManuRepository.execute(clz.newInstance(), createSql);
                    }
                }

                Parsed clzParsed = Parser.get(clz);
                Field f = clzParsed.getKeyField(X.KEY_ONE);
                if (f.getType() == String.class)
                    continue;
                final String name = clz.getName();
                IdGenerator generator = new IdGenerator();
                generator.setClzName(name);
                List<IdGenerator> list = repository.list(generator);
                if (list.isEmpty()) {
                    logger.info("id init: " + generator.getClzName());
                    generator.setMaxId(0);
                    repository.create(generator);
                }

            } catch (Exception e) {
                flag |= true;
//					e.printStackTrace();
            }
        }

        logger.info("X7 Repository " + (flag ? "still " : "") + "started" + (flag ? " OK, wtih some problem" : ""));

    }
}