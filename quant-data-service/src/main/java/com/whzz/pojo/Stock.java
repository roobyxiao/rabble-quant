package com.whzz.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;


@Entity
@Data
@Table(name = "stock")
public class Stock {
    @Id
    private String code;

    private String name;

    private Date ipoDate;

    private Date outDate;

    private int type;

    private boolean status;
}
