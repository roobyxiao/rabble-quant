package com.whzz.dao;

import com.whzz.pojo.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDao extends JpaRepository<Stock, String> {
    public List<Stock> findAllByStatus(boolean status);
}
