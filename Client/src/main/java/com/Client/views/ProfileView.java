package com.Client.views;

import com.Client.model.UserSession;
import com.Client.model.response.UserDataDTO;
import com.Client.views.components.Header;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

@Route("/profile")
@PageTitle("Профиль пользователя")
@CssImport("./styles/profile_view_style.css")
@CssImport("./styles/header_style.css")
public class ProfileView extends VerticalLayout implements BeforeEnterObserver {
    private Div mainContainer = new Div();
    private Div userInfoContainer = new Div();
    private H2 usernameHeader = new H2();
    private H3 loginHeader = new H3();
    private H4 idHeader = new H4();
    private Button logoutButton = new Button("Выйти");
    private Paragraph rolesParagraph = new Paragraph();
    private Paragraph messageParagraph = new Paragraph();

    public ProfileView() {
        Header header = new Header(ProfileView.class);

        // Настройка стилей
        setSizeFull();
        getStyle().setBackgroundColor("#f0f2f5");
        mainContainer.setSizeFull();
        mainContainer.setClassName("profile-view");

        // Добавление контейнера с информацией о пользователе
        userInfoContainer.add(usernameHeader, loginHeader, idHeader, rolesParagraph);
        userInfoContainer.setClassName("user-info-container");
        logoutButton.setClassName("logout-button");
        logoutButton.addClickListener(e -> logout());
        mainContainer.add(userInfoContainer, logoutButton);
        add(header, mainContainer, messageParagraph);
    }

    private void logout() {
        UI.getCurrent().getSession().close();
        getUI().ifPresent(ui -> ui.navigate("/"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserDataDTO userData = UserSession.getUserData();
        if (userData == null) {
            event.rerouteTo("/login");
        }
        else {
            usernameHeader.setText(userData.name);
            loginHeader.setText("Логин: " + userData.username);
            idHeader.setText("ID: " + userData.id);
            rolesParagraph.setText("Роль: " + userData.role);
        }
    }
}
