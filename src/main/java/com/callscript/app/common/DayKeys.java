package com.callscript.app.common;

import java.time.LocalDate;

/**
 * 当日集計に使う日付キー("YYYY-M-D"形式、ゼロ埋めなし)。
 * フロントエンドのプロトタイプにあった todayKey() のロジックをそのまま踏襲する。
 */
public final class DayKeys {

    private DayKeys() {
    }

    public static String today() {
        return of(LocalDate.now());
    }

    public static String of(LocalDate date) {
        return date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth();
    }
}
