package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import whzz.util.FastJsonSerializerUtil;

import java.util.Date;

@Data
public class Daily {
    private Date date;
    private String code;
    private float preClose;
    private float open;
    private float high;
    private float low;
    private float close;
    private long volume;
    @JSONField(deserializeUsing = FastJsonSerializerUtil.LongFormat.class)
    private long amount;
    private float turn;
    @JSONField(name = "tradestatus")
    private boolean tradeStatus;
    private float pctChg;
    private boolean isST;
}
