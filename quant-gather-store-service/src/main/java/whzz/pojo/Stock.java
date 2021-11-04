package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Stock implements RowMapper<Stock> {
    private String code;

    private Date ipoDate;

    public Stock() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getIpoDate() {
        return ipoDate;
    }

    public void setIpoDate(Date ipoDate) {
        this.ipoDate = ipoDate;
    }

    @Override
    public Stock mapRow(ResultSet resultSet, int i) throws SQLException {
        Stock stock = new Stock();
        stock.setCode(resultSet.getString("code"));
        stock.setIpoDate(resultSet.getDate("ipo_date"));
        return stock;
    }
}
