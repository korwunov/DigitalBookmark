package com.Client.views;

import com.Client.model.UserSession;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

@Route("files")
@PageTitle("Файлы")
public class FilesView extends VerticalLayout implements BeforeEnterObserver {

    public FilesView() {
        add(new H1("Файлы"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (UserSession.getUserData() == null) {
            event.rerouteTo("/login");
        }
    }
}
