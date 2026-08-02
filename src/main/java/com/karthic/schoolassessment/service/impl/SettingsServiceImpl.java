package com.karthic.schoolassessment.service.impl;

import com.karthic.schoolassessment.model.AppSettings;
import com.karthic.schoolassessment.model.UserCredentials;
import com.karthic.schoolassessment.service.SettingsService;
import com.karthic.schoolassessment.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsServiceImpl.class);

    private final StorageService storageService;
    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final Path usersFilePath;
    private final Path settingsFilePath;

    public SettingsServiceImpl(
            StorageService storageService,
            InMemoryUserDetailsManager userDetailsManager,
            PasswordEncoder passwordEncoder,
            @Value("${storage.config-path}") String configPath) {
        this.storageService = storageService;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.usersFilePath = Path.of(configPath, "users.json");
        this.settingsFilePath = Path.of(configPath, "settings.json");
    }

    @Override
    public AppSettings loadSettings() throws IOException {
        if (!storageService.exists(settingsFilePath)) {
            return AppSettings.builder()
                    .defaultExamDuration(60)
                    .questionsPerPage(10)
                    .build();
        }
        return storageService.readJson(settingsFilePath, AppSettings.class);
    }

    @Override
    public void saveSettings(AppSettings settings) throws IOException {
        storageService.writeJson(settingsFilePath, settings);
        log.info("Application settings saved: duration={}, questionsPerPage={}",
                settings.getDefaultExamDuration(), settings.getQuestionsPerPage());
    }

    @Override
    public void changePassword(String username, String newPassword) throws IOException {
        String encoded = passwordEncoder.encode(newPassword);

        UserDetails existing = userDetailsManager.loadUserByUsername(username);
        UserDetails updated = User.withUserDetails(existing)
                .password(encoded)
                .build();
        userDetailsManager.updateUser(updated);

        List<UserCredentials> users = loadAllUsers();
        List<UserCredentials> persisted = users.stream()
                .map(u -> u.username().equals(username)
                        ? new UserCredentials(u.username(), encoded, u.role())
                        : u)
                .toList();
        storageService.writeJson(usersFilePath, persisted);
        log.info("Password changed for user: {}", username);
    }

    @Override
    public boolean verifyCurrentPassword(String username, String rawPassword) {
        UserDetails user = userDetailsManager.loadUserByUsername(username);
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public List<UserCredentials> loadAllUsers() throws IOException {
        if (!storageService.exists(usersFilePath)) {
            return List.of();
        }
        return storageService.readJsonList(usersFilePath, UserCredentials.class);
    }
}
