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
package x7.repository.internal;

import x7.core.bean.*;
import x7.core.bean.condition.InCondition;
import x7.core.repository.X;
import x7.core.util.ExceptionUtil;
import x7.repository.Repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DomainObjectRepositoy {


    private Repository repository;
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    protected  <T,WITH> List<DomainObject<T, WITH>> listDomainObject_Known_HasRelative(Criteria.DomainObjectCriteria domainObjectCriteria) {

        try {

            /*
             * knownMainIdList step 1
             */
            List<Object> mainInList = domainObjectCriteria.getKnownMainIdList();
            List<T> mainList = null;


            /*
             * step 2  if relativeClass
             */
            Parsed withParsed = Parser.get(domainObjectCriteria.getWithClz());
            Parsed relativeParsed = Parser.get(domainObjectCriteria.getRelativeClz());

            List relativeList = null;
            List withList = null;

            InCondition relativeInCondition = new InCondition(domainObjectCriteria.getMainPropperty(), mainInList);
            relativeInCondition.setClz(domainObjectCriteria.getRelativeClz());
            relativeList = repository.in(relativeInCondition);

            BeanElement relativeWithBe = relativeParsed.getElement(domainObjectCriteria.getWithProperty());

            List<Object> withInList = new ArrayList<>();
            for (Object r : relativeList) {
                Object in = relativeWithBe.getMethod.invoke(r);
                withInList.add(in);
            }

            String key = withParsed.getKey(X.KEY_ONE);

            InCondition withInCondition = new InCondition(key, withInList);
            withInCondition.setClz(domainObjectCriteria.getWithClz());
            withList = repository.in(withInCondition);


            List<DomainObject<T, WITH>> list = new ArrayList<>();



            /*
             * result assemble step3
             */
            BeanElement relatievMainBe = domainObjectCriteria.getRelativeClz() == null ? null :
                    relativeParsed.getElement(domainObjectCriteria.getMainPropperty());

            Field withKeyF = withParsed.getKeyField(X.KEY_ONE);
            withKeyF.setAccessible(true);

            BeanElement wBe = withParsed.getElement(domainObjectCriteria.getMainPropperty());// maybe null

            for (Object mainKeyOne : domainObjectCriteria.getKnownMainIdList()) {

                List withListOne = new ArrayList();


                for (Object r : relativeList) {
                    Object oRelative = relatievMainBe.getMethod.invoke(r);
                    if (mainKeyOne.toString().equals(oRelative.toString())) {
                        Object relativeWithKey = relativeWithBe.getMethod.invoke(r);

                        for (Object w : withList) {
                            Object withId = withKeyF.get(w);
                            if (relativeWithKey.toString().equals(withId.toString())) {
                                withListOne.add(w);
                            }
                        }

                    }
                }


                DomainObject domainObject = new DomainObject(domainObjectCriteria.getClz(),domainObjectCriteria.getWithClz());
                domainObject.setMainId(mainKeyOne);
                domainObject.reSetWithList(withListOne);

                list.add(domainObject);
            }


            return list;
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

    }

    protected  <T,WITH> List<DomainObject<T, WITH>> listDomainObject_Known_NonRelative(Criteria.DomainObjectCriteria domainObjectCriteria) {

        try {
            /*
             * knownMainIdList step 1
             */
            List<Object> mainInList = domainObjectCriteria.getKnownMainIdList();

            /*
             * step 2  if relativeClass
             */
            Parsed withParsed = Parser.get(domainObjectCriteria.getWithClz());


            InCondition withInCondition = new InCondition(domainObjectCriteria.getMainPropperty(), mainInList);
            withInCondition.setClz(domainObjectCriteria.getWithClz());
            List withList = repository.in(withInCondition);

            List<DomainObject<T, WITH>> list = new ArrayList<>();

            /*
             * result assemble step3
             */

            Field withKeyF = withParsed.getKeyField(X.KEY_ONE);
            withKeyF.setAccessible(true);

            BeanElement wBe = withParsed.getElement(domainObjectCriteria.getMainPropperty());// maybe null

            for (Object mainKeyOne : domainObjectCriteria.getKnownMainIdList()) {

                List withListOne = new ArrayList();

                for (Object w : withList) {
                    Object withR = wBe.getMethod.invoke(w);
                    if (mainKeyOne.toString().equals(withR.toString())) {
                        withListOne.add(w);
                    }
                }

                DomainObject domainObject = new DomainObject(domainObjectCriteria.getClz(),domainObjectCriteria.getWithClz());
                domainObject.setMainId(mainKeyOne);
                domainObject.reSetWithList(withListOne);

                list.add(domainObject);
            }


            return list;
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

    }


    protected  <T,WITH> List<DomainObject<T, WITH>> listDomainObject_HasRelative(Criteria.DomainObjectCriteria domainObjectCriteria) {

        try {

            /*
             * knownMainIdList step 1
             */
            List<Object> mainInList = new ArrayList<>();
            List<T> mainList = null;
            if (mainInList == null || mainInList.isEmpty()) {

                mainList = repository.list((Criteria) domainObjectCriteria);

                Parsed mainParsed = Parser.get(domainObjectCriteria.getClz());
                Field mainField = mainParsed.getKeyField(X.KEY_ONE);
                mainField.setAccessible(true);

                for (Object t : mainList) {
                    Object in = mainField.get(t);
                    mainInList.add(in);
                }
            }


            /*
             * step 2  if relativeClass
             */
            Parsed withParsed = Parser.get(domainObjectCriteria.getWithClz());
            Parsed relativeParsed = Parser.get(domainObjectCriteria.getRelativeClz());

            List relativeList = null;
            List withList = null;


            InCondition relativeInCondition = new InCondition(domainObjectCriteria.getMainPropperty(), mainInList);
            relativeInCondition.setClz(domainObjectCriteria.getRelativeClz());
            relativeList = repository.in(relativeInCondition);

            BeanElement relativeWithBe = relativeParsed.getElement(domainObjectCriteria.getWithProperty());

            List<Object> withInList = new ArrayList<>();
            for (Object r : relativeList) {
                Object in = relativeWithBe.getMethod.invoke(r);
                withInList.add(in);
            }

            String key = withParsed.getKey(X.KEY_ONE);

            InCondition withInCondition = new InCondition(key, withInList);
            withInCondition.setClz(domainObjectCriteria.getWithClz());
            withList = repository.in(withInCondition);

            List<DomainObject<T, WITH>> list = new ArrayList<>();


            /*
             * result assemble step3
             */
            BeanElement relatievMainBe = domainObjectCriteria.getRelativeClz() == null ? null :
                    relativeParsed.getElement(domainObjectCriteria.getMainPropperty());

            Field withKeyF = withParsed.getKeyField(X.KEY_ONE);
            withKeyF.setAccessible(true);


            Parsed mainParsed = Parser.get(domainObjectCriteria.getClz());
            Field mainField = mainParsed.getKeyField(X.KEY_ONE);
            mainField.setAccessible(true);

            BeanElement wBe = withParsed.getElement(domainObjectCriteria.getMainPropperty());

            for (Object main : mainList) {

                Object mainKeyOne = mainField.get(main);

                List withListOne = new ArrayList();


                for (Object r : relativeList) {
                    Object oRelative = relatievMainBe.getMethod.invoke(r);
                    if (mainKeyOne.toString().equals(oRelative.toString())) {
                        Object relativeWithKey = relativeWithBe.getMethod.invoke(r);

                        for (Object w : withList) {
                            Object withId = withKeyF.get(w);
                            if (relativeWithKey.toString().equals(withId.toString())) {
                                withListOne.add(w);
                            }
                        }

                    }
                }

                DomainObject domainObject = new DomainObject(domainObjectCriteria.getClz(),domainObjectCriteria.getWithClz());
                domainObject.reSetMain(main);
                domainObject.reSetWithList(withListOne);

                list.add(domainObject);
            }


            return list;
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

    }


    protected  <T,WITH> List<DomainObject<T, WITH>> listDomainObject_NonRelative(Criteria.DomainObjectCriteria domainObjectCriteria) {

        try {

            /*
             * knownMainIdList step 1
             */
            List<Object> mainInList = new ArrayList<>();
            List<T> mainList = null;

            Parsed mainParsed = Parser.get(domainObjectCriteria.getClz());
            Field mainField = mainParsed.getKeyField(X.KEY_ONE);
            mainField.setAccessible(true);

            if (mainInList == null || mainInList.isEmpty()) {

                mainList = repository.list((Criteria) domainObjectCriteria);


                for (Object t : mainList) {
                    Object in = mainField.get(t);
                    mainInList.add(in);
                }
            }


            /*
             * step 2  if relativeClass
             */
            Parsed withParsed = Parser.get(domainObjectCriteria.getWithClz());

            List withList = null;

            InCondition withInCondition = new InCondition(domainObjectCriteria.getMainPropperty(), mainInList);
            withInCondition.setClz(domainObjectCriteria.getWithClz());
            withList = repository.in(withInCondition);


            List<DomainObject<T, WITH>> list = new ArrayList<>();


            /*
             * result assemble step3
             */
//                BeanElement relatievMainBe = domainObjectCriteria.getRelativeClz() == null ? null :
//                        relativeParsed.getElement(domainObjectCriteria.getMainPropperty());


            BeanElement wBe = withParsed.getElement(domainObjectCriteria.getMainPropperty());

            for (Object main : mainList) {

                Object mainKeyOne = mainField.get(main);

                List withListOne = new ArrayList();

                for (Object w : withList) {
                    Object withR = wBe.getMethod.invoke(w);
                    if (mainKeyOne.toString().equals(withR.toString())) {
                        withListOne.add(w);
                    }
                }


                DomainObject domainObject = new DomainObject(domainObjectCriteria.getClz(),domainObjectCriteria.getWithClz());
                domainObject.reSetMain(main);
                domainObject.reSetWithList(withListOne);

                list.add(domainObject);
            }


            return list;
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtil.getMessage(e));
        }

    }


}
