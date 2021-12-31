package whzz.pojo;

import lombok.Data;

import java.sql.Date;

@Data
public class Stock {
    private String code;

    private String name;

    private Date ipoDate;

    private Date outDate;
}
