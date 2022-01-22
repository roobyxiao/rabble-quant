package whzz.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class Tick {
    private String code;

    private Date date;

    private List<TickData> data;
}