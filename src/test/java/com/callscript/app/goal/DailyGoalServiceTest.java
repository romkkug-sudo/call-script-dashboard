package com.callscript.app.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyGoalServiceTest {

    @Mock
    private DailyGoalRepository repository;

    private DailyGoalService service() {
        return new DailyGoalService(repository);
    }

    @Test
    void getGoal_未設定の場合はコール70_アポ1のデフォルト値を作成して返す() {
        when(repository.findById("default")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DailyGoal goal = service().getGoal();

        ArgumentCaptor<DailyGoal> captor = ArgumentCaptor.forClass(DailyGoal.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("default");
        assertThat(captor.getValue().getCallTarget()).isEqualTo(70);
        assertThat(captor.getValue().getAppointmentTarget()).isEqualTo(1);
        assertThat(goal.getCallTarget()).isEqualTo(70);
        assertThat(goal.getAppointmentTarget()).isEqualTo(1);
    }

    @Test
    void getGoal_既に設定がある場合はその値をそのまま返す() {
        DailyGoal existing = new DailyGoal("default", 50, 2);
        when(repository.findById("default")).thenReturn(Optional.of(existing));

        DailyGoal goal = service().getGoal();

        assertThat(goal.getCallTarget()).isEqualTo(50);
        assertThat(goal.getAppointmentTarget()).isEqualTo(2);
        verify(repository, never()).save(any());
    }

    @Test
    void update_既存の設定を新しい目標値に更新して保存する() {
        DailyGoal existing = new DailyGoal("default", 70, 1);
        when(repository.findById("default")).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DailyGoal updated = service().update(100, 3);

        assertThat(updated.getCallTarget()).isEqualTo(100);
        assertThat(updated.getAppointmentTarget()).isEqualTo(3);
        verify(repository).save(existing);
    }

    @Test
    void update_未設定の状態で更新すると新規作成される() {
        when(repository.findById("default")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DailyGoal updated = service().update(80, 2);

        assertThat(updated.getId()).isEqualTo("default");
        assertThat(updated.getCallTarget()).isEqualTo(80);
        assertThat(updated.getAppointmentTarget()).isEqualTo(2);
    }

    @Test
    void update_コール目標が0以下だと例外になる() {
        assertThatThrownBy(() -> service().update(0, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void update_アポ目標が0以下だと例外になる() {
        assertThatThrownBy(() -> service().update(70, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
