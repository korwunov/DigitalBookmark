import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Subject;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.repositories.StudentRepository;
import com.BookmarkService.repositories.SubjectRepository;
import com.BookmarkService.repositories.TeacherRepository;
import com.BookmarkService.services.SubjectService;
import com.BookmarkService.web.dto.SubjectDTO;
import com.BookmarkService.web.dto.SubjectsToAddDTO;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    private SubjectService subjectService;

    private Subject mockSubject;
    private Teacher mockTeacher;
    private Student mockStudent;
    private SubjectDTO subjectDTO;
    private SubjectsToAddDTO subjectsToAddDTO;

    @BeforeEach
    void setUp() {
        // Создаем тестовые объекты
        mockSubject = new Subject();
        mockSubject.setId(1L);
        mockSubject.setName("Math");

        mockTeacher = new Teacher();
        mockTeacher.setId(101L);
        List<Subject> teacherSubjects = new ArrayList<>();
        mockTeacher.setTeacherSubjects(teacherSubjects);

        mockStudent = new Student();
        mockStudent.setId(201L);
        List<Subject> studentSubjects = new ArrayList<>();
        mockStudent.setStudentSubjects(studentSubjects);

        subjectDTO = new SubjectDTO();
        subjectDTO.setName("Physics");

        subjectsToAddDTO = new SubjectsToAddDTO();
        subjectsToAddDTO.setUserId(101L);
        subjectsToAddDTO.setSubjectIds(List.of(1L));

        // Создаем сервис, внедряя моки через reflection, как в предыдущем примере
        subjectService = new SubjectService();
        try {
            java.lang.reflect.Field subjectRepoField = subjectService.getClass().getDeclaredField("subjectRepository");
            subjectRepoField.setAccessible(true);
            subjectRepoField.set(subjectService, subjectRepository);

            java.lang.reflect.Field teacherRepoField = subjectService.getClass().getDeclaredField("teacherRepository");
            teacherRepoField.setAccessible(true);
            teacherRepoField.set(subjectService, teacherRepository);

            java.lang.reflect.Field studentRepoField = subjectService.getClass().getDeclaredField("studentRepository");
            studentRepoField.setAccessible(true);
            studentRepoField.set(subjectService, studentRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock repositories", e);
        }
    }

    @Test
    @DisplayName("addSubject: Добавляет предмет, если имя уникально")
    void addSubject_AddsSuccessfullyIfNameIsUnique() throws Exception {
        given(subjectRepository.findByName("Physics")).willReturn(Optional.empty());
        given(subjectRepository.save(any(Subject.class))).willAnswer(invocation -> invocation.getArgument(0));

        Subject result = subjectService.addSubject(subjectDTO);

        then(subjectRepository).should().save(any(Subject.class));
        assertThat(result.getName()).isEqualTo("Physics");
    }

    @Test
    @DisplayName("addSubject: Выбрасывает BadRequestException, если предмет с таким именем уже существует")
    void addSubject_ThrowsIfNameExists() {
        given(subjectRepository.findByName("Physics")).willReturn(Optional.of(mockSubject));

        assertThatThrownBy(() -> subjectService.addSubject(subjectDTO))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("subject already exist");
    }

    @Test
    @DisplayName("getAllSubjects: Возвращает все предметы")
    void getAllSubjects_ReturnsAll() {
        List<Subject> expectedSubjects = List.of(mockSubject);
        given(subjectRepository.findAll()).willReturn(expectedSubjects);

        List<Subject> result = subjectService.getAllSubjects();

        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expectedSubjects);
    }

    @Test
    @DisplayName("getSubjectById: Возвращает предмет по ID")
    void getSubjectById_ReturnsSubject() throws Exception {
        given(subjectRepository.findById(1L)).willReturn(Optional.of(mockSubject));

        Subject result = subjectService.getSubjectById(1L);

        assertThat(result).isEqualTo(mockSubject);
    }

    @Test
    @DisplayName("getSubjectById: Выбрасывает исключение, если предмет не найден")
    void getSubjectById_ThrowsIfNotFound() {
        given(subjectRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.getSubjectById(1L))
                .isInstanceOf(Exception.class)
                .hasMessage("subject with id 1 not found");
    }

    @Test
    @DisplayName("deleteSubject: Удаляет предмет по ID")
    void deleteSubject_DeletesSuccessfully() throws Exception {
        given(subjectRepository.findById(1L)).willReturn(Optional.of(mockSubject));
        willDoNothing().given(subjectRepository).deleteById(1L);

        Subject result = subjectService.deleteSubject(1L);

        then(subjectRepository).should().deleteById(1L);
        assertThat(result).isEqualTo(mockSubject);
    }

    @Test
    @DisplayName("deleteSubject: Выбрасывает исключение, если ID null")
    void deleteSubject_ThrowsIfIdIsNull() {
        assertThatThrownBy(() -> subjectService.deleteSubject(null))
                .isInstanceOf(Exception.class)
                .hasMessage("no id in request");
    }

    @Test
    @DisplayName("deleteSubject: Выбрасывает исключение, если предмет не найден")
    void deleteSubject_ThrowsIfNotFound() {
        given(subjectRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.deleteSubject(1L))
                .isInstanceOf(Exception.class)
                .hasMessage("subject with id 1 not found");
    }

    @Test
    @DisplayName("addSubjectsToTeacher: Выбрасывает NotFoundException, если учитель не найден")
    void addSubjectsToTeacher_ThrowsIfTeacherNotFound() {
        given(teacherRepository.findById(101L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.addSubjectsToTeacher(subjectsToAddDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("teacher not found");
    }

    @Test
    @DisplayName("unassignSubjectToTeacher: Выбрасывает NotFoundException, если учитель не найден")
    void unassignSubjectToTeacher_ThrowsIfTeacherNotFound() {
        given(teacherRepository.findById(101L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.unassignSubjectToTeacher(subjectsToAddDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("teacher not found");
    }
}