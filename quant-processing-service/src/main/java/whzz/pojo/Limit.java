package whzz.pojo;

import java.sql.Date;

public class Limit {
    private String code;

    private Date date;

    private int upDown;

    private boolean keep;

    private int days;

    public Limit(String code, Date date, int upDown, boolean keep, int days) {
        this.code = code;
        this.date = date;
        this.upDown = upDown;
        this.keep = keep;
        this.days = days;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUpDown() {
        return upDown;
    }

    public void setUpDown(int upDown) {
        this.upDown = upDown;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
