package com.whzz.dao;

import com.whzz.pojo.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface TradeCalRepository extends JpaRepository<TradeCal, Date> {
    TradeCal findTopOrderByDateDesc();
}
