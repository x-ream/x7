package io.xream.x7.common.bean;

public interface TransformConfigurable extends Transformed{

    String getOriginTable();
    String getTargetTable();
    String getOriginColumn();
    String getTargetColumn();
}
