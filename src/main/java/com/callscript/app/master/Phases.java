package com.callscript.app.master;

import java.util.List;

/**
 * フェーズ定義(受付突破/ごあいさつ/クロージング/切り返し)。プロトタイプJSの
 * PHASES / SUCCESS_KEYS を移植したもの。「切り返し」は架電結果を記録するフェーズではなく、
 * 反論対応のトークを参照するためだけの一覧なので、results / successKeys は空にしている
 * (フロントエンド側はこれが空のフェーズを「結果記録なし」として扱う)。
 */
public final class Phases {

    public static final List<PhaseDefinition> ALL = List.of(
            new PhaseDefinition(
                    "uketsuke", "受付突破",
                    List.of(
                            new ResultOption("success", "担当者に繋がった", "success"),
                            new ResultOption("absent", "担当者不在", "fail"),
                            new ResultOption("receptionNg", "受付NG", "fail")
                    ),
                    List.of("success")
            ),
            new PhaseDefinition(
                    "aisatsu", "ごあいさつ",
                    List.of(
                            new ResultOption("success", "継続できた", "success"),
                            new ResultOption("ng", "NG", "fail")
                    ),
                    List.of("success")
            ),
            new PhaseDefinition(
                    "closing", "クロージング",
                    List.of(
                            new ResultOption("visit", "訪問アポ獲得", "success"),
                            new ResultOption("remote", "リモートアポ獲得", "success"),
                            new ResultOption("doc", "資料送付", "hold"),
                            new ResultOption("recall", "再架電", "hold"),
                            new ResultOption("ng", "NG", "fail")
                    ),
                    List.of("visit", "remote")
            ),
            new PhaseDefinition(
                    "kirikaeshi", "切り返し",
                    List.of(),
                    List.of()
            )
    );

    private Phases() {
    }
}
