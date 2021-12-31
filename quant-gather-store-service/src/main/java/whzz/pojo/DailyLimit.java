package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import whzz.util.FastJsonSerializerUtil;
import java.sql.Date;

@Data
public class DailyLimit
{
    @JSONField(name = "ts_code", deserializeUsing = FastJsonSerializerUtil.TSCodeFormat.class)
    private String code;

    @JSONField(name = "trade_date", format = "yyyyMMdd")
    private Date date;

    @JSONField(name = "up_limit")
    private float limitUp;

    @JSONField(name = "down_limit")
    private float limitDown;
}
