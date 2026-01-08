package util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 时间工具类（Java 8+）
 * 线程安全，基于 java.time
 */
public final class TimeUtil {

    private TimeUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /* ==================== 基础 ==================== */

    /**
     * 获取当前时间戳（毫秒）
     */
    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间戳（秒）
     */
    public static long nowSeconds() {
        return Instant.now().getEpochSecond();
    }

    /**
     * 获取当前 LocalDateTime（系统默认时区）
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前 ZonedDateTime（系统默认时区）
     */
    public static ZonedDateTime nowZoned() {
        return ZonedDateTime.now();
    }

    /* ==================== 时间戳转换 ==================== */

    /**
     * 毫秒时间戳 → LocalDateTime
     */
    public static LocalDateTime millisToLocalDateTime(long millis) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(millis),
                ZoneId.systemDefault()
        );
    }

    /**
     * 秒时间戳 → LocalDateTime
     */
    public static LocalDateTime secondsToLocalDateTime(long seconds) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(seconds),
                ZoneId.systemDefault()
        );
    }

    /**
     * LocalDateTime → 毫秒时间戳
     */
    public static long localDateTimeToMillis(LocalDateTime time) {
        Objects.requireNonNull(time);
        return time.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    /**
     * LocalDateTime → 秒时间戳
     */
    public static long localDateTimeToSeconds(LocalDateTime time) {
        Objects.requireNonNull(time);
        return time.atZone(ZoneId.systemDefault())
                .toInstant()
                .getEpochSecond();
    }

    /* ==================== 格式化 & 解析 ==================== */

    /**
     * 默认格式
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * LocalDateTime → 字符串（默认格式）
     */
    public static String format(LocalDateTime time) {
        return format(time, DEFAULT_PATTERN);
    }

    /**
     * LocalDateTime → 字符串（自定义格式）
     */
    public static String format(LocalDateTime time, String pattern) {
        Objects.requireNonNull(time);
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 字符串 → LocalDateTime（默认格式）
     */
    public static LocalDateTime parse(String timeStr) {
        return parse(timeStr, DEFAULT_PATTERN);
    }

    /**
     * 字符串 → LocalDateTime（自定义格式）
     */
    public static LocalDateTime parse(String timeStr, String pattern) {
        Objects.requireNonNull(timeStr);
        return LocalDateTime.parse(
                timeStr,
                DateTimeFormatter.ofPattern(pattern)
        );
    }

    /* ==================== 常用时间点 ==================== */

    /**
     * 今天开始时间 00:00:00
     */
    public static LocalDateTime startOfToday() {
        return LocalDate.now().atStartOfDay();
    }

    /**
     * 今天结束时间 23:59:59
     */
    public static LocalDateTime endOfToday() {
        return LocalDate.now().atTime(23, 59, 59);
    }

    /**
     * 指定日期开始时间
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * 指定日期结束时间
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    /* ==================== 时间计算 ==================== */

    /**
     * 增加秒
     */
    public static LocalDateTime plusSeconds(LocalDateTime time, long seconds) {
        return time.plusSeconds(seconds);
    }

    /**
     * 增加分钟
     */
    public static LocalDateTime plusMinutes(LocalDateTime time, long minutes) {
        return time.plusMinutes(minutes);
    }

    /**
     * 增加小时
     */
    public static LocalDateTime plusHours(LocalDateTime time, long hours) {
        return time.plusHours(hours);
    }

    /**
     * 增加天
     */
    public static LocalDateTime plusDays(LocalDateTime time, long days) {
        return time.plusDays(days);
    }

    /**
     * 两个时间相差秒数
     */
    public static long betweenSeconds(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).getSeconds();
    }

    /**
     * 两个时间相差毫秒
     */
    public static long betweenMillis(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toMillis();
    }
}

