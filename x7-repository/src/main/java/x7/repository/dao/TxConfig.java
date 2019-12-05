package x7.repository.dao;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class TxConfig {

    public TxConfig(DataSourceTransactionManager dstm) {
        Tx.init(dstm);
    }
}
