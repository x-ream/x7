package io.xream.x7.repository;

import io.xream.x7.common.bean.condition.InCondition;

import java.util.List;

public interface QueryForCache {
    <T> List<T> in(InCondition inCondition);
}
