package whzz.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Daily {

    private String code;
    private Date date;
    private float open;
    private float high;
    private float low;
    private float close;
    private float lastClose;
    private long volume;
    private float percent;
    private float highLimit;
    private float lowLimit;
}
