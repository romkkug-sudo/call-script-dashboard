package com.callscript.app.script;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * 初期トークを投入する。id単位で「まだ登録されていないものだけ」保存するため、
 * 運用開始後にシード内容を追加しても、既存のデータ(記録・追加したトーク・編集内容)は
 * 一切変更されない。受付突破・ごあいさつ・クロージングは各2案、切り返し(反論対応の
 * 参考トーク)は14件、業種別トーク(受付突破・ごあいさつ)は8業種分。
 *
 * ※ 公開用のため、トーク本文はすべて架空の会社「サンプル株式会社」を想定したダミーです。
 */
@Component
public class ScriptSeeder implements ApplicationRunner {

    private static final String COMPANY = "サンプル株式会社";

    private final ScriptRepository repository;

    public ScriptSeeder(ScriptRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Instant now = Instant.now();
        List<Script> all = new ArrayList<>();
        all.addAll(uketsukeScripts(now));
        all.addAll(aisatsuScripts(now));
        all.addAll(closingScripts(now));
        all.addAll(kirikaeshiScripts(now));
        all.addAll(industryScripts(now));

        List<Script> missing = all.stream()
                .filter(s -> !repository.existsById(s.getId()))
                .toList();
        if (!missing.isEmpty()) {
            repository.saveAll(missing);
        }
    }

    private static Script active(String id, String phase, String label, String body, Instant now) {
        return new Script(id, phase, label, body, "active", now, null);
    }

    private List<Script> uketsukeScripts(Instant now) {
        return List.of(
                active("uketsuke-1", "uketsuke", "基本形(業務改善訴求)", """
                        お世話になっております。%sの〇〇と申します。
                        企業様の業務効率化のご案内でお電話いたしました。ご担当の方はいらっしゃいますか。""".formatted(COMPANY), now),
                active("uketsuke-2", "uketsuke", "案2(要件を先に伝える)", """
                        ● 名乗る
                        お世話になります。%sの〇〇と申します。
                        ● 要件を短く
                        社内の事務作業の効率化について、1分ほど状況をお伺いできればと思いお電話しました。
                        総務またはITご担当の方をお願いできますでしょうか。""".formatted(COMPANY), now)
        );
    }

    private List<Script> aisatsuScripts(Instant now) {
        return List.of(
                active("aisatsu-1", "aisatsu", "基本形(落ち着いた感謝)", """
                        お時間いただきありがとうございます。%sの〇〇です。""".formatted(COMPANY), now),
                active("aisatsu-2", "aisatsu", "案2(質問から入る)", """
                        ● 名乗る
                        お電話ありがとうございます。%sの〇〇です。
                        ● オープンクエスチョン
                        御社では、手作業で時間がかかっている事務作業などはございますか？
                        ● 現状確認
                        現在、外部の会社にご相談されていることはありますか？""".formatted(COMPANY), now)
        );
    }

    private List<Script> closingScripts(Instant now) {
        return List.of(
                active("closing-1", "closing", "基本形(訪問→リモート→資料→再架電)", """
                        一度、担当の者から詳しくご説明させていただければと思います。15分ほどで大丈夫ですので、来週でしたら何曜日がご都合よろしいでしょうか。

                        (難しい場合)オンラインでも可能です。
                        (それも難しい場合)資料をお送りして、改めてお電話してもよろしいでしょうか。""", now),
                active("closing-2", "closing", "案2(二択で日程を聞く)", """
                        ● 二択で打診
                        来週の火曜日と木曜日でしたら、どちらがご都合よろしいでしょうか。
                        ● 時間帯の確認
                        午前と午後では、どちらがよろしいですか。
                        ● 復唱して締める
                        〇日〇時にお伺いします。ありがとうございました。""", now)
        );
    }

    private List<Script> kirikaeshiScripts(Instant now) {
        return List.of(
                active("kirikaeshi-1", "kirikaeshi", "分岐:外部の会社を導入済み(YES)", """
                        ありがとうございます。今の取り組みで、まだ手が回っていない部分などはございますか？
                        比較材料として、情報交換だけでもさせていただければと思います。""", now),
                active("kirikaeshi-2", "kirikaeshi", "分岐:未導入・これから検討(NO)", """
                        承知しました。検討される際の参考に、他社様でよくある取り組み事例だけでもご紹介させていただけますか。""", now),
                active("kirikaeshi-3", "kirikaeshi", "共通ヒアリング:人手が足りない場合", """
                        人手が足りない中で、特に時間を取られている作業はどのあたりでしょうか。""", now),
                active("kirikaeshi-4", "kirikaeshi", "共通ヒアリング:アポイント打診", """
                        お話を伺って、お役に立てる部分がありそうだと感じました。15分ほど、詳しくご説明するお時間をいただけますか。""", now),
                active("kirikaeshi-5", "kirikaeshi", "「今は間に合っています」", """
                        承知しました。今後の見直しの際に参考にしていただけるよう、簡単な資料だけお送りしてもよろしいでしょうか。""", now),
                active("kirikaeshi-6", "kirikaeshi", "「具体的に何をしてくれる？」", """
                        例えば、手作業で行っている集計や転記作業を自動化するご支援をしています。御社で近い作業はございますか？""", now),
                active("kirikaeshi-7", "kirikaeshi", "「どれくらいの規模から頼める？」", """
                        小さな作業1つからご相談いただけます。まずは困っている作業を伺えればと思います。""", now),
                active("kirikaeshi-8", "kirikaeshi", "「資料だけ送ってください」", """
                        承知しました。関係のありそうな事例をお送りしたいので、特に気になる分野を1つだけ教えていただけますか。
                        資料をお送りした後、10分ほど補足のお時間をいただけますか？""", now),
                active("kirikaeshi-9", "kirikaeshi", "「忙しいので今は難しい」", """
                        お忙しいところ失礼しました。来週以降で、10分ほどお時間をいただける日はございますか？""", now),
                active("kirikaeshi-10", "kirikaeshi", "「予算がありません」", """
                        今すぐ大きな費用がかかるお話ではございません。
                        費用を抑えて効率化できるとしたら、検討したい作業はございますか？""", now),
                active("kirikaeshi-11", "kirikaeshi", "「必要になったら連絡します」", """
                        承知しました。必要なときに思い出していただけるよう、事例資料だけお送りしてもよろしいでしょうか。""", now),
                active("kirikaeshi-12", "kirikaeshi", "「興味ありません」", """
                        承知しました。今は優先度が高くない、という状況でしょうか。
                        今後の参考として、事例だけお送りしてもよろしいでしょうか。""", now),
                active("kirikaeshi-13", "kirikaeshi", "「担当者が不在です」", """
                        承知しました。ご担当者様は、どちらの部署の方になりますか？
                        改めてお電話したいのですが、つながりやすい曜日や時間帯はございますか？""", now),
                active("kirikaeshi-14", "kirikaeshi", "「他社と比較してから決めたい」", """
                        もちろんです。比較の材料として、弊社の事例もお送りしますので、あわせてご検討いただけますか。""", now)
        );
    }

    private static final String UKETSUKE_INDUSTRY_TEMPLATE = """
            %1$sの会社様に業務効率化のご案内をしておりまして、総務、または情報システムご担当の方にお取次ぎいただけますか。

            (部署名が分からない場合の一言)
            %1$sの会社様によくある%2$sのご案内で伺っております、とお伝えください。""";

    private static final String AISATSU_INDUSTRY_TEMPLATE = """
            ● 前置き
            2分だけ、要点だけお伝えします。ご不要でしたらその場でおっしゃってください。

            ● オープナー
            %1$sの会社様では、%2$sに手間がかかっているケースが多いと伺っています。御社ではいかがですか。

            ● ヒアリング(状況→問題→示唆)
            状況:%3$s
            問題:%4$s
            示唆:%5$s""";

    /**
     * 業種別トーク(受付突破・ごあいさつ)。業種ごとに「困りごとの言葉」と
     * ヒアリングの質問だけを差し替え、型(テンプレート)は共通にしている。
     */
    private List<Script> industryScripts(Instant now) {
        record Industry(String slug, String name, String painWord, String situation, String problem, String implication) {
        }
        List<Industry> industries = List.of(
                new Industry("kensetsu", "建設・建築", "工程管理・図面共有",
                        "現場の進捗確認は、今も紙や電話でのやり取りが中心ですか？",
                        "そうなると、図面の版違いや現場との情報共有漏れが起きたりしませんか。",
                        "それが続くと、複数の現場が重なるタイミングで特に大変になりませんか。"),
                new Industry("unyu", "運輸・物流", "配車・勤怠管理",
                        "配車表や勤怠は今も紙やエクセル中心ですか？",
                        "そうなると、配車の組み違いや残業時間の把握漏れが起きたりしませんか。",
                        "それが続くと、繁忙期に特に大変になりませんか。"),
                new Industry("service", "警備・ビルメンテナンス", "現場配置・勤怠管理",
                        "現場ごとの人員配置は手作業で組まれていますか？",
                        "そうなると、急な欠員対応や勤怠の集計ミスが起きたりしませんか。",
                        "それが続くと、人手不足のタイミングで特に大変になりませんか。"),
                new Industry("inshoku", "飲食", "シフト・仕入管理",
                        "シフトは店舗ごとに紙やチャットでのやり取りですか？",
                        "そうなると、シフトの調整漏れや仕入れの過不足が起きたりしませんか。",
                        "それが続くと、繁忙期に特に大変になりませんか。"),
                new Industry("it", "IT・Web", "案件進捗・ナレッジ共有",
                        "案件の進捗共有は担当者任せになっていませんか？",
                        "そうなると、引き継ぎのたびに手間がかかったりしませんか。",
                        "それが続くと、案件が重なるタイミングで特に大変になりませんか。"),
                new Industry("shokuhin", "食品", "製造工程・品質記録",
                        "製造記録は紙の帳票で管理されていますか？",
                        "そうなると、記録の記入漏れや確認作業に手間がかかったりしませんか。",
                        "それが続くと、監査対応のタイミングで特に大変になりませんか。"),
                new Industry("seizo", "製造", "加工工程・品質記録",
                        "加工の工程記録は紙台帳が中心ですか？",
                        "そうなると、転記ミスや確認作業に手間がかかったりしませんか。",
                        "それが続くと、品質トラブルの対応時に特に大変になりませんか。"),
                new Industry("iryo", "医療・福祉業界", "申し送り・シフト管理",
                        "申し送りは口頭やノート中心だったりしませんか？",
                        "そうなると、申し送り漏れやシフトの調整ミスが起きたりしませんか。",
                        "それが続くと、人手不足のタイミングで特に大変になりませんか。")
        );

        List<Script> scripts = new ArrayList<>();
        for (Industry ind : industries) {
            String label = ind.name() + "向け";
            scripts.add(active("uketsuke-ind-" + ind.slug(), "uketsuke", label,
                    UKETSUKE_INDUSTRY_TEMPLATE.formatted(ind.name(), ind.painWord()), now));
            scripts.add(active("aisatsu-ind-" + ind.slug(), "aisatsu", label,
                    AISATSU_INDUSTRY_TEMPLATE.formatted(ind.name(), ind.painWord(), ind.situation(),
                            ind.problem(), ind.implication()), now));
        }
        return scripts;
    }
}
