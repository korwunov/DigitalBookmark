package com.Client.utils;

import com.Client.model.UserSession;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.WebStorage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.concurrent.ExecutionException;

public class HttpRequestEntity {
    public static HttpEntity<Void> getRequestEntity() {
        UserSession userSession = (UserSession) UI.getCurrent().getSession().getAttribute("userSession");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", userSession.getAuthToken());
        return new HttpEntity<>(headers);
    }

}
