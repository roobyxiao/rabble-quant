package whzz.pojo;

import lombok.Data;

import java.sql.Date;
import java.sql.Time;

@Data
public class UpLimit
{
    private String code;

    private Date date;

    private Time firstTime;

    private Time endTime;

    private boolean open;

    private int last;

    private boolean keep;

    private boolean status;
}
