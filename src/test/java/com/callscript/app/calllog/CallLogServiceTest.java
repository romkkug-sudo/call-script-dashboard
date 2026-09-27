package com.callscript.app.calllog;

import com.callscript.app.common.DayKeys;
import com.callscript.app.script.Script;
import com.callscript.app.script.ScriptNotFoundException;
import com.callscript.app.script.ScriptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CallLogServiceTest {

    @Mock
    private CallLogRepository repository;

    @Mock
    private ScriptRepository scriptRepository;

    private CallLogService service() {
        return new CallLogService(repository, scriptRepository);
    }

    @Test
    void record_通話結果を記録すると_スクリプトのラベルを引き継いで保存される() {
        Script script = new Script("uketsuke-1", "uketsuke", "基本形", "本文", "active", Instant.now(), null);
        when(scriptRepository.findById("uketsuke-1")).thenReturn(Optional.of(script));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CallLog result = service().record("uketsuke", "uketsuke-1", "success", null);

        ArgumentCaptor<CallLog> captor = ArgumentCaptor.forClass(CallLog.class);
        verify(repository).save(captor.capture());
        CallLog saved = captor.getValue();
        assertThat(saved.getPhase()).isEqualTo("uketsuke");
        assertThat(saved.getScriptId()).isEqualTo("uketsuke-1");
        assertThat(saved.getScriptLabel()).isEqualTo("基本形");
        assertThat(saved.getResult()).isEqualTo("success");
        assertThat(saved.getNgReason()).isNull();
        assertThat(saved.getMemo()).isEqualTo("");
        assertThat(saved.getTs()).isNotNull();
        assertThat(saved.getDayKey()).isEqualTo(DayKeys.today());
        assertThat(result).isEqualTo(saved);
    }

    @Test
    void record_NG理由付きで記録できる() {
        Script script = new Script("uketsuke-1", "uketsuke", "基本形", "本文", "active", Instant.now(), null);
        when(scriptRepository.findById("uketsuke-1")).thenReturn(Optional.of(script));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CallLog result = service().record("uketsuke", "uketsuke-1", "ng", "今は必要ない");

        assertThat(result.getResult()).isEqualTo("ng");
        assertThat(result.getNgReason()).isEqualTo("今は必要ない");
    }

    @Test
    void record_存在しないscriptIdを指定すると例外になる() {
        when(scriptRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().record("uketsuke", "missing", "success", null))
                .isInstanceOf(ScriptNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void listForToday_当日分のログをts降順で返す() {
        CallLog log1 = new CallLog(Instant.now(), DayKeys.today(), "uketsuke", "uketsuke-1", "基本形", "success", null, "");
        when(repository.findByDayKeyOrderByTsDesc(DayKeys.today())).thenReturn(List.of(log1));

        List<CallLog> result = service().listForToday();

        assertThat(result).containsExactly(log1);
    }

    @Test
    void listAll_全期間のログをts降順で返す() {
        CallLog log1 = new CallLog(Instant.now(), DayKeys.today(), "uketsuke", "uketsuke-1", "基本形", "success", null, "");
        when(repository.findAllByOrderByTsDesc()).thenReturn(List.of(log1));

        List<CallLog> result = service().listAll();

        assertThat(result).containsExactly(log1);
    }

    @Test
    void delete_指定したidのログを削除する() {
        service().delete(5L);

        verify(repository).deleteById(5L);
    }

    @Test
    void updateMemo_メモを更新して保存する() {
        CallLog log = new CallLog(Instant.now(), DayKeys.today(), "uketsuke", "uketsuke-1", "基本形", "success", null, "");
        when(repository.findById(5L)).thenReturn(Optional.of(log));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CallLog result = service().updateMemo(5L, "いい感触だった");

        assertThat(result.getMemo()).isEqualTo("いい感触だった");
        verify(repository).save(log);
    }

    @Test
    void updateMemo_存在しないidを指定すると例外になる() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().updateMemo(5L, "メモ"))
                .isInstanceOf(CallLogNotFoundException.class);
    }
}
