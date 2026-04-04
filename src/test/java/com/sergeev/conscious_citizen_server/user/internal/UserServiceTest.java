package com.sergeev.conscious_citizen_server.user.internal;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {/*
    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordChangeService passwordChangeService;

    @InjectMocks
    private UserService userService;

    private Long userId;
    private User activeUser;

    UserServiceTest() {}

    @BeforeEach
    void setUp() {
        SecureRandom secureRandom = new SecureRandom();
        userId = Math.abs(secureRandom.nextLong());
        Role role = new Role(2L, "USER");
        activeUser = new User(
                userId,
                "John Doe",
                role,
                "Russia",
                "test@mail.com",
                "+12345678901",
                "encoded-password",
                true
        );
    }

    // Вход позитивные тесты

    @Test
    void shouldLoginByEmailSuccessfully() {

        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        AuthResult result = userService.email(new LoginRequest("test@mail.com", "raw-password"));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId.toString(), result.accessToken());
    }

    @Test
    void shouldLoginByPhoneSuccessfully() {

        when(repository.findByPhone("+12345678901"))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        AuthResult result = userService.email(new LoginRequest("+12345678901", "raw-password"));
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId.toString(), result.accessToken());
    }

    // Вход негативные тесты

    @Test
    void shouldThrowIfUserNotFound() {

        when(repository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.email(new LoginRequest("unknown@mail.com", "pass"))
        );
    }

    @Test
    void shouldThrowIfPasswordInvalid() {

        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches("wrong", "encoded-password"))
                .thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.email(new LoginRequest("test@mail.com", "wrong"))
        );
    }

    @Test
    void shouldThrowIfUserInactive() {
        Role role = new Role(2L, "USER");
        User inactiveUser = new User(
                userId,
                "John Doe",
                role,
                "Russia",
                "test@mail.com",
                "+12345678901",
                "encoded-password",
                false
        );

        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(inactiveUser));

        when(passwordEncoder.matches("raw-password", "encoded-password"))
                .thenReturn(true);

        Assertions.assertThrows(IllegalStateException.class, () ->
                userService.email(new LoginRequest("test@mail.com", "raw-password"))
        );
    }

    // Регистрация
    @Test
    void shouldRegisterSuccessfully() {
        RegisterUserRequest request = new RegisterUserRequest(
                "John",  "+12345678901", "test@mail.com","addr", "pass"
        );

        when(repository.existsByEmail("test@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("hash");

        User savedUser = User.builder().id(1L).build();
        when(repository.save(any())).thenReturn(savedUser);

        Long id = userService.register(request);

        Assertions.assertEquals(1L, id);
        verify(publisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void shouldThrowIfUserAlreadyExists() {
        RegisterUserRequest request = new RegisterUserRequest(
                "John",  "+123", "test@mail.com","addr", "pass"
        );

        when(repository.existsByEmail("test@mail.com")).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.register(request));
    }

    // Редактирование профиля
    @Test
    void shouldUpdateProfile() {
        UpdateProfileRequest request = new UpdateProfileRequest(
                "New Name", "+999","test@mail.com",  "New addr"
        );

        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(activeUser));

        when(userMapper.toResponse(any())).thenReturn(new UserDto(activeUser.getFullName(), activeUser.getPhone(), activeUser.getEmail(), activeUser.getAddress()));
        UserDto dto = userService.updateProfile(request);

        Assertions.assertNotNull(dto);
        verify(publisher).publishEvent(any(UserProfileUpdatedEvent.class));
    }

    @Test
    void shouldGetUser() {
        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(activeUser));

        when(userMapper.toResponse(activeUser))
                .thenReturn(new UserDto(activeUser.getFullName(), activeUser.getPhone(), activeUser.getEmail(), activeUser.getAddress()));

        UserDto dto = userService.get("test@mail.com");

        Assertions.assertNotNull(dto);
    }

    // Смена пароля
    @Test
    void shouldInitiatePasswordReset() {
        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(activeUser));

        when(passwordChangeService.generateToken()).thenReturn("raw");
        when(passwordChangeService.hashToken("raw")).thenReturn("hash");

        userService.initiatePasswordReset(
                new PasswordResetRequest("test@mail.com")
        );

        verify(tokenRepository).save(any());
        verify(publisher).publishEvent(any(PasswordResetRequestedEvent.class));
    }

    @Test
    void shouldThrowIfUserNotFoundOnReset() {
        when(repository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.initiatePasswordReset(
                        new PasswordResetRequest("test@mail.com")
                ));
    }

    @Test
    void shouldConfirmPasswordReset() {
        PasswordResetToken token = mock(PasswordResetToken.class);

        when(passwordChangeService.hashToken("token")).thenReturn("hash");
        when(tokenRepository.findByTokenHash("hash"))
                .thenReturn(Optional.of(token));
        when(token.isExpired()).thenReturn(false);
        when(token.isUsed()).thenReturn(false);

        when(repository.findById(userId))
                .thenReturn(Optional.of(activeUser));

        when(token.getUserId()).thenReturn(userId);
        when(passwordEncoder.encode("new")).thenReturn("encoded");

        userService.confirmPasswordReset(
                new PasswordResetConfirmRequest("token", "new")
        );

        verify(tokenRepository).invalidateAllForUser(userId);
        verify(repository).save(any());
    }

    @Test
    void shouldThrowIfTokenInvalid() {
        when(passwordChangeService.hashToken("token")).thenReturn("hash");
        when(tokenRepository.findByTokenHash("hash"))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                userService.confirmPasswordReset(
                        new PasswordResetConfirmRequest("token", "new")
                ));
    }

    @Test
    void shouldThrowIfTokenExpired() {
        PasswordResetToken token = mock(PasswordResetToken.class);

        when(passwordChangeService.hashToken("token")).thenReturn("hash");
        when(tokenRepository.findByTokenHash("hash"))
                .thenReturn(Optional.of(token));

        when(token.isExpired()).thenReturn(true);

        Assertions.assertThrows(IllegalStateException.class, () ->
                userService.confirmPasswordReset(
                        new PasswordResetConfirmRequest("token", "new")
                ));
    }

    @Test
    void shouldThrowIfTokenUsed() {
        PasswordResetToken token = mock(PasswordResetToken.class);

        when(passwordChangeService.hashToken("token")).thenReturn("hash");
        when(tokenRepository.findByTokenHash("hash"))
                .thenReturn(Optional.of(token));

        when(token.isExpired()).thenReturn(false);
        when(token.isUsed()).thenReturn(true);

        Assertions.assertThrows(IllegalStateException.class, () ->
                userService.confirmPasswordReset(
                        new PasswordResetConfirmRequest("token", "new")
                ));
    }

*/
}
