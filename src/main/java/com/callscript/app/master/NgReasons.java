package com.callscript.app.master;

import java.util.List;

/**
 * NG理由マスタ(固定6種、自由記述にしない)。理由の追加・編集が必要になったらDBテーブル化を検討する。
 */
public final class NgReasons {

    public static final List<String> ALL = List.of(
            "今は必要ない",
            "既に取引先がある",
            "担当者不在",
            "検討します(保留)",
            "自社で対応している",
            "予算がない"
    );

    private NgReasons() {
    }
}
