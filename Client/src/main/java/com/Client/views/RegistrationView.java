package com.Client.views;

import com.Client.services.AuthService;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestClientException;

import java.util.Objects;


@Route("/registration")
@PageTitle("Регистрация")
public class RegistrationView extends VerticalLayout implements BeforeEnterObserver {
    private final TextField usernameField = new TextField("Логин");
    private final TextField fioField = new TextField("ФИО");
    private final PasswordField passwordField = new PasswordField("Пароль");
    private final PasswordField confirmPasswordField = new PasswordField("Повторите пароль");
    private final ToggleButton rolesToggler = new ToggleButton();
    private final Button registerButton = new Button("Зарегистрироваться");

    private AuthService authService;

    public RegistrationView(AuthService authService) {
        this.authService = authService;

        usernameField.setLabel("Введите логин");
        fioField.setLabel("Введите ФИО");
        passwordField.setLabel("Введите пароль");
        confirmPasswordField.setLabel("Повторите пароль");
        rolesToggler.setLabel("Студент");


        FormLayout formLayout = new FormLayout();
        formLayout.add(usernameField, rolesToggler, fioField, passwordField, confirmPasswordField, registerButton);

        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("500px", 2));
        formLayout.setColspan(usernameField, 1);
        formLayout.setColspan(rolesToggler, 1);
        formLayout.setColspan(fioField, 2);
        formLayout.setColspan(passwordField, 1);
        formLayout.setColspan(confirmPasswordField, 1);
//        formLayout.setClassName("login-view");
        formLayout.setWidth(50, Unit.PERCENTAGE);
        add(formLayout);
//        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
        registerButton.addClickListener(e -> register());
        rolesToggler.addValueChangeListener(evt -> {
            if (!rolesToggler.getValue()) {
                rolesToggler.setLabel("Студент");
            }
            else {
                rolesToggler.setLabel("Преподаватель");
            }
        });
    }

    private void register() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();
        String confirmedPassword = confirmPasswordField.getValue();
        String name = fioField.getValue();
        String role = (rolesToggler.getValue()) ? "ROLE_TEACHER" : "ROLE_STUDENT";
        if (!Objects.equals(password, confirmedPassword)) {
            Notification.show("Пароли не совпадают", 3000, Notification.Position.TOP_CENTER);
        }
        else {
            try {
                authService.register(username, password, name, role);
                getUI().ifPresent(ui -> ui.navigate("/profile"));
            } catch (RestClientException e) {
                Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        }

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

    }
}
