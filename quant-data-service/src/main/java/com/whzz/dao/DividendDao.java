package com.whzz.dao;

import com.whzz.pojo.Dividend;
import com.whzz.pojo.DividendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DividendDao extends JpaRepository<Dividend, DividendId>
{
}
