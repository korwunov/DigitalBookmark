import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Student;
import com.BookmarkService.repositories.StudentRepository;
import com.BookmarkService.services.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    private StudentService studentService;

    private Student mockStudent;

    @BeforeEach
    void setUp() {
        // Создаем тестовый объект студента
        mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setUsername("testuser");
        mockStudent.setPassword("password123");

        // Создаем сервис, внедряя моки
        studentService = new StudentService();
        try {
            java.lang.reflect.Field repoField = studentService.getClass().getDeclaredField("studentRepository");
            repoField.setAccessible(true);
            repoField.set(studentService, studentRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock repository", e);
        }
    }

    @Test
    @DisplayName("addStudent: Добавляет студента, если имя пользователя уникально")
    void addStudent_AddsSuccessfullyIfUsernameIsUnique() throws Exception {
        given(studentRepository.findByUsername("testuser")).willReturn(Optional.empty());

        studentService.addStudent(mockStudent);

        then(studentRepository).should().save(mockStudent);
        assertThat(mockStudent.getRole()).isEqualTo(EROLE.ROLE_STUDENT);
    }

    @Test
    @DisplayName("addStudent: Выбрасывает исключение, если имя пользователя уже существует")
    void addStudent_ThrowsIfUsernameExists() {
        given(studentRepository.findByUsername("testuser")).willReturn(Optional.of(mockStudent));

        assertThatThrownBy(() -> studentService.addStudent(mockStudent))
                .isInstanceOf(Exception.class)
                .hasMessage("email already registered");
    }

    @Test
    @DisplayName("getAllStudents: Возвращает список всех студентов")
    void getAllStudents_ReturnsAllStudents() {
        List<Student> expectedStudents = List.of(mockStudent);
        given(studentRepository.findAll()).willReturn(expectedStudents);

        List<Student> actualStudents = studentService.getAllStudents();

        assertThat(actualStudents).hasSize(1);
        assertThat(actualStudents).containsExactlyElementsOf(expectedStudents);
    }

    @Test
    @DisplayName("getStudentByID: Возвращает студента по ID")
    void getStudentByID_ReturnsStudent() throws Exception {
        given(studentRepository.findById(1L)).willReturn(Optional.of(mockStudent));

        Optional<Student> result = studentService.getStudentByID(1L);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockStudent);
    }

    @Test
    @DisplayName("getStudentByID: Выбрасывает исключение, если студент не найден")
    void getStudentByID_ThrowsIfNotFound() {
        given(studentRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentByID(1L))
                .isInstanceOf(Exception.class)
                .hasMessage("user not found");
    }

    @Test
    @DisplayName("deleteStudent: Удаляет студента по ID")
    void deleteStudent_DeletesSuccessfully() throws Exception {
        willDoNothing().given(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        then(studentRepository).should().deleteById(1L);
    }

    @Test
    @DisplayName("deleteStudent: Выбрасывает исключение, если студент не найден")
    void deleteStudent_ThrowsIfNotFound() {
        willThrow(new RuntimeException("Entity not found")).given(studentRepository).deleteById(1L);

        assertThatThrownBy(() -> studentService.deleteStudent(1L))
                .isInstanceOf(Exception.class)
                .hasMessage("user not found");
    }
}