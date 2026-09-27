package com.callscript.app.script;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScriptSeederTest {

    @Mock
    private ScriptRepository repository;

    private ScriptSeeder seeder() {
        return new ScriptSeeder(repository);
    }

    @Test
    void run_何も登録されていなければ通話3フェーズ分_各2案の初期トークを保存する() throws Exception {
        when(repository.existsById(anyString())).thenReturn(false);

        seeder().run(null);

        List<Script> saved = capturedScripts();
        assertThat(saved).filteredOn(s -> !"kirikaeshi".equals(s.getPhase()) && !s.getId().contains("-ind-"))
                .extracting(Script::getId, Script::getPhase, Script::getStatus)
                .containsExactlyInAnyOrder(
                        tuple("uketsuke-1", "uketsuke", "active"),
                        tuple("uketsuke-2", "uketsuke", "active"),
                        tuple("aisatsu-1", "aisatsu", "active"),
                        tuple("aisatsu-2", "aisatsu", "active"),
                        tuple("closing-1", "closing", "active"),
                        tuple("closing-2", "closing", "active")
                );
    }

    @Test
    void run_何も登録されていなければ切り返しトークも14件保存する() throws Exception {
        when(repository.existsById(anyString())).thenReturn(false);

        seeder().run(null);

        List<Script> saved = capturedScripts();
        assertThat(saved).filteredOn(s -> "kirikaeshi".equals(s.getPhase())).hasSize(14)
                .allMatch(s -> "active".equals(s.getStatus()))
                .allMatch(s -> s.getId().startsWith("kirikaeshi-"))
                .extracting(Script::getLabel)
                .contains("「今は間に合っています」", "「予算がありません」", "「担当者が不在です」");
    }

    @Test
    void run_何も登録されていなければ業種別トークを8業種分_受付突破とごあいさつに保存する() throws Exception {
        when(repository.existsById(anyString())).thenReturn(false);

        seeder().run(null);

        List<Script> saved = capturedScripts();
        assertThat(saved).filteredOn(s -> s.getId().contains("-ind-") && "uketsuke".equals(s.getPhase()))
                .hasSize(8);
        assertThat(saved).filteredOn(s -> s.getId().contains("-ind-") && "aisatsu".equals(s.getPhase()))
                .hasSize(8);
        assertThat(saved).extracting(Script::getLabel).contains("建設・建築向け", "医療・福祉業界向け");
    }

    @Test
    void run_一部だけ既に登録済みならその分だけ除いて保存する() throws Exception {
        when(repository.existsById(anyString())).thenReturn(false);
        when(repository.existsById("uketsuke-1")).thenReturn(true);

        seeder().run(null);

        List<Script> saved = capturedScripts();
        assertThat(saved).extracting(Script::getId).doesNotContain("uketsuke-1");
        assertThat(saved).extracting(Script::getId).contains("uketsuke-2");
    }

    @Test
    void run_すべて登録済みなら何もしない() throws Exception {
        when(repository.existsById(anyString())).thenReturn(true);

        seeder().run(null);

        verify(repository, never()).saveAll(anyList());
    }

    private List<Script> capturedScripts() {
        ArgumentCaptor<List<Script>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        return captor.getValue();
    }
}
