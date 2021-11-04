package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class TradeCal implements RowMapper<TradeCal> {

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public TradeCal mapRow(ResultSet resultSet, int i) throws SQLException {
        TradeCal tradeCal = new TradeCal();
        tradeCal.setDate(resultSet.getDate("date"));
        return tradeCal;
    }
}
