package com.whzz.dao;

import com.whzz.pojo.Dividend;
import com.whzz.pojo.DividendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DividendDao extends JpaRepository<Dividend, DividendId>
{
    @Query(value = "SELECT DISTINCT code FROM Dividend", nativeQuery = true)
    List<String> findDistinctCodes();

    List<Dividend> findByCodeOrderByDividendDateDesc(String code);
}
