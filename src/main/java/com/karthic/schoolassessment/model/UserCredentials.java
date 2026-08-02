package com.karthic.schoolassessment.model;

/**
 * Represents a persisted user credential entry in users.json.
 */
public record UserCredentials(String username, String encodedPassword, String role) {}
