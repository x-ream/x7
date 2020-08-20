package io.xream.x7.repository.id;


import io.xream.sqli.api.IdGenerator;

public interface IdGeneratorService extends IdGenerator {
    void setIdGeneratorPolicy(IdGeneratorPolicy policy);
    IdGeneratorPolicy getIdGeneratorPolicy();
}