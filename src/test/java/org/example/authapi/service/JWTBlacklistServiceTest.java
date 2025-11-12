package org.example.authapi.service;

import org.example.authapi.model.JwtBlacklist;
import org.example.authapi.repository.JWTBlacklistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class JWTBlacklistServiceTest {
    @Mock
    private JWTBlacklistRepository jwtBlacklistRepository;

    @InjectMocks
    private JWTBlacklistService jwtBlacklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBlacklistToken_WhenNotAlreadyBlacklisted_ShouldSaveToken() {
        String token ="abc123";

        when(jwtBlacklistRepository.existsByToken(token)).thenReturn(false);

        jwtBlacklistService.blacklistToken(token);

        ArgumentCaptor<JwtBlacklist> captor = ArgumentCaptor.forClass(JwtBlacklist.class);
        verify(jwtBlacklistRepository, times(1)).save(captor.capture());

        JwtBlacklist saved = captor.getValue();

        assertThat(saved.getToken()).isEqualTo(token);
        assertThat(saved.getBlacklistedAt()).isNotNull();
    }

    @Test
    void testBlacklistToken_WhenAlreadyBlacklisted_ShouldNotSaveAgain() {
        String token ="abc123";

        when(jwtBlacklistRepository.existsByToken(token)).thenReturn(true);
        jwtBlacklistService.blacklistToken(token);
        verify(jwtBlacklistRepository, times(1)).save(any(JwtBlacklist.class));
    }

    @Test
    void testIsBlacklisted_shouldReturnTrueOrFalse() {
        String token ="abc123";

        when(jwtBlacklistRepository.existsByToken(token)).thenReturn(true);
        assertThat(jwtBlacklistService.isBlacklisted(token)).isTrue();

        when(jwtBlacklistRepository.existsByToken(token)).thenReturn(false);
        assertThat(jwtBlacklistService.isBlacklisted(token)).isFalse();
    }
}
