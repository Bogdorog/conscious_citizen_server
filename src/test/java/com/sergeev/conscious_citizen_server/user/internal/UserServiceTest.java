package com.sergeev.conscious_citizen_server.user.internal;

import com.sergeev.conscious_citizen_server.user.api.UserRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository repository = mock(UserRepository.class);
    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

    private final UserService service = new UserService(repository, publisher);

    @Test
    void shouldRegisterUserSuccessfully() {

        when(repository.existsByEmail("test@mail.com")).thenReturn(false);

        User saved = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("test@mail.com")
                .phone("123")
                .build();

        when(repository.save(any())).thenReturn(saved);

        Long id = service.register("John Doe", "test@mail.com", "123");

        assertThat(id).isEqualTo(1L);

        ArgumentCaptor<UserRegisteredEvent> captor =
                ArgumentCaptor.forClass(UserRegisteredEvent.class);

        verify(publisher).publishEvent(captor.capture());

        assertThat(captor.getValue().userId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowIfEmailExists() {

        when(repository.existsByEmail("test@mail.com")).thenReturn(true);

        assertThatThrownBy(() ->
                service.register("John", "test@mail.com", "123")
        ).isInstanceOf(IllegalArgumentException.class);

        verify(repository, never()).save(any());
    }
}
