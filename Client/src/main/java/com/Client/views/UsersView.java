package com.Client.views;

import com.Client.model.UserSession;
import com.Client.model.response.GroupDTO;
import com.Client.model.response.UserDataDTO;
import com.Client.services.AuthService;
import com.Client.services.GroupsService;
import com.Client.services.StudentsService;
import com.Client.services.UsersService;
import com.Client.views.components.Header;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Objects;

@Route("users")
@PageTitle("Пользователи")
@CssImport("./styles/users_view_style.css")
public class UsersView extends VerticalLayout implements BeforeEnterObserver {
    private StudentsService studentsService;
    private UsersService usersService;
    private GroupsService groupsService;
    private AuthService authService;

    private Main usersMainContainer = new Main();
    private Grid<UserDataDTO> usersGrid = new Grid<>(UserDataDTO.class);
    private Button createGroupButton = new Button("Добавить группу");
    private Dialog createGroupDialog = new Dialog();
    private TextField groupNameField = new TextField("Название группы");
    private Button confirmCreateGroupButton = new Button("Создать");
    private Dialog assignGroupDialog = new Dialog();
    private ComboBox<GroupDTO> groupComboBox = new ComboBox<>("Выберите группу");
    private Button assignGroupButton = new Button("Назначить");
    private ConfirmDialog confirmDialog = new ConfirmDialog();

    private Registration confirmUnassignGroupEvent;
    private Registration confirmPromoteToAdminEvent;
    private Registration confirmDemoteFromAdminEvent;
    private Registration confirmSetEnabledEvent;

    public UsersView(UsersService usersService, GroupsService groupsService, AuthService authService, StudentsService studentsService) {
        this.usersService = usersService;
        this.groupsService = groupsService;
        this.authService = authService;
        this.studentsService = studentsService;

        assignGroupDialog.removeAll();

        // Настройка грида
        usersGrid.removeAllColumns();
        Grid.Column<UserDataDTO> idColumn = usersGrid.addColumn(UserDataDTO::getId).setKey("userId").setHeader("ID").setWidth("25px");
        Grid.Column<UserDataDTO> nameColumn = usersGrid.addColumn(UserDataDTO::getName).setKey("userFullname").setHeader("Имя").setWidth("200px");
        Grid.Column<UserDataDTO> usernameColumn = usersGrid.addColumn(UserDataDTO::getUsername).setKey("userName").setHeader("Имя пользователя");
        Grid.Column<UserDataDTO> roleColumn = usersGrid.addColumn(UserDataDTO::getRoleString).setKey("userRole").setHeader("Роль");
        Grid.Column<UserDataDTO> groupColumn = usersGrid.addComponentColumn(user -> {
            if (user.getRole().equals("ROLE_STUDENT")) {
                return new Span(user.getGroup() != null ? user.getGroup().name : "Не назначена");
            }
            return new Span("");
        }).setKey("userGroup").setHeader("Группа");

        Grid.Column<UserDataDTO> statusColumn = usersGrid.addComponentColumn(user -> {
            Icon icon = user.isEnabled() ? new Icon(VaadinIcon.CHECK_CIRCLE) : new Icon(VaadinIcon.CLOSE_CIRCLE);
            icon.getElement().getStyle().set("cursor", "pointer");
            icon.setColor(user.isEnabled() ? "green" : "red");
            icon.addClickListener(e -> toggleEnableStatus(user.getId(), user.isEnabled()));
            return icon;
        }).setKey("userStatus").setHeader("Статус").setWidth("50px");

        Grid.Column<UserDataDTO> actionsColumn = usersGrid.addComponentColumn(user -> {
            HorizontalLayout actionsLayout = new HorizontalLayout();

            switch (user.getRole()) {
                case "ROLE_TEACHER" -> {
                    Button promoteButton = new Button("Выдать права администратора");
                    promoteButton.addClickListener(e -> promoteToAdmin(user.getId()));
                    promoteButton.getElement().getStyle().set("color", "#007bff").set("cursor", "pointer");
                    promoteButton.addClassName("promote-button");
                    actionsLayout.add(promoteButton);
                }
                case "ROLE_ADMIN" -> {
                    if (!UserSession.getUserData().id.equals(user.id)) {
                        Button demoteButton = new Button("Отзыв прав администратора");
                        demoteButton.addClickListener(e -> demoteFromAdmin(user.getId()));
                        demoteButton.getElement().getStyle().set("color", "#007bff").set("cursor", "pointer");
                        demoteButton.addClassName("demote-button");
                        actionsLayout.add(demoteButton);
                    }
                    else {
                        actionsLayout.add(new Span("Текущий пользователь"));
                    }

                }
                case "ROLE_STUDENT" -> {
                    Button assignGroupButton = new Button("Назначить группу");
                    Button unassignGroupButton = new Button("Открепить от группы");

                    if (user.getGroup() != null) {
                        unassignGroupButton.addClickListener(e -> openUnassignGroupDialog(user.getId()));
                        unassignGroupButton.getElement().getStyle().set("color", "#007bff").set("cursor", "pointer");
                        unassignGroupButton.addClassName("unassign-group-button");
                        actionsLayout.add(unassignGroupButton);
                    } else {
                        assignGroupButton.addClickListener(e -> openAssignGroupDialog(user.getId()));
                        assignGroupButton.getElement().getStyle().set("color", "#007bff").set("cursor", "pointer");
                        assignGroupButton.addClassName("assign-group-button");
                        actionsLayout.add(assignGroupButton);
                    }
                }
            }

            actionsLayout.setSpacing(true);
            return actionsLayout;
        }).setKey("actions").setHeader("Действия");

        // Настройка модального окна для назначения группы
        H2 assignGroupDialogTitle = new H2("Форма для назначения группы");
        Icon assignGroupDialogCloseButton = new Icon(VaadinIcon.CLOSE_SMALL);
        assignGroupDialogCloseButton.getElement().getStyle().set("cursor", "pointer");
        assignGroupDialogCloseButton.addClickListener(e -> assignGroupDialog.close());
        HorizontalLayout assignGroupDialogHeaderLayout = new HorizontalLayout(assignGroupDialogTitle, assignGroupDialogCloseButton);
        assignGroupDialogHeaderLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        assignGroupDialogHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        assignGroupDialogHeaderLayout.setWidthFull();
        assignGroupDialogHeaderLayout.setPadding(true);
        assignGroupDialogHeaderLayout.addClassName("dialog-header");
        assignGroupDialog.add(assignGroupDialogHeaderLayout, groupComboBox, assignGroupButton);
        groupComboBox.setItemLabelGenerator(GroupDTO::getName);
//        assignGroupDialog.addClassName("assign-group-dialog");
        groupComboBox.addClassName("form-field");
        assignGroupButton.addClassName("form-field");

        // Настройка модального окна для создания группы
        createGroupButton.addClickListener(e -> createGroupDialog.open());
        Icon createGroupDialogCloseButton = new Icon(VaadinIcon.CLOSE_SMALL);
        createGroupDialogCloseButton.getElement().getStyle().set("cursor", "pointer");
        createGroupDialogCloseButton.addClickListener(e -> createGroupDialog.close());
        HorizontalLayout createGroupDialogHeaderLayout = new HorizontalLayout(new H2("Форма для создания группы"), createGroupDialogCloseButton);
        createGroupDialogHeaderLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        createGroupDialogHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        createGroupDialogHeaderLayout.setWidthFull();
        createGroupDialogHeaderLayout.setPadding(true);
        createGroupDialogHeaderLayout.addClassName("dialog-header");
        createGroupDialog.add(createGroupDialogHeaderLayout, groupNameField, confirmCreateGroupButton);
//        createGroupDialog.addClassName("assign-group-dialog");
        groupNameField.addClassName("form-field");
        confirmCreateGroupButton.addClassName("form-field");
        confirmCreateGroupButton.addClickListener(e -> createGroup());

        // Настройка диалоговых окон для подтверждения действий
        confirmDialog.setHeader("Подтверждение");
        confirmDialog.setText("Вы уверены?");
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Подтвердить");
        confirmDialog.setCancelText("Отмена");

        confirmDialog.addCancelListener(e -> confirmDialog.close());

        // Настройка основного содержимого
        Div mainContainerHeader = new Div();
        createGroupButton.addClassName("add-group-button");
        mainContainerHeader.add(new H2("Пользователи"), createGroupButton);
        mainContainerHeader.setClassName("grid-header");
        usersMainContainer.setSizeFull();
//        usersMainContainer.setClassName("users-view");
        usersMainContainer.add(mainContainerHeader, usersGrid);
        setHeightFull();
        addClassName("background-style");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (UserSession.getUserData() == null) {
            event.rerouteTo("/login");
        } else if (!UserSession.getUserRole().equals("ROLE_ADMIN")) {
            Notification.show("У вас недостаточно прав для просмотра пользователей.", 3000, Notification.Position.TOP_CENTER);
            event.rerouteTo("/profile");
        }
        else {
            Header header = new Header(UsersView.class, UserSession.getUserRole());
            add(header, usersMainContainer);
            refreshGrid();
        }
    }

    private Long userId;

    private void refreshGrid() {
        // Отображаем грид для администраторов
        List<UserDataDTO> allUsers = usersService.getAllUsers();
        usersGrid.setDataProvider(new ListDataProvider<>(allUsers));
        // Заполняем выпадающие списки для администраторов
        loadGroups();
    }


    private void loadGroups() {
        List<GroupDTO> groups = groupsService.getAllGroups();
        groupComboBox.setItems(groups);
    }

    private void openAssignGroupDialog(Long userId) {
        this.userId = userId;
        assignGroupButton.addClickListener(e -> assignGroup());
        assignGroupDialog.open();
    }

    private void openUnassignGroupDialog(Long userId) {
        this.userId = userId;
        confirmDialog.setText("Вы уверены, что хотите открепить пользователя от группы?");
        removeAllConfirmEvents();
        confirmUnassignGroupEvent = confirmDialog.addConfirmListener(e -> unassignGroup());
        confirmDialog.open();
    }

    private void toggleEnableStatus(Long userId, boolean currentStatus) {
        this.userId = userId;
        confirmDialog.setText(currentStatus ? "Вы уверены, что хотите выключить пользователя?" : "Вы уверены, что хотите включить пользователя?");
        removeAllConfirmEvents();
        confirmSetEnabledEvent = confirmDialog.addConfirmListener(e -> setUserEnabled(!currentStatus));
        confirmDialog.open();
    }

    private void promoteToAdmin(Long userId) {
        this.userId = userId;
        confirmDialog.setText("Вы уверены, что хотите выдать права администратора?");
        removeAllConfirmEvents();
        confirmPromoteToAdminEvent = confirmDialog.addConfirmListener(e -> setRole("ROLE_ADMIN"));
        confirmDialog.open();
    }

    private void demoteFromAdmin(Long userId) {
        this.userId = userId;
        confirmDialog.setText("Вы уверены, что хотите отзывать права администратора?");
        removeAllConfirmEvents();
        confirmDemoteFromAdminEvent = confirmDialog.addConfirmListener(e -> setRole("ROLE_TEACHER"));
        confirmDialog.open();
    }

    private void createGroup() {
        String name = groupNameField.getValue();
        if (name == null) {
            Notification.show("Введите название группы", 3000, Notification.Position.TOP_CENTER);
        } else {
            try {
                studentsService.createGroup(name);
                Notification.show("Группа создана успешно", 3000, Notification.Position.TOP_CENTER);
                createGroupDialog.close();
                refreshGrid();
            } catch (Exception e) {
                Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        }
    }

    private void assignGroup() {
        GroupDTO selectedGroup = groupComboBox.getValue();
        if (selectedGroup == null) {
            Notification.show("Выберите группу", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        try {
            usersService.assignGroupToStudent(this.userId, selectedGroup.getId());
            Notification.show("Группа назначена успешно", 3000, Notification.Position.TOP_CENTER);
            assignGroupDialog.close();
            groupComboBox.clear();
            refreshGrid();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void unassignGroup() {
        try {
            usersService.unassignGroupFromStudent(this.userId);
            Notification.show("Группа откреплена успешно", 3000, Notification.Position.TOP_CENTER);
            confirmDialog.close();
            refreshGrid();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void setUserEnabled(boolean targetEnable) {
        try {
            authService.setUserEnabled(this.userId, targetEnable);
            Notification.show(targetEnable ? "Пользователь подключен успешно" : "Пользователь отключен успешно", 3000, Notification.Position.TOP_CENTER);
            confirmDialog.close();
            refreshGrid();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void setRole(String role) {
        try {
            usersService.setRole(this.userId, role);
            Notification.show(Objects.equals(role, "ROLE_TEACHER") ? "Права администратора успешно отозваны" : "Права администратора успешно подключены", 3000, Notification.Position.TOP_CENTER);
            confirmDialog.close();
            refreshGrid();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void removeAllConfirmEvents() {
        if (Objects.nonNull(this.confirmSetEnabledEvent)) this.confirmSetEnabledEvent.remove();
        if (Objects.nonNull(this.confirmUnassignGroupEvent)) this.confirmUnassignGroupEvent.remove();
        if (Objects.nonNull(this.confirmDemoteFromAdminEvent)) this.confirmDemoteFromAdminEvent.remove();
        if (Objects.nonNull(this.confirmPromoteToAdminEvent)) this.confirmPromoteToAdminEvent.remove();
    }
}
