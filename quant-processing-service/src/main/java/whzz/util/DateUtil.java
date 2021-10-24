package whzz.util;

import java.util.Calendar;
import java.sql.Date;

public class DateUtil {
    public static Date addDay(Date date, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return new Date(calendar.getTime().getTime());
    }

    public static Date minusYear(Date date, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -year);
        return new Date(calendar.getTime().getTime());
    }
}
