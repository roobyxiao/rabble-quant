package com.whzz.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;


@Entity
@Data
@Table(name = "stock")
public class Stock {
    @Id
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
    @Column(name = "ipo_date")
    private Date ipoDate;
    @Column(name = "out_date")
    private Date outDate;
    @Column(name = "type")
    private int type;
    @Column(name = "status")
    private boolean status;

}
