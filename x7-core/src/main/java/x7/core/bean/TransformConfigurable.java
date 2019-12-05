package x7.core.bean;

public interface TransformConfigurable extends Transformed{

    String getOriginTable();
    String getTargetTable();
    String getOriginColumn();
    String getTargetColumn();
}
