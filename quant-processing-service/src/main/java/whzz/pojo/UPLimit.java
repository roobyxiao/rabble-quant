package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UPLimit implements RowMapper<UPLimit>
{
    private String code;

    private Date date;

    private boolean keep;

    private int last;

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    public Date getDate ()
    {
        return date;
    }

    public void setDate (Date date)
    {
        this.date = date;
    }

    public boolean isKeep ()
    {
        return keep;
    }

    public void setKeep (boolean keep)
    {
        this.keep = keep;
    }

    public int getLast ()
    {
        return last;
    }

    public void setLast (int last)
    {
        this.last = last;
    }

    @Override
    public UPLimit mapRow (ResultSet resultSet,
                           int i) throws SQLException
    {
        UPLimit upLimit = new UPLimit();
        upLimit.setCode(resultSet.getString("code"));
        upLimit.setDate(resultSet.getDate("date"));
        upLimit.setLast(resultSet.getInt("last"));
        upLimit.setKeep(resultSet.getBoolean("keep"));
        return upLimit;
    }
}
