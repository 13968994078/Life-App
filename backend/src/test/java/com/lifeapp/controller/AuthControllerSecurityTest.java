package com.lifeapp.controller;

import com.lifeapp.auth.AuthInterceptor;
import com.lifeapp.auth.JwtUtil;
import com.lifeapp.common.GlobalExceptionHandler;
import com.lifeapp.dto.LoginRequest;
import com.lifeapp.dto.UpdateProfileRequest;
import com.lifeapp.service.AuthService;
import com.lifeapp.vo.LoginResponse;
import com.lifeapp.vo.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerSecurityTest {

    private static final String TEST_SECRET = "01234567890123456789012345678901";

    private AuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .addInterceptors(new AuthInterceptor(new JwtUtil(TEST_SECRET)))
                .build();
    }

    @Test
    void currentUserReturnsUnauthorizedWhenAuthorizationMissing() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("请先登录"));

        verifyNoInteractions(authService);
    }

    @Test
    void currentUserReturnsUnauthorizedWhenTokenInvalid() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("登录已失效，请重新登录"));

        verifyNoInteractions(authService);
    }

    @Test
    void loginDoesNotRequireAuthorizationHeader() throws Exception {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setUsername("demo");
        user.setNickname("demo");

        LoginResponse response = new LoginResponse();
        response.setToken("token-123");
        response.setUser(user);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("token-123"))
                .andExpect(jsonPath("$.data.user.username").value("demo"));
    }

    @Test
    void updateProfileRequiresAuthorizationHeader() throws Exception {
        mockMvc.perform(put("/api/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"早睡达人\",\"avatar\":\"https://example.com/avatar.png\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("请先登录"));

        verifyNoInteractions(authService);
    }

    @Test
    void updateProfileReturnsLatestUserProfile() throws Exception {
        UserProfile user = new UserProfile();
        user.setId(1L);
        user.setUsername("demo");
        user.setNickname("早睡达人");
        user.setAvatar("https://example.com/avatar.png");
        when(authService.updateProfile(any(UpdateProfileRequest.class))).thenReturn(user);

        mockMvc.perform(put("/api/auth/profile")
                        .header("Authorization", "Bearer " + new JwtUtil(TEST_SECRET).generateToken(1L, "demo"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"早睡达人\",\"avatar\":\"https://example.com/avatar.png\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nickname").value("早睡达人"))
                .andExpect(jsonPath("$.data.avatar").value("https://example.com/avatar.png"));
    }

    @Test
    void updateProfileRejectsBlankNickname() throws Exception {
        mockMvc.perform(put("/api/auth/profile")
                        .header("Authorization", "Bearer " + new JwtUtil(TEST_SECRET).generateToken(1L, "demo"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"   \",\"avatar\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
