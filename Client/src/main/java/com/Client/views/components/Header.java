package com.Client.views.components;

import com.Client.views.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

@CssImport("./styles/header_style.css")
public class Header extends HorizontalLayout {
    public Header(Class<?> view, String role) {
        // Создание хедера с ссылками
        //Сделать недоступным переход в активный раздел и сохранение оценки

        setWidthFull();
        setPadding(true);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        setClassName("header");

        RouterLink gradesLink = new RouterLink("Оценки", GradesView.class);
        RouterLink subjectsLink = new RouterLink("Предметы", SubjectsView.class);
        RouterLink filesLink = new RouterLink("Файлы", FilesView.class);
        RouterLink profileLink = new RouterLink("Профиль", ProfileView.class);
        RouterLink usersLink = new RouterLink("Пользователи", UsersView.class);

        gradesLink.getStyle().set("color", view == GradesView.class ? "#007bff" : "white").setBackgroundColor(view == GradesView.class ? "white" : "inherit").set("margin-right", "20px").set("text-decoration", "none").set("font-weight", "bold");
        subjectsLink.getStyle().set("color", view == SubjectsView.class ? "#007bff" : "white").setBackgroundColor(view == SubjectsView.class ? "white" : "inherit").set("margin-right", "20px").set("text-decoration", "none").set("font-weight", "bold");
        filesLink.getStyle().set("color", view == FilesView.class ? "#007bff" : "white").setBackgroundColor(view == FilesView.class ? "white" : "inherit").set("margin-right", "20px").set("text-decoration", "none").set("font-weight", "bold");
        profileLink.getStyle().set("color", view == ProfileView.class ? "#007bff" : "white").setBackgroundColor(view == ProfileView.class ? "white" : "inherit").set("margin-right", "20px").set("text-decoration", "none").set("font-weight", "bold");
        usersLink.getStyle().set("color", view == UsersView.class ? "#007bff" : "white").setBackgroundColor(view == UsersView.class ? "white" : "inherit").set("margin-right", "20px").set("text-decoration", "none").set("font-weight", "bold");

        gradesLink.setRoute(GradesView.class);
        subjectsLink.setRoute(SubjectsView.class);
        filesLink.setRoute(FilesView.class);
        profileLink.setRoute(ProfileView.class);

        add(gradesLink, subjectsLink, filesLink, profileLink);
    }
}
