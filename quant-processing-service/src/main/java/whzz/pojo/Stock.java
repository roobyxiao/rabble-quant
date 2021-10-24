package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Stock implements RowMapper<Stock> {
    private String code;
    private String name;
    private Date ipoDate;
    private Date outDate;

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

    public Date getIpoDate() {
        return ipoDate;
    }

    public void setIpoDate(Date ipoDate) {
        this.ipoDate = ipoDate;
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    @Override
    public Stock mapRow(ResultSet resultSet, int i) throws SQLException {
        Stock stock = new Stock();
        stock.setCode(resultSet.getString("code"));
        stock.setName(resultSet.getString("name"));
        stock.setIpoDate(resultSet.getDate("ipo_date"));
        stock.setOutDate(resultSet.getDate("out_date"));
        return stock;
    }
}
