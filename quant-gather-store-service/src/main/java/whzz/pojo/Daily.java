package whzz.pojo;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Daily implements RowMapper<Daily> {
    private Date date;
    private String code;
    private float preClose;
    private float open;
    private float high;
    private float low;
    private float close;
    private long volume;
    private boolean isST;
    private float highLimit;
    private float lowLimit;

    public Daily() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getPreClose() {
        return preClose;
    }

    public void setPreClose(float preClose) {
        this.preClose = preClose;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public boolean isST() {
        return isST;
    }

    public void setST(boolean ST) {
        isST = ST;
    }

    public float getHighLimit() {
        return highLimit;
    }

    public void setHighLimit(float highLimit) {
        this.highLimit = highLimit;
    }

    public float getLowLimit() {
        return lowLimit;
    }

    public void setLowLimit(float lowLimit) {
        this.lowLimit = lowLimit;
    }

    @Override
    public Daily mapRow(ResultSet resultSet, int i) throws SQLException {
        Daily daily = new Daily();
        daily.setCode(resultSet.getString("code"));
        daily.setDate(resultSet.getDate("date"));
        daily.setPreClose(resultSet.getFloat("preclose"));
        daily.setOpen(resultSet.getFloat("open"));
        daily.setHigh(resultSet.getFloat("high"));
        daily.setLow(resultSet.getFloat("low"));
        daily.setClose(resultSet.getFloat("close"));
        daily.setST(resultSet.getBoolean("isST"));
        daily.setVolume(resultSet.getLong("volume"));
        daily.setHighLimit(resultSet.getFloat("high_limit"));
        daily.setLowLimit(resultSet.getFloat("low_limit"));
        return daily;
    }
}
