package com.callscript.app.master;

/**
 * フェーズごとの結果選択肢(例:「担当者に繋がった」「NG」)。
 * cls はフロントエンドの表示色分け(success/hold/fail)に対応する。
 */
public record ResultOption(String key, String label, String cls) {
}
