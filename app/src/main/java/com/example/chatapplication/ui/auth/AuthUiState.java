package com.example.chatapplication.ui.auth;

public class AuthUiState {

    public enum Status {
        IDLE,
        LOADING,
        CODE_SENT,
        VERIFIED,
        ERROR
    }

    public final Status status;
    public final String message;

    private AuthUiState(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static AuthUiState idle() {
        return new AuthUiState(Status.IDLE, null);
    }

    public static AuthUiState loading() {
        return new AuthUiState(Status.LOADING, null);
    }

    public static AuthUiState codeSent() {
        return new AuthUiState(Status.CODE_SENT, null);
    }

    public static AuthUiState verified() {
        return new AuthUiState(Status.VERIFIED, null);
    }

    public static AuthUiState error(String message) {
        return new AuthUiState(Status.ERROR, message);
    }
}
