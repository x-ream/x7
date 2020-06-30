package io.xream.x7.repository.id;


public interface IdGeneratorService {
    void setIdGeneratorPolicy(IdGeneratorPolicy policy);
    long createId(String clzName);
    IdGeneratorPolicy getIdGeneratorPolicy();
}