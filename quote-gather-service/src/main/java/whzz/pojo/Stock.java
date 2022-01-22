package whzz.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
@Data
public class Stock {
    private String code;
    @JSONField(name = "code_name")
    private String name;
    @JSONField(name = "ipoDate")
    private Date ipoDate;
    @JSONField(name = "outDate")
    private Date outDate;
    private int type;
    private boolean status;
}
