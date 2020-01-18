package io.xream.x7.repository.id;

import io.xream.x7.common.repository.X;

@X.NoCache
public class IdGenerator {

    private static final long serialVersionUID = -4482390783954339652L;

    @X.Key
    private String clzName;
    private long maxId;

    public String getClzName() {
        return clzName;
    }

    public void setClzName(String clzName) {
        this.clzName = clzName;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    @Override
    public String toString() {
        return "IdGenerator{" +
                "clzName='" + clzName + '\'' +
                ", maxId=" + maxId +
                '}';
    }
}
