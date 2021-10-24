package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Stock implements RowMapper<Stock> {
    private String code;

    public Stock() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Stock mapRow(ResultSet resultSet, int i) throws SQLException {
        Stock stock = new Stock();
        stock.setCode(resultSet.getString("code"));
        return stock;
    }
}
