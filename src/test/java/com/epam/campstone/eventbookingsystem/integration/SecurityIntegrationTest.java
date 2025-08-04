package com.epam.campstone.eventbookingsystem.integration;

import com.epam.campstone.eventbookingsystem.model.User;
import com.epam.campstone.eventbookingsystem.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USER_EMAIL = "test@example.com";
    private static final String TEST_USER_PASSWORD = "password123";
    private static final String TEST_USER_FIRST_NAME = "Test";
    private static final String TEST_USER_LAST_NAME = "User";

    @BeforeEach
    void setUp() {
        // Create a test user
        User user = new User();
        user.setEmail(TEST_USER_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_USER_PASSWORD));
        user.setFirstName(TEST_USER_FIRST_NAME);
        user.setLastName(TEST_USER_LAST_NAME);
        user.setActive(true);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void accessPublicPage_ShouldBeSuccessful() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void accessProtectedPage_Unauthenticated_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/auth/login"));
    }

    @Test
    void login_WithValidCredentials_ShouldSucceed() throws Exception {
        mockMvc.perform(formLogin("/auth/login")
                        .user("username", TEST_USER_EMAIL)
                        .password("password", TEST_USER_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(authenticated().withUsername(TEST_USER_EMAIL));
    }

    @Test
    void login_WithInvalidCredentials_ShouldFail() throws Exception {
        mockMvc.perform(formLogin("/auth/login")
                        .user("username", TEST_USER_EMAIL)
                        .password("password", "wrongpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=true"))
                .andExpect(unauthenticated());
    }

    @Test
    void register_WithValidData_ShouldCreateUserAndRedirect() throws Exception {
        String newUserEmail = "newuser@example.com";

        mockMvc.perform(post("/auth/register")
                        .param("firstName", "New")
                        .param("lastName", "User")
                        .param("email", newUserEmail)
                        .param("password", "newpassword123")
                        .param("confirmPassword", "newpassword123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?registered"));

        // Verify user was created
        assertThat(userRepository.findByEmail(newUserEmail)).isPresent();
    }

    @Test
    void logout_ShouldInvalidateSession() throws Exception {
        // First login
        mockMvc.perform(formLogin("/auth/login")
                        .user("username", TEST_USER_EMAIL)
                        .password("password", TEST_USER_PASSWORD));

        // Then logout
        mockMvc.perform(post("/auth/logout").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout"))
                .andExpect(unauthenticated());
    }

    @Test
    void accessAdminPage_AsUser_ShouldBeForbidden() throws Exception {
        // Login as regular user
        mockMvc.perform(formLogin("/auth/login")
                        .user("username", TEST_USER_EMAIL)
                        .password("password", TEST_USER_PASSWORD));

        // Try to access admin page
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    void csrfProtection_ShouldBeEnabled() throws Exception {
        // Try to submit a form without CSRF token
        mockMvc.perform(post("/auth/register")
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("email", "test2@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123"))
                .andExpect(status().isForbidden());
    }
}
