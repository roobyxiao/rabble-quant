package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import whzz.util.FastJsonSerializerUtil;

import java.sql.Date;

@Data
public class XqDaily {
    @JSONField(name = "symbol", deserializeUsing = FastJsonSerializerUtil.XqSymbolFormat.class)
    private String code;
    @JSONField(name = "time", deserializeUsing = FastJsonSerializerUtil.DateFormat.class)
    private Date date;
    private float lastClose;
    private float open;
    private float high;
    private float low;
    @JSONField(name = "current")
    private float close;
    private long volume;
    private float amount;
    @JSONField(name = "turnover_rate")
    private float turn;
    private float percent;
    private int status;
    private float limitUp;
    private float limitDown;
}
