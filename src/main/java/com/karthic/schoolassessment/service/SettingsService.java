package com.karthic.schoolassessment.service;

import com.karthic.schoolassessment.model.AppSettings;

import java.io.IOException;

public interface SettingsService {

    AppSettings loadSettings() throws IOException;

    void saveSettings(AppSettings settings) throws IOException;

    /**
     * Changes the password for the given username. Updates both the in-memory
     * security manager and the persisted users.json file.
     */
    void changePassword(String username, String newPassword) throws IOException;

    boolean verifyCurrentPassword(String username, String rawPassword);
}
