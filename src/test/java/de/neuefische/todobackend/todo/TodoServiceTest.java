package de.neuefische.todobackend.todo;

import de.neuefische.todobackend.dto.TodoWOId;
import de.neuefische.todobackend.model.Todo;
import de.neuefische.todobackend.service.IdService;
import de.neuefische.todobackend.repository.TodoRepository;
import de.neuefische.todobackend.service.TodoService;
import de.neuefische.todobackend.model.TodoStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    TodoRepository mockrepo = mock(TodoRepository.class);
    IdService mockIdService = mock(IdService.class);
    TodoService todoService = new TodoService(mockrepo, mockIdService);

    @Test
    void findAllTodos_shouldReturn_t1_t2_t2_whenCalled() {
        //GIVEN
        Todo t1 = new Todo("1", "d1", TodoStatus.OPEN);
        Todo t2 = new Todo("2", "d2", TodoStatus.OPEN);
        Todo t3 = new Todo("3", "d3", TodoStatus.OPEN);
        List<Todo> todos = List.of(t1, t2, t3);

        when(mockrepo.findAll()).thenReturn(todos);

        //WHEN
        List<Todo> actual = todoService.findAllTodos();

        //THEN
        verify(mockrepo).findAll();
        assertEquals(todos, actual);
    }

    @Test
    void addTodo_shouldSaveTodoWithIdIntoDatabase_whenCalledWithDto() {
        //GIVEN
        TodoWOId newTodo = new TodoWOId("Test-Description", TodoStatus.OPEN);
        Todo todoToSave = new Todo("Test-Id", "Test-Description", TodoStatus.OPEN);

        when(mockIdService.randomId()).thenReturn("Test-Id");
        when(mockrepo.save(todoToSave)).thenReturn(todoToSave);

        //WHEN
        Todo actual = todoService.addTodo(newTodo);

        //THEN
        verify(mockIdService).randomId();
        verify(mockrepo).save(todoToSave);
        assertEquals(todoToSave, actual);
    }

    @Test
    void updateTodo_shouldReturnUpdatedTodo_whenCalledWithValidIdAndDto() {
        //GIVEN
        String id = "123";
        TodoWOId todoToUpdate = new TodoWOId("test-description", TodoStatus.IN_PROGRESS);

        Todo updatedTodo = new Todo("123", "test-description", TodoStatus.IN_PROGRESS);

        when(mockrepo.save(updatedTodo)).thenReturn(updatedTodo);

        //WHEN
        Todo actual = todoService.updateTodo(todoToUpdate, id);

        //THEN
        verify(mockrepo).save(updatedTodo);
        assertEquals(updatedTodo, actual);
    }

    @Test
    void getTodoByIdTest_shouldReturnTodo_whenCalledWithValidId() {
        //GIVEN
        String id = "1";
        Todo todo = new Todo("1", "test-description", TodoStatus.OPEN);

        when(mockrepo.findById(id)).thenReturn(Optional.of(todo));

        //WHEN
        Todo actual = todoService.findTodoById(id);

        //THEN
        verify(mockrepo).findById(id);
        assertEquals(todo, actual);
    }

    @Test
    void getTodoByIdTest_shouldThrowException_whenCalledWithInvalidId() {
        //GIVEN
        String id = "1";

        when(mockrepo.findById(id)).thenReturn(Optional.empty());

        //WHEN
        assertThrows(NoSuchElementException.class, () -> todoService.findTodoById(id));

        //THEN
        verify(mockrepo).findById(id);
    }

    @Test
    void deleteTodo_shouldCallDeleteMethodFromRepo_WhenCalledWithValidId() {
        //GIVEN
        String id = "1";
        doNothing().when(mockrepo).deleteById(id);

        //WHEN
        todoService.deleteTodo(id);

        //THEN
        verify(mockrepo).deleteById(id);
    }
}
