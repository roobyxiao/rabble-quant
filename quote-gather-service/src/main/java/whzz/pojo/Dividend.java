package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import whzz.util.FastJsonSerializerUtil;

import java.math.BigDecimal;
import java.sql.Date;

@Data
public class Dividend {
    @JSONField(name = "SECUCODE", deserializeUsing = FastJsonSerializerUtil.TSCodeFormat.class)
    private String code;
    @JSONField(name = "PLAN_NOTICE_DATE")
    private Date planDate;
    @JSONField(name = "EX_DIVIDEND_DATE")
    private Date dividendDate;
    @JSONField(name = "BONUS_IT_RATIO")
    private BigDecimal ratio;
}
