package com.Client.views;

import com.Client.model.UserSession;
import com.Client.model.request.MarkDTO;
import com.Client.model.response.GroupDTO;
import com.Client.model.response.MarksDataDTO;
import com.Client.model.response.StudentDTO;
import com.Client.model.response.SubjectDTO;
import com.Client.services.GroupsService;
import com.Client.services.MarksService;
import com.Client.services.StudentsService;
import com.Client.services.SubjectsService;
import com.Client.views.components.Header;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Objects;

@Route("grades")
@PageTitle("Оценки")
@CssImport("./styles/marks_view_style.css")
public class GradesView extends VerticalLayout implements BeforeEnterObserver {
    private MarksService marksService;
    private GroupsService groupsService;
    private SubjectsService subjectsService;
    private StudentsService studentsService;

    private Div mainContainer = new Div();
    private Grid<MarksDataDTO> gradesGrid = new Grid<>(MarksDataDTO.class);
    private Button addGradeButton = new Button("Добавить оценку");
    private Dialog addGradeDialog = new Dialog();
    private ComboBox<SubjectDTO> subjectComboBox = new ComboBox<>("Предмет");
    private ComboBox<GroupDTO> groupComboBox = new ComboBox<>("Группа");
    private ComboBox<StudentDTO> studentComboBox = new ComboBox<>("ФИО студента");
    private ComboBox<Double> gradeComboBox = new ComboBox<>("Оценка");
    private Button saveGradeButton = new Button("Сохранить");

    public GradesView(MarksService marksService, GroupsService groupsService, SubjectsService subjectsService, StudentsService studentsService) {
        this.marksService = marksService;
        this.groupsService = groupsService;
        this.subjectsService = subjectsService;
        this.studentsService = studentsService;

        getStyle().setBackgroundColor("#f0f2f5");
        gradesGrid.getColumnByKey("subjectName").setHeader("Предмет");
        gradesGrid.getColumnByKey("ownerGroup").setHeader("Группа студента");
        gradesGrid.getColumnByKey("ownerName").setHeader("ФИО студента");
        gradesGrid.getColumnByKey("markValue").setHeader("Оценка");
        gradesGrid.getColumnByKey("markDate").setHeader("Дата");
        gradesGrid.getColumnByKey("giverName").setHeader("Преподаватель");

        // Настройка модального окна для добавления оценки
        H2 dialogTitle = new H2("Добавление оценки");
        Icon closeButton = new Icon(VaadinIcon.CLOSE_SMALL);
        closeButton.getElement().getStyle().set("cursor", "pointer");
        closeButton.addClickListener(e -> addGradeDialog.close());
        HorizontalLayout headerLayout = new HorizontalLayout(dialogTitle, closeButton);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setWidthFull();
        headerLayout.setPadding(true);
        headerLayout.addClassName("dialog-header");

        FormLayout addGradeForm = new FormLayout();
        addGradeForm.add(subjectComboBox, groupComboBox, studentComboBox, gradeComboBox);
        addGradeForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("300px", 2));
        addGradeForm.setColspan(subjectComboBox, 1);
        addGradeForm.setColspan(groupComboBox, 1);
        addGradeForm.setColspan(studentComboBox, 2);
        addGradeForm.setColspan(gradeComboBox, 1);
        addGradeDialog.add(headerLayout, addGradeForm, saveGradeButton);

        subjectComboBox.setItemLabelGenerator(SubjectDTO::getName);
        groupComboBox.setItemLabelGenerator(GroupDTO::getName);
        studentComboBox.setItemLabelGenerator(StudentDTO::getName);
        gradeComboBox.setItems(2.0, 3.0, 4.0, 5.0);

        saveGradeButton.addClickListener(e -> saveMark());

        // Настройка основного содержимого
        mainContainer.setSizeFull();
        mainContainer.setClassName("grades-view");

        addGradeDialog.addClassName("add-grade-dialog");
        subjectComboBox.addClassName("form-field");
        studentComboBox.addClassName("form-field");
        groupComboBox.addClassName("form-field");
        gradeComboBox.addClassName("form-field");
        saveGradeButton.addClassName("form-field");

        groupComboBox.addValueChangeListener(event -> {
            GroupDTO selectedGroup = event.getValue();
            if (selectedGroup != null && subjectComboBox.getValue() != null) {
                loadStudentsByGroupAndSubject(selectedGroup.getId(), subjectComboBox.getValue().id);
            }
        });

        subjectComboBox.addValueChangeListener(event -> {
            SubjectDTO selectedSubject = event.getValue();
            if (selectedSubject != null && groupComboBox.getValue() != null) {
                loadStudentsByGroupAndSubject(groupComboBox.getValue().getId(), selectedSubject.getId());
            }
        });

        mainContainer.add(gradesGrid);
        Header header = new Header(GradesView.class);
        add(header, mainContainer);
    }

    private void saveMark() {
        SubjectDTO selectedSubject = subjectComboBox.getValue();
        GroupDTO selectedGroup = groupComboBox.getValue();
        StudentDTO selectedStudent = studentComboBox.getValue();
        Double selectedGrade = gradeComboBox.getValue();

        if (selectedSubject == null || selectedGroup == null || selectedStudent == null || selectedGrade == null) {
            Notification.show("Заполните все поля", 3000, Notification.Position.TOP_CENTER);
            return;
        }

        try {
            marksService.setMark(new MarkDTO(selectedStudent.getId(), selectedSubject.getId(), selectedGrade.intValue()));
            Notification.show("Оценка добавлена успешно", 3000, Notification.Position.TOP_CENTER);
            addGradeDialog.close();
            refreshGrid();

        } catch (RestClientException ex) {
            Notification.show(ex.getMessage(), 5000, Notification.Position.TOP_CENTER);
        }
    }

    private void refreshGrid() {
        String role = UserSession.getUserRole();
        try {
            if (Objects.equals(role, "ROLE_STUDENT")) {
                // Установка источника данных для грида
                List<MarksDataDTO> studentGrades = marksService.getMarks("students");
                gradesGrid.setDataProvider(new ListDataProvider<>(studentGrades));
                // Удаление колонок, не предназначенных для данной роли
                if (Objects.nonNull(gradesGrid.getColumnByKey("ownerName"))) gradesGrid.removeColumn(gradesGrid.getColumnByKey("ownerName"));
                if (Objects.nonNull(gradesGrid.getColumnByKey("ownerGroup"))) gradesGrid.removeColumn(gradesGrid.getColumnByKey("ownerGroup"));
                if (Objects.nonNull(gradesGrid.getColumnByKey("markId"))) gradesGrid.removeColumn(gradesGrid.getColumnByKey("markId"));
                // Скрытие кнопки для выставления оценки
                addGradeButton.setVisible(false);
            }
            else if (Objects.equals(role, "ROLE_TEACHER")) {
                List<MarksDataDTO> grades = marksService.getMarks("teachers");
                gradesGrid.setDataProvider(new ListDataProvider<>(grades));
                if (Objects.nonNull(gradesGrid.getColumnByKey("giverName"))) gradesGrid.removeColumn(gradesGrid.getColumnByKey("giverName"));
                if (Objects.nonNull(gradesGrid.getColumnByKey("markId"))) gradesGrid.removeColumn(gradesGrid.getColumnByKey("markId"));
                // Добавление кнопки и модального окна для оценивания
                add(addGradeButton, addGradeDialog);
                addGradeButton.addClickListener(e -> addGradeDialog.open());
                groupComboBox.setItems(groupsService.getAllGroups());
                subjectComboBox.setItems(subjectsService.getTeachersAvailableSubjects());
            } else if (Objects.equals(role, "ROLE_ADMIN")) {
                List<MarksDataDTO> grades = marksService.getMarks("teachers");
                gradesGrid.setDataProvider(new ListDataProvider<>(grades));
                add(addGradeButton, addGradeDialog);
                addGradeButton.addClickListener(e -> addGradeDialog.open());
                groupComboBox.setItems(groupsService.getAllGroups());
                subjectComboBox.setItems(subjectsService.getTeachersAvailableSubjects());
            }

        } catch (RestClientException ex) {
            Notification.show(ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
        }
    }

    private void loadStudentsByGroupAndSubject(Long groupId, Long subjectId) {
        if (groupId != null && subjectId != null) {
            List<StudentDTO> students = studentsService.getStudentsByGroupAndSubject(groupId, subjectId);
            studentComboBox.setItems(students);
        } else {
            studentComboBox.clear();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (UserSession.getUserData() == null) {
            event.rerouteTo("/login");
        }
        else {
            refreshGrid();
        }
    }
}