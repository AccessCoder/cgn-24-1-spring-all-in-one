package de.neuefische.todobackend.service;

import de.neuefische.todobackend.dto.TodoWOId;
import de.neuefische.todobackend.model.Todo;
import de.neuefische.todobackend.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TodoService implements TdService{

    private final TodoRepository todoRepository;
    private final IdService idService;

    public TodoService(TodoRepository todoRepository, IdService idService) {
        this.todoRepository = todoRepository;
        this.idService = idService;
    }

    public List<Todo> findAllTodos() {
        return todoRepository.findAll();
    }

    public Todo addTodo(TodoWOId todoWOId) {
        String id = idService.randomId();

        Todo todoToSave = new Todo(id, todoWOId.description(), todoWOId.status());

        return todoRepository.save(todoToSave);
    }

    public Todo updateTodo(TodoWOId todo, String id) {
        Todo todoToUpdate = new Todo(id, todo.description(), todo.status());

        return todoRepository.save(todoToUpdate);
    }

    public Todo findTodoById(String id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo with id: " + id + " not found!"));
    }

    public void deleteTodo(String id) {
        todoRepository.deleteById(id);
    }

    @Override
    public void generateStackOfTodos() {

    }
}
