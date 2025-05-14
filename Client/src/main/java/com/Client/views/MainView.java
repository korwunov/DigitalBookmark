package com.Client.views;

import com.Client.model.UserSession;
import com.Client.views.components.Header;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;

@Route(value = "")
@RouteAlias(value = "main")
@PageTitle("Main")
@CssImport("./styles/main_view_style.css")
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private Image logo = new Image("images/logo.png", "Logo");
    private H1 title = new H1("Электронная зачетная книжка");
    private Button loginButton = new Button("Авторизоваться");
    private Button registerButton = new Button("Зарегистрироваться");

    public MainView() {

        // Настройка логотипа
        logo.setWidth("100px");
        logo.setHeight("100px");

        // Настройка кнопок
        loginButton.setWidthFull();
        registerButton.setWidthFull();

        // Добавление обработчиков событий для кнопок
        loginButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("/login")));
        registerButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("/registration")));

        // Создание основного содержимого
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        content.setSpacing(false);
        content.setPadding(false);

        Div logoContainer = new Div(logo);
        logoContainer.setClassName("logo-container");

        Div titleContainer = new Div(title);
        titleContainer.setClassName("title-container");

        HorizontalLayout buttonsLayout = new HorizontalLayout(loginButton, registerButton);
        buttonsLayout.setSpacing(true);
        buttonsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        buttonsLayout.setClassName("buttons-layout");

        content.add(logoContainer, titleContainer, buttonsLayout);

        // Добавление основного содержимого в основной Layout
        add(content);

        // Центрирование основного содержимого
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        setAlignItems(FlexComponent.Alignment.STRETCH);
        setSizeFull();
        setClassName("main-view");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (UserSession.getUserData() != null) {
            event.rerouteTo("/grades");
        }
    }
}
