package whzz.pojo;

import java.util.Date;

public class Forward{
    private Date date;
    private String code;
    private float preClose;
    private float open;
    private float high;
    private float low;
    private float close;
    private long volume;
    private long adjustVolume;

    public Forward() {
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

    public long getAdjustVolume() {
        return adjustVolume;
    }

    public void setAdjustVolume(long adjustVolume) {
        this.adjustVolume = adjustVolume;
    }
}
