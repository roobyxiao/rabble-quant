package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import whzz.util.FastJsonSerializerUtil;
import java.sql.Date;
import java.sql.Time;

@Data
public class UpLimit
{
    @JSONField(name = "c", deserializeUsing = FastJsonSerializerUtil.CodeFormat.class)
    private String code;
    private Date date;
    private float zdp;
    @JSONField(name = "fbt", deserializeUsing = FastJsonSerializerUtil.TimeFormat.class)
    private Time firstTime;
    @JSONField(name = "lbt", deserializeUsing = FastJsonSerializerUtil.TimeFormat.class)
    private Time endTime;
    @JSONField(name = "zbc")
    private int open;
    @JSONField(name = "lbc")
    private int last;
}
