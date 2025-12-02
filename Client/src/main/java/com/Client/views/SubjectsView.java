package com.Client.views;
import com.Client.model.UserSession;
import com.Client.model.request.AssignSubjectDTO;
import com.Client.model.request.CreateSubjectDTO;
import com.Client.model.response.StudentDTO;
import com.Client.model.response.SubjectDTO;
import com.Client.model.response.TeacherDTO;
import com.Client.services.GroupsService;
import com.Client.services.StudentsService;
import com.Client.services.SubjectsService;
import com.Client.services.TeachersService;
import com.Client.views.components.Header;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
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
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

import java.util.List;
import java.util.Objects;

@Route("subjects")
@PageTitle("Предметы")
@CssImport("./styles/subjects_view_style.css")
public class SubjectsView extends VerticalLayout implements BeforeEnterObserver {

    private SubjectsService subjectsService;
    private GroupsService groupsService;
    private StudentsService studentsService;
    private TeachersService teachersService;

    private Div mainContainer = new Div();
    private Grid<SubjectDTO> subjectsGrid = new Grid<>(SubjectDTO.class);
    private Button addSubjectButton = new Button("Добавить предмет");
    private Dialog addSubjectDialog = new Dialog();
    private TextField subjectNameField = new TextField("Название предмета");
    private Button saveSubjectButton = new Button("Сохранить");

    private Dialog connectSubjectDialog = new Dialog();
    private ComboBox<String> roleComboBox = new ComboBox<>("Выберите роль");
    private ComboBox<StudentDTO> studentComboBox = new ComboBox<>("Выберите студента");
    private ComboBox<TeacherDTO> teacherComboBox = new ComboBox<>("Выберите преподавателя");
    private Button connectSubjectButton = new Button("Подключить");
    private H2 connectDialogTitle = new H2();

    public SubjectsView(SubjectsService subjectsService, GroupsService groupsService, StudentsService studentsService, TeachersService teachersService) {
        this.subjectsService = subjectsService;
        this.groupsService = groupsService;
        this.studentsService = studentsService;
        this.teachersService = teachersService;

        /**
         *  Настройка формы для добавления предмета
         */
        H2 dialogTitle = new H2("Форма для добавления предмета");
        Icon closeButton = new Icon(VaadinIcon.CLOSE_SMALL);
        closeButton.getElement().getStyle().set("cursor", "pointer");
        closeButton.addClickListener(e -> addSubjectDialog.close());

        HorizontalLayout headerLayout = new HorizontalLayout(dialogTitle, closeButton);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setWidthFull();
        headerLayout.setPadding(true);
        headerLayout.addClassName("dialog-header");

        addSubjectDialog.add(headerLayout);
        addSubjectDialog.add(subjectNameField);
        addSubjectDialog.add(saveSubjectButton);
        subjectNameField.setPlaceholder("Введите название предмета");
        saveSubjectButton.addClickListener(e -> saveSubject());

        /**
         *  Настройка формы для подключения
         */
        Icon connectDialogCloseButton = new Icon(VaadinIcon.CLOSE_SMALL);
        connectDialogCloseButton.getElement().getStyle().set("cursor", "pointer");
        connectDialogCloseButton.addClickListener(e -> connectSubjectDialog.close());

        HorizontalLayout connectDialogHeaderLayout = new HorizontalLayout(connectDialogTitle, connectDialogCloseButton);
        connectDialogHeaderLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        connectDialogHeaderLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        connectDialogHeaderLayout.setWidthFull();
        connectDialogHeaderLayout.setPadding(true);
        connectDialogHeaderLayout.addClassName("dialog-header");
        teacherComboBox.setVisible(false);
        studentComboBox.setVisible(false);

        connectSubjectDialog.add(connectDialogHeaderLayout, roleComboBox, studentComboBox, teacherComboBox, connectSubjectButton);

        roleComboBox.setItems("Студент", "Преподаватель");
        roleComboBox.addValueChangeListener(event -> {
            String selectedRole = event.getValue();
            if ("Студент".equals(selectedRole)) {
                teacherComboBox.setVisible(false);
                List<StudentDTO> students = studentsService.getAllStudents();
                studentComboBox.setItems(students);
                studentComboBox.setItemLabelGenerator(StudentDTO::getName);
                studentComboBox.setVisible(true);
            } else if ("Преподаватель".equals(selectedRole)) {
                studentComboBox.setVisible(false);
                List<TeacherDTO> teachers = teachersService.getAllTeachers();
                teacherComboBox.setItems(teachers);
                teacherComboBox.setItemLabelGenerator(TeacherDTO::getName);
                teacherComboBox.setVisible(true);
            }
        });

        /**
         *  Настройка основного содержимого
         */
        subjectsGrid.getColumnByKey("id").setAutoWidth(true);
        subjectsGrid.addColumn(SubjectDTO::getName).setKey("subjectsNames").setHeader("Предмет").setAutoWidth(true);
        subjectsGrid.addColumn(subject -> String.join(", ", subject.getStudentsNames())).setKey("originStudentsNames").setHeader("Студенты").setAutoWidth(true);
        subjectsGrid.addColumn(subject -> String.join(", \n", subject.getTeachersNames())).setKey("originTeachersNames").setHeader("Преподаватели").setAutoWidth(true);

        subjectsGrid.addComponentColumn(subject -> {
            VerticalLayout actionsLayout = new VerticalLayout();

            Button deleteButton = new Button("Удалить");
            deleteButton.addClickListener(e -> deleteSubject(subject.getId()));
            deleteButton.getElement().getStyle().set("color", "red").set("cursor", "pointer");
            deleteButton.addClassName("delete-button");

            Button connectButton = new Button("Подключить");
            connectButton.addClickListener(e -> openConnectSubjectDialog(subject.getId(), "assign"));
            connectButton.getElement().getStyle().set("color", "#007bff").set("cursor", "pointer");
            connectButton.addClassName("connect-button");

            Button disconnectButton = new Button("Отключить");
            disconnectButton.addClickListener(e -> openConnectSubjectDialog(subject.getId(), "unassign"));
            disconnectButton.getElement().getStyle().set("color", "#007bff").set("cursor", "pointer");
            disconnectButton.addClassName("connect-button");

            actionsLayout.add(connectButton, disconnectButton, deleteButton);
            actionsLayout.setSpacing(true);
            return actionsLayout;
        }).setKey("actions").setHeader("Действия");
        mainContainer.setClassName("subjects-view");

//        addSubjectDialog.addClassName("add-subject-dialog");
        subjectNameField.addClassName("form-field");
        saveSubjectButton.addClassName("form-field");

        addSubjectButton.addClassName("add-subject-button");
        addSubjectButton.addClickListener(e -> addSubjectDialog.open());
        addSubjectButton.setVisible(false);
        Div mainContainerHeader = new Div();
        mainContainerHeader.add(new H2("Предметы"), addSubjectButton);
        mainContainerHeader.addClassName("grid-header");
        mainContainer.add(mainContainerHeader, subjectsGrid, addSubjectDialog);
        setSizeFull();
        addClassName("background-style");
    }

    private void saveSubject() {
        String subjectName = subjectNameField.getValue();
        if (subjectName.isEmpty()) {
            Notification.show("Заполните все поля", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        CreateSubjectDTO subjectDTO = new CreateSubjectDTO(subjectName);
        try {
            subjectsService.createSubject(subjectDTO);
            Notification.show("Предмет добавлен успешно", 3000, Notification.Position.TOP_CENTER);
            addSubjectDialog.close();
            refreshGrid();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void manageSubject(Long subjectId, String action) {
        String pickedRole = roleComboBox.getValue();
        Long userId;
        if ("Студент".equals(pickedRole)) {
            userId = studentComboBox.getValue().getId();
        } else {
            userId = teacherComboBox.getValue().getId();
        }
        try {
            subjectsService.assignSubject("Студент".equals(pickedRole) ? "ROLE_STUDENT" : "ROLE_TEACHER", new AssignSubjectDTO(userId, List.of(subjectId)), action);
            Notification.show("Предмет " + (action.equals("assign") ? "подключен" : "отключен") + " успешно", 3000, Notification.Position.TOP_CENTER);
            connectSubjectDialog.close();
            refreshGrid();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }


    }

    private void deleteSubject(Long subjectId) {
        if (subjectId == null) {
            Notification.show("Не удалось определить предмет для удаления", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        try {
            subjectsService.deleteSubject(subjectId);
            Notification.show("Предмет удален успешно", 3000, Notification.Position.TOP_CENTER);
            refreshGrid();
        } catch (Exception e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void openConnectSubjectDialog(Long id, String action) {
        connectDialogTitle.setText(("assign".equals(action) ? "Подключение" : "Отключение") + " предмета");
        connectSubjectButton.setText(("assign".equals(action) ? "Подключить" : "Отключить"));
        connectSubjectDialog.open();
        connectSubjectButton.addClickListener(e -> manageSubject(id, action));
    }

    private void refreshGrid() {
        String role = UserSession.getUserRole();
        if (Objects.equals(role, "ROLE_STUDENT")) {
            // Отображаем грид для студентов
            List<SubjectDTO> studentSubjects = subjectsService.getAllUserSubjects();
            subjectsGrid.setDataProvider(new ListDataProvider<>(studentSubjects));
            if (Objects.nonNull(subjectsGrid.getColumnByKey("originStudentsNames"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("originStudentsNames"));
            if (Objects.nonNull(subjectsGrid.getColumnByKey("id"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("id"));
            if (Objects.nonNull(subjectsGrid.getColumnByKey("actions"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("actions"));
        } else if (Objects.equals(role, "ROLE_TEACHER")) {
            // Отображаем грид для преподавателей
            List<SubjectDTO> teacherSubjects = subjectsService.getAllUserSubjects();
            subjectsGrid.setDataProvider(new ListDataProvider<>(teacherSubjects));
            if (Objects.nonNull(subjectsGrid.getColumnByKey("originTeachersNames"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("originTeachersNames"));
            if (Objects.nonNull(subjectsGrid.getColumnByKey("id"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("id"));
            if (Objects.nonNull(subjectsGrid.getColumnByKey("actions"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("actions"));
        } else if (Objects.equals(role, "ROLE_ADMIN")) {
            // Отображаем грид для администраторов
            List<SubjectDTO> allSubjects = subjectsService.getAllUserSubjects();
            subjectsGrid.setDataProvider(new ListDataProvider<>(allSubjects));
            addSubjectButton.setVisible(true);
        }
        if (Objects.nonNull(subjectsGrid.getColumnByKey("studentsNames"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("studentsNames"));
        if (Objects.nonNull(subjectsGrid.getColumnByKey("teachersNames"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("teachersNames"));
        if (Objects.nonNull(subjectsGrid.getColumnByKey("teachers"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("teachers"));
        if (Objects.nonNull(subjectsGrid.getColumnByKey("students"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("students"));
        if (Objects.nonNull(subjectsGrid.getColumnByKey("name"))) subjectsGrid.removeColumn(subjectsGrid.getColumnByKey("name"));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (UserSession.getUserData() == null) {
            event.rerouteTo("/login");
        }
        else {
            add(new Header(SubjectsView.class, UserSession.getUserRole()), mainContainer);
            refreshGrid();
        }
    }
}
