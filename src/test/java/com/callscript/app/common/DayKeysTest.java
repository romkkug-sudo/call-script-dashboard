package com.callscript.app.common;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class DayKeysTest {

    @Test
    void of_日付をYYYY_M_D形式のキーに変換する_ゼロ埋めしない() {
        LocalDate date = LocalDate.of(2026, Month.JANUARY, 5);

        String key = DayKeys.of(date);

        assertThat(key).isEqualTo("2026-1-5");
    }

    @Test
    void today_現在の日付から当日のキーを生成する() {
        String key = DayKeys.today();

        assertThat(key).isEqualTo(DayKeys.of(LocalDate.now()));
    }
}
