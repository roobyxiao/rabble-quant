package whzz.pojo;


import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock implements RowMapper<Stock> {
    private String code;
    private String name;

    public Stock() {

    }

    public Stock(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Stock mapRow(ResultSet resultSet, int i) throws SQLException {
        Stock stock = new Stock();
        stock.setCode(resultSet.getString("symbol"));
        stock.setName(resultSet.getString("name"));
        return stock;
    }
}
