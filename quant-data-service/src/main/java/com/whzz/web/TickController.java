package com.whzz.web;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import com.whzz.pojo.Tick;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.sql.Date;

@RestController
@RequestMapping(value = "/tick")
public class TickController {

    private String path = System.getProperty("user.dir") + "/data";

    @RequestMapping(value = "/save")
    public String saveTick(@RequestBody Tick tick)
    {
        Date date = tick.getDate();
        String[] dates = date.toString().split("-");
        File yearPath = new File(path + "/" + dates[0]);
        File monthPath = new File(path + "/" + dates[0] + "/" + dates[1]);
        File dayPath = new File(path + "/" + dates[0] + "/" + dates[1] + "/" + dates[2]);
        if (!yearPath.exists() && !yearPath.isDirectory())
            yearPath.mkdir();
        if (!monthPath.exists() && !monthPath.isDirectory())
            monthPath.mkdir();
        if (!dayPath.exists() && !dayPath.isDirectory())
            dayPath.mkdir();
        File file = new File(dayPath.getAbsolutePath() + "/" + tick.getCode());
        CsvWriter writer = null;
        try {
            writer =CsvUtil.getWriter(dayPath.getAbsoluteFile() + "/" + tick.getCode() + ".csv",
                    CharsetUtil.CHARSET_ISO_8859_1);
            writer.writeBeans(tick.getData());
        }  catch (IORuntimeException e) {
            e.printStackTrace();
        } finally {
            assert writer != null;
            writer.close();
        }


        return "tick saved: " + DateUtil.now();
    }
}
