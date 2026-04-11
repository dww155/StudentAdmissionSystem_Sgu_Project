package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.Application;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Authentication.LoginRequest;
import com.sgu.admission_desktop.dto.Authentication.LoginResponse;
import com.sgu.admission_desktop.service.AuthService;
import com.sgu.admission_desktop.util.ApiClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        hideError();
    }

    @FXML
    private void onLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password.");
            return;
        }

        setLoading(true);
        hideError();

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("username", username);
            payload.put("password", password);

            LoginRequest request = ControllerSupport.convert(payload, LoginRequest.class);
            ApiResponse<LoginResponse> response = authService.login(request);

            if (!response.isSuccess()) {
                showError(response.getMessage() == null ? "Login failed." : response.getMessage());
                setLoading(false);
                return;
            }

            Map<String, Object> loginData = ControllerSupport.toMap(response.getData());
            String token = ControllerSupport.safeString(loginData.get("token"));
            boolean authenticated = Boolean.parseBoolean(String.valueOf(loginData.get("authenticated")));

            if (!authenticated || token.isBlank()) {
                showError("Invalid username or password.");
                setLoading(false);
                return;
            }

            ApiClient.setToken(token);

            Application.showMainLayout();
        } catch (Exception e) {
            showError(ControllerSupport.extractMessage(e));
            setLoading(false);
        }
    }

    private void setLoading(boolean loading) {
        loginButton.setDisable(loading);
        loginButton.setText(loading ? "Signing in..." : "Login");
        usernameField.setDisable(loading);
        passwordField.setDisable(loading);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
