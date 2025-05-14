package com.Client.model;

import com.Client.model.response.UserDataDTO;
import com.vaadin.flow.component.UI;
import lombok.Data;

@Data
public class UserSession {
    private String authToken;
    private UserDataDTO userData;

    public static UserDataDTO getUserData() {
        UserSession session = (UserSession) UI.getCurrent().getSession().getAttribute("userSession");
        if (session != null) {
            UserDataDTO userData = session.userData;
            return userData;
        }
        return null;
    }

    public static String getUserRole() {
        UserSession session = (UserSession) UI.getCurrent().getSession().getAttribute("userSession");
        if (session != null) {
            return session.userData.role;
        }
        return null;
    }

    public static String getUserToken() {
        return ((UserSession) UI.getCurrent().getSession().getAttribute("userSession")).getAuthToken();
    }
}
