package com.Client.views;

import com.Client.model.UserSession;
import com.Client.model.response.GroupDTO;
import com.Client.model.response.UserDataDTO;
import com.Client.services.GroupsService;
import com.Client.services.UsersService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("users")
@PageTitle("Пользователи")
@CssImport("./styles/users_view_style.css")
public class UsersView extends VerticalLayout implements BeforeEnterObserver {

    private UsersService usersService;
    private GroupsService groupsService;


    private Grid<UserDataDTO> usersGrid = new Grid<>(UserDataDTO.class);
    private Dialog assignGroupDialog = new Dialog();
    private ComboBox<GroupDTO> groupComboBox = new ComboBox<>("Выберите группу");
    private Button assignGroupButton = new Button("Назначить");
    private Button confirmAssignGroupButton = new Button("Подтвердить");
    private Button cancelAssignGroupButton = new Button("Отмена");

    private ConfirmDialog confirmDialog = new ConfirmDialog();
    private Button confirmEnableButton = new Button("Подтвердить");
    private Button confirmDisableButton = new Button("Отмена");

    private Button confirmPromoteButton = new Button("Подтвердить");
    private Button confirmDemoteButton = new Button("Отмена");

    public UsersView(UsersService usersService, GroupsService groupsService) {
        this.usersService = usersService;
        this.groupsService = groupsService;

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

        assignGroupDialog.add(assignGroupDialogHeaderLayout);
        assignGroupDialog.add(groupComboBox);
        assignGroupDialog.add(assignGroupButton);

        groupComboBox.setItemLabelGenerator(GroupDTO::getName);

        assignGroupButton.addClickListener(e -> assignGroup());

        assignGroupDialog.addClassName("assign-group-dialog");
        groupComboBox.addClassName("form-field");
        assignGroupButton.addClassName("form-field");

        // Настройка диалоговых окон для подтверждения действий
        confirmDialog.setHeader("Подтверждение");
        confirmDialog.setText("Вы уверены?");
        confirmDialog.setCancelable(true);
        confirmDialog.setConfirmText("Подтвердить");
        confirmDialog.setCancelText("Отмена");

//        confirmDialog.addConfirmListener(e -> {
//            if (confirmDialog.().contains("включить")) {
//                toggleEnableStatus(currentUserId, false);
//            } else if (confirmDialog.getText().contains("выключить")) {
//                toggleEnableStatus(currentUserId, true);
//            } else if (confirmDialog.getText().contains("выдать")) {
//                promoteToAdmin(currentUserId);
//            } else if (confirmDialog.getText().contains("отзыв")) {
//                demoteFromAdmin(currentUserId);
//            } else if (confirmDialog.getText().contains("открепить")) {
//                unassignGroup(currentUserId);
//            }
//        });

        confirmDialog.addCancelListener(e -> confirmDialog.close());

        // Настройка основного содержимого
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
        setClassName("users-view");

        add(new H2("Пользователи"), usersGrid);
    }

    private Long currentUserId;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (UserSession.getUserData() == null) {
            event.rerouteTo("/login");
        } else if (!UserSession.getUserRole().equals("ROLE_ADMIN")) {
            Notification.show("У вас недостаточно прав для просмотра пользователей.", 3000, Notification.Position.TOP_CENTER);
            event.rerouteTo("/profile");
        }
        else {
            refreshGrid();
        }
    }

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
        this.currentUserId = userId;
        confirmDialog.setText("Вы уверены, что хотите прикрепить пользователя к группе?");
        assignGroupDialog.open();
    }

    private void openUnassignGroupDialog(Long userId) {
        this.currentUserId = userId;
        confirmDialog.setText("Вы уверены, что хотите открепить пользователя от группы?");
        confirmDialog.open();
    }

    private void toggleEnableStatus(Long userId, boolean currentStatus) {
        this.currentUserId = userId;
        confirmDialog.setText(currentStatus ? "Вы уверены, что хотите выключить пользователя?" : "Вы уверены, что хотите включить пользователя?");
        confirmDialog.open();
    }

    private void promoteToAdmin(Long userId) {
        this.currentUserId = userId;
        confirmDialog.setText("Вы уверены, что хотите выдать права администратора?");
        confirmDialog.open();
    }

    private void demoteFromAdmin(Long userId) {
        this.currentUserId = userId;
        confirmDialog.setText("Вы уверены, что хотите отзывать права администратора?");
        confirmDialog.open();
    }

    private void assignGroup() {
        GroupDTO selectedGroup = groupComboBox.getValue();
        if (selectedGroup == null) {
            Notification.show("Выберите группу", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        try {
            //usersService.assignGroupToStudent(currentUserId, selectedGroup.getId());
            Notification.show("Группа назначена успешно", 3000, Notification.Position.TOP_CENTER);
            assignGroupDialog.close();
            refreshGrid();
        } catch (Exception e) {
            Notification.show("Ошибка при назначении группы", 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void unassignGroup(Long userId) {
        try {
            //usersService.unassignGroupFromStudent(userId);
            Notification.show("Группа откреплена успешно", 3000, Notification.Position.TOP_CENTER);
            refreshGrid();
        } catch (Exception e) {
            Notification.show("Ошибка при откреплении группы", 3000, Notification.Position.TOP_CENTER);
        }
    }


}
