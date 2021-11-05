package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.sql.Date;

public class TradeCal {
    @JSONField(name = "calendar_date")
    private Date date;
    @JSONField(name = "is_trading_day")
    private boolean open;
}
