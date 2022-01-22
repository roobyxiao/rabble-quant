package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import whzz.util.FastJsonSerializerUtil;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TickData {
    @JSONField(deserializeUsing = FastJsonSerializerUtil.TimeFormat.class)
    private Time t;
    @JSONField(deserializeUsing = FastJsonSerializerUtil.ThousandFormat.class)
    private float p;
    private long v;
    private int bs;
}
