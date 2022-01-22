package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.sql.Date;

@Data
public class Forward{
    private String code;
    private Date date;
    @JSONField(name = "f46")
    private float open;
    @JSONField(name = "f44")
    private float high;
    @JSONField(name = "f45")
    private float low;
    @JSONField(name = "f43")
    private float close;
    @JSONField(name = "f60")
    private float lastClose;
    @JSONField(name = "f47")
    private long volume;
    @JSONField(name = "f48")
    private float amount;
    @JSONField(name = "f168")
    private float turn;
    @JSONField(name = "f170")
    private float percent;
}
