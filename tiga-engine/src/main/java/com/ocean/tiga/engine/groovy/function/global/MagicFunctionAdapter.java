package com.ocean.tiga.engine.groovy.function.global;

import org.ssssssss.script.functions.DateExtension;
import org.ssssssss.script.functions.NumberExtension;
import org.ssssssss.script.functions.ObjectConvertExtension;
import org.ssssssss.script.functions.StreamExtension;
import org.ssssssss.script.parsing.ast.BinaryOperation;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 全局函数适配器
 *
 * @author Tiga Platform Team
 */
public class MagicFunctionAdapter {

    /**
     * 创建`int`数组
     * @param size 数组大小
     * @return int[]
     */
    public static int[] new_int_array(int size) {
        return new int[size];
    }

    /**
     * 创建`short`数组
     * @param size 数组大小
     * @return short[]
     */
    public static short[] new_short_array(int size) {
        return new short[size];
    }

    /**
     * 创建`double`数组
     * @param size 数组大小
     * @return double[]
     */
    public static double[] new_double_array(int size) {
        return new double[size];
    }

    /**
     * 创建`float`数组
     * @param size 数组大小
     * @return float[]
     */
    public static float[] new_float_array(int size) {
        return new float[size];
    }

    /**
     * 创建`byte`数组
     * @param size 数组大小
     * @return byte[]
     */
    public static byte[] new_byte_array(int size) {
        return new byte[size];
    }

    /**
     * 创建`char`数组
     * @param size 数组大小
     * @return char[]
     */
    public static char[] new_char_array(int size) {
        return new char[size];
    }

    /**
     * 创建`boolean`数组
     * @param size 数组大小
     * @return boolean[]
     */
    public static boolean[] new_boolean_array(int size) {
        return new boolean[size];
    }

    /**
     * 创建`long`数组
     * @param size 数组大小
     * @return long[]
     */
    public static long[] new_long_array(int size) {
        return new long[size];
    }

    /**
     * 创建`Object`数组
     * @param size 数组大小
     * @return Object[]
     */
    public static Object[] new_array(int size) {
        return new Object[size];
    }

    /**
     * 创建指定类型的数组
     * @param componentType 数组类型
     * @param size 数组大小
     * @return T[]
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] new_array(Class<T> componentType, int size) {
        return (T[]) Array.newInstance(componentType, size);
    }

    /**
     * 创建`String`数组
     * @param array 字符串
     * @return String[]
     */
    public static String[] new_array(String... array) {
        return array;
    }

    /**
     * 区间迭代器
     * @param from 起始编号
     * @param to 结束编号
     * @return Iterator<Integer>
     */
    public static Iterator<Integer> range(int from, int to) {
        return new Iterator<Integer>() {
            int idx = from;

            @Override
            public boolean hasNext() {
                return idx <= to;
            }

            @Override
            public Integer next() {
                return idx++;
            }
        };
    }

    /**
     * 生成uuid字符串，不包含`-`
     * @return String
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 判断对象是否不是`NULL`
     * @param value 目标对象
     * @return boolean
     */
    public static boolean not_null(Object value) {
        return value != null;
    }

    /**
     * 判断对象是否是`NULL`
     * @param value 目标对象
     * @return boolean
     */
    public static boolean is_null(Object value) {
        return value == null;
    }

    /**
     * 换行打印
     * @param target 目标对象
     */
    public static void println(Object target) {
        System.out.println(target);
    }

    /**
     * 判断字符串是否不是空
     * @param cs 目标字符串
     * @return boolean
     */
    public static boolean not_blank(CharSequence cs) {
        return !is_blank(cs);
    }

    /**
     * 判断字符串是否为空
     * @param cs 目标字符串
     * @return boolean
     */
    public static boolean is_blank(CharSequence cs) {
        if (cs == null) {
            return true;
        }
        int strLen = cs.length();
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 聚合函数-count
     * @param target 集合
     * @return int
     */
    public static int count(Object target) {
        if (target == null) {
            return 0;
        } else if (target instanceof Map) {
            return 1;
        }
        try {
            return StreamExtension.arrayLikeToList(target).size();
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * 聚合函数-max (传统循环版，避开 BinaryOperator)
     * @param target 集合对象
     * @return 最大值
     */
    public static Object max(Object target) {
        if (target == null) return null;
        if (target instanceof Map) return target;
        try {
            List<Object> list = StreamExtension.arrayLikeToList(target);
            if (list == null || list.isEmpty()) return null;
            Object max = null;
            for (Object item : list) {
                if (max == null || BinaryOperation.compare(item, max) > 0) {
                    max = item;
                }
            }
            return max;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 聚合函数-min (传统循环版，避开 BinaryOperator)
     * @param target 集合对象
     * @return 最小值
     */
    public static Object min(Object target) {
        if (target == null) return null;
        if (target instanceof Map) return target;
        try {
            List<Object> list = StreamExtension.arrayLikeToList(target);
            if (list == null || list.isEmpty()) return null;
            Object min = null;
            for (Object item : list) {
                if (min == null || BinaryOperation.compare(item, min) < 0) {
                    min = item;
                }
            }
            return min;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 聚合函数-sum
     * @param target 集合
     * @return Number
     */
    public static Number sum(Object target) {
        if (target == null || target instanceof Map) {
            return null;
        }
        try {
            OptionalDouble value = StreamExtension.arrayLikeToList(target).stream()
                    .mapToDouble(v -> ObjectConvertExtension.asDouble(v, Double.NaN))
                    .filter(v -> !Double.isNaN(v))
                    .reduce(Double::sum);
            return value.isPresent() ? value.getAsDouble() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 聚合函数-avg
     * @param target 集合
     * @return Object
     */
    public static Object avg(Object target) {
        if (target == null || target instanceof Map) {
            return target;
        }
        try {
            OptionalDouble average = StreamExtension.arrayLikeToList(target).stream()
                    .mapToDouble(v -> ObjectConvertExtension.asDouble(v, Double.NaN))
                    .filter(v -> !Double.isNaN(v))
                    .average();
            return average.isPresent() ? average.getAsDouble() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期格式化
     * @param target 目标日期
     * @param pattern 格式
     * @return String
     */
    public static String date_format(Date target, String pattern) {
        return target == null ? null : DateExtension.format(target, pattern);
    }

    /**
     * 取当前时间
     * @return Date
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 取当前时间戳(秒)
     * @return  long
     */
    public static long current_timestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 取当前时间戳(毫秒)
     * @return long
     */
    public static long current_timestamp_millis() {
        return System.currentTimeMillis();
    }

    /**
     * 四舍五入保留N位小数
     * @param target 目标值
     * @param len 保留的小数位数
     * @return double
     */
    public static double round(Number target, int len) {
        return NumberExtension.round(target, len);
    }

    /**
     * 求百分比
     * @param target 目标值
     * @param len 保留的小数位数
     * @return String
     */
    public static String percent(Number target, int len) {
        return NumberExtension.asPercent(target, len);
    }

    /**
     * 四舍五入保留N为小数
     * @param target 目标值
     * @return double
     */
    public static double round(Number target) {
        return NumberExtension.round(target, 0);
    }

    /**
     * 向上取整
     * @param target 目标值
     * @return Number
     */
    public static Number ceil(Number target) {
        return NumberExtension.ceil(target);
    }

    /**
     * 向下取整
     * @param target  目标值
     * @return Number
     */
    public static Number floor(Number target) {
        return NumberExtension.floor(target);
    }

    /**
     * 求百分比
     * @param target  目标值
     * @return  String
     */
    public static String percent(Number target) {
        return NumberExtension.asPercent(target, 0);
    }

    /**
     * 打印
     * @param target 目标对象
     */
    public static void print(Object target) {
        System.out.print(target);
    }

    /**
     * 格式化打印
     * @param format 打印格式
     * @param args 打印参数
     */
    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }
}
