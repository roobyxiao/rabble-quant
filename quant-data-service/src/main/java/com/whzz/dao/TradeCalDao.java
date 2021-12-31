package com.whzz.dao;

import com.whzz.pojo.TradeCal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface TradeCalDao extends JpaRepository<TradeCal, Date> {
    TradeCal findTopByOrderByDateDesc();

    List<TradeCal> findAllByDateBetweenAndOpenIsTrue(Date startDate, Date endDate);

    TradeCal findTradeCalByDate(Date date);
}
