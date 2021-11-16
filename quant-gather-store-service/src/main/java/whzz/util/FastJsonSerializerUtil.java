package whzz.util;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.Type;
import java.sql.Time;

public class FastJsonSerializerUtil
{
    public static class TSCodeFormat implements ObjectDeserializer
    {
        @Override
        public String deserialze (DefaultJSONParser defaultJSONParser,
                                 Type type,
                                 Object o)
        {
            String code = defaultJSONParser.getLexer().stringVal();
            String[] codes = code.split("\\.");
            code = codes[1].toLowerCase() + "." + codes[0];
            return code;
        }

        @Override
        public int getFastMatchToken ()
        {
            return 0;
        }
    }

    public static class CodeFormat implements ObjectDeserializer
    {
        @Override
        public String deserialze (DefaultJSONParser defaultJSONParser,
                                 Type type,
                                 Object o)
        {
            String code = defaultJSONParser.getLexer().stringVal();
            if (code.charAt(0) == '6')
                code = "sh." + code;
            else
                code = "sz." + code;
            return code;
        }

        @Override
        public int getFastMatchToken ()
        {
            return 0;
        }
    }

    public static class TimeFormat implements ObjectDeserializer
    {
        @Override
        public Time deserialze (DefaultJSONParser defaultJSONParser,
                                 Type type,
                                 Object o)
        {
            Integer time = defaultJSONParser.getLexer().integerValue().intValue();
            String value = time.toString();
            if (value.length() == 5)
                value = "0" + value;
            value = value.replaceAll("(.{2})", ":$1").substring(1);
            return Time.valueOf(value);
        }

        @Override
        public int getFastMatchToken ()
        {
            return 0;
        }
    }

    public static class LongFormat implements ObjectDeserializer
    {
        @Override
        public Long deserialze (DefaultJSONParser defaultJSONParser,
                                 Type type,
                                 Object o)
        {
            String val = defaultJSONParser.getLexer().stringVal();
            long value = 0;
            if (!val.isEmpty()) {
                Float floatValue = Float.parseFloat(val);
                value = floatValue.longValue();
            }
            return value;
        }

        @Override
        public int getFastMatchToken ()
        {
            return 0;
        }
    }
}
