package com.Client.views;

import com.Client.service.AuthService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestClientException;

@Route("/login")
@PageTitle("Авторизация")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private TextField usernameField = new TextField("Логин");
    private PasswordField passwordField = new PasswordField("Пароль");
    private Button loginButton = new Button("Войти");

    private AuthService authService;

    public LoginView(AuthService authService) {
        this.authService = authService;
        usernameField.setPlaceholder("Введите логин");
        passwordField.setPlaceholder("Введите пароль");

        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, passwordField, loginButton);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 5));
        formLayout.setColspan(usernameField, 3);
        formLayout.setColspan(passwordField, 3);
        formLayout.setColspan(loginButton, 3);
//        formLayout.setClassName("login-view");
        add(formLayout);
        loginButton.addClickListener(e -> login());
        setAlignItems(Alignment.CENTER);//puts button in horizontal  center
        setJustifyContentMode(JustifyContentMode.BETWEEN);//puts button in vertical center
//        setSizeFull();
    }

    private void login() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        try {
            authService.login(username, password);
        } catch (RestClientException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Redirect to main view if already logged in
//        if (SecurityUtils.isUserLoggedIn()) {
//            event.rerouteTo("");
//        }
    }
}
