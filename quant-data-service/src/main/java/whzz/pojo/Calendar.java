package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Calendar implements RowMapper<Calendar> {

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public Calendar mapRow(ResultSet resultSet, int i) throws SQLException {
        Calendar calendar = new Calendar();
        calendar.setDate(resultSet.getDate("calendar_date"));
        return calendar;
    }
}
