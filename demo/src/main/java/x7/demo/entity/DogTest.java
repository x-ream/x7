package x7.demo.entity;

import io.xream.sqli.annotation.X;

import java.math.BigDecimal;

public class DogTest  {

    @X.Key
    private long id;

    private BigDecimal number;

    private long petId;

    private String userName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public long getPetId() {
        return petId;
    }

    public void setPetId(long petId) {
        this.petId = petId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
