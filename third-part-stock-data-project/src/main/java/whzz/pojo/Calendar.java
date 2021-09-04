package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Calendar implements RowMapper<Calendar> {

    private Date date;

    private int open;

    public Calendar(){

    }

    public Calendar(Date date, int open){
        this.date = date;
        this.open = open;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    @Override
    public Calendar mapRow(ResultSet resultSet, int i) throws SQLException {
        Calendar calendar = new Calendar();
        calendar.setDate(resultSet.getDate("calendar_date"));
        calendar.setOpen(resultSet.getInt("is_trading_day"));
        return calendar;
    }
}
