package com.karthic.schoolassessment.config;

import com.karthic.schoolassessment.model.UserCredentials;
import com.karthic.schoolassessment.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final StorageService storageService;
    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final String examPath;
    private final String resultPath;
    private final String configPath;

    public DataInitializer(
            StorageService storageService,
            InMemoryUserDetailsManager userDetailsManager,
            PasswordEncoder passwordEncoder,
            @Value("${storage.exam-path}") String examPath,
            @Value("${storage.result-path}") String resultPath,
            @Value("${storage.config-path}") String configPath) {
        this.storageService = storageService;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.examPath = examPath;
        this.resultPath = resultPath;
        this.configPath = configPath;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        createDirectory(examPath);
        createDirectory(resultPath);
        createDirectory(configPath);
        initializeUsers();
        log.info("Data directories initialized successfully");
    }

    private void initializeUsers() throws IOException {
        Path usersFile = Path.of(configPath, "users.json");

        if (!Files.exists(usersFile)) {
            List<UserCredentials> defaults = List.of(
                    new UserCredentials("admin",   passwordEncoder.encode("admin123"),   "ROLE_ADMIN"),
                    new UserCredentials("student", passwordEncoder.encode("student123"), "ROLE_STUDENT")
            );
            storageService.writeJson(usersFile, defaults);
            log.info("Created default users.json");
            return;
        }

        List<UserCredentials> users = storageService.readJsonList(usersFile, UserCredentials.class);
        for (UserCredentials uc : users) {
            UserDetails existing = userDetailsManager.loadUserByUsername(uc.username());
            UserDetails updated = User.withUserDetails(existing)
                    .password(uc.encodedPassword())
                    .build();
            userDetailsManager.updateUser(updated);
        }
        log.info("Loaded {} user(s) from users.json", users.size());
    }

    private void createDirectory(String path) throws IOException {
        Path dir = Path.of(path);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            log.info("Created directory: {}", dir.toAbsolutePath());
        }
    }
}
