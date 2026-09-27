package com.callscript.app.script;

import com.callscript.app.calllog.CallLogRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptServiceTest {

    @Mock
    private ScriptRepository repository;

    @Mock
    private CallLogRepository callLogRepository;

    private ScriptService service() {
        return new ScriptService(repository, callLogRepository);
    }

    @Test
    void create_既存2件のフェーズに新規追加すると_案3というラベルでactive状態で保存される() {
        when(repository.findByPhaseOrderByCreatedAtAsc("uketsuke")).thenReturn(List.of(
                new Script("uketsuke-1", "uketsuke", "基本形", "本文1", "active", Instant.now(), null),
                new Script("uketsuke-2", "uketsuke", "案2", "本文2", "archived", Instant.now(), null)
        ));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Script created = service().create("uketsuke", "新しい本文");

        ArgumentCaptor<Script> captor = ArgumentCaptor.forClass(Script.class);
        verify(repository).save(captor.capture());
        Script saved = captor.getValue();
        assertThat(saved.getPhase()).isEqualTo("uketsuke");
        assertThat(saved.getLabel()).isEqualTo("案3");
        assertThat(saved.getText()).isEqualTo("新しい本文");
        assertThat(saved.getStatus()).isEqualTo("active");
        assertThat(saved.getId()).startsWith("uketsuke-");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getRevisionOf()).isNull();
        assertThat(created).isEqualTo(saved);
    }

    @Test
    void listByPhase_includeArchivedがfalseの場合はactiveのみ返す() {
        Script active = new Script("uketsuke-1", "uketsuke", "基本形", "本文1", "active", Instant.now(), null);
        Script archived = new Script("uketsuke-2", "uketsuke", "旧版", "本文2", "archived", Instant.now(), null);
        when(repository.findByPhaseOrderByCreatedAtAsc("uketsuke")).thenReturn(List.of(active, archived));

        List<Script> result = service().listByPhase("uketsuke", false);

        assertThat(result).containsExactly(active);
    }

    @Test
    void listByPhase_includeArchivedがtrueの場合は全件返す() {
        Script active = new Script("uketsuke-1", "uketsuke", "基本形", "本文1", "active", Instant.now(), null);
        Script archived = new Script("uketsuke-2", "uketsuke", "旧版", "本文2", "archived", Instant.now(), null);
        when(repository.findByPhaseOrderByCreatedAtAsc("uketsuke")).thenReturn(List.of(active, archived));

        List<Script> result = service().listByPhase("uketsuke", true);

        assertThat(result).containsExactly(active, archived);
    }

    @Test
    void revise_使用実績があるトークを編集すると元がarchivedになり_v2ラベルの新バージョンが作られる() {
        Script original = new Script("uketsuke-1", "uketsuke", "基本形", "旧本文", "active", Instant.now(), null);
        when(repository.findById("uketsuke-1")).thenReturn(Optional.of(original));
        when(callLogRepository.existsByScriptId("uketsuke-1")).thenReturn(true);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Script revised = service().revise("uketsuke-1", "新本文");

        ArgumentCaptor<Script> captor = ArgumentCaptor.forClass(Script.class);
        verify(repository, times(2)).save(captor.capture());
        Script savedOld = captor.getAllValues().get(0);
        Script savedNew = captor.getAllValues().get(1);

        assertThat(savedOld.getId()).isEqualTo("uketsuke-1");
        assertThat(savedOld.getStatus()).isEqualTo("archived");

        assertThat(savedNew.getPhase()).isEqualTo("uketsuke");
        assertThat(savedNew.getLabel()).isEqualTo("基本形 v2");
        assertThat(savedNew.getText()).isEqualTo("新本文");
        assertThat(savedNew.getStatus()).isEqualTo("active");
        assertThat(savedNew.getRevisionOf()).isEqualTo("uketsuke-1");
        assertThat(revised).isEqualTo(savedNew);
    }

    @Test
    void revise_使用実績があるトークで既にv2のラベルを編集すると_v3になる() {
        Script v2 = new Script("uketsuke-2", "uketsuke", "基本形 v2", "本文", "active", Instant.now(), "uketsuke-1");
        when(repository.findById("uketsuke-2")).thenReturn(Optional.of(v2));
        when(callLogRepository.existsByScriptId("uketsuke-2")).thenReturn(true);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Script revised = service().revise("uketsuke-2", "さらに新しい本文");

        assertThat(revised.getLabel()).isEqualTo("基本形 v3");
        assertThat(revised.getRevisionOf()).isEqualTo("uketsuke-2");
    }

    @Test
    void revise_使用実績が一度もないトークを編集すると_同じidのまま上書きされる() {
        Script unused = new Script("uketsuke-3", "uketsuke", "案3", "旧本文", "active", Instant.now(), null);
        when(repository.findById("uketsuke-3")).thenReturn(Optional.of(unused));
        when(callLogRepository.existsByScriptId("uketsuke-3")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Script revised = service().revise("uketsuke-3", "新本文");

        verify(repository, times(1)).save(any());
        assertThat(revised.getId()).isEqualTo("uketsuke-3");
        assertThat(revised.getLabel()).isEqualTo("案3");
        assertThat(revised.getText()).isEqualTo("新本文");
        assertThat(revised.getStatus()).isEqualTo("active");
        assertThat(revised.getRevisionOf()).isNull();
    }

    @Test
    void revise_存在しないidを指定すると例外になる() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().revise("missing", "本文"))
                .isInstanceOf(ScriptNotFoundException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void archive_指定したスクリプトをarchived状態にして保存する() {
        Script active = new Script("uketsuke-1", "uketsuke", "基本形", "本文", "active", Instant.now(), null);
        when(repository.findById("uketsuke-1")).thenReturn(Optional.of(active));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Script result = service().archive("uketsuke-1");

        assertThat(result.getStatus()).isEqualTo("archived");
        verify(repository).save(active);
    }

    @Test
    void archive_存在しないidを指定すると例外になる() {
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().archive("missing"))
                .isInstanceOf(ScriptNotFoundException.class);
    }

    @Test
    void restore_archived状態のスクリプトをactiveに戻す() {
        Script archived = new Script("uketsuke-1", "uketsuke", "基本形", "本文", "archived", Instant.now(), null);
        when(repository.findById("uketsuke-1")).thenReturn(Optional.of(archived));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Script result = service().restore("uketsuke-1");

        assertThat(result.getStatus()).isEqualTo("active");
        verify(repository).save(archived);
    }
}
