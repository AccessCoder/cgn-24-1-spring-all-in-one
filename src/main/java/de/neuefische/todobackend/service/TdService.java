package de.neuefische.todobackend.service;

import de.neuefische.todobackend.dto.TodoWOId;
import de.neuefische.todobackend.model.Todo;

import java.util.List;

public interface TdService {

    List<Todo> findAllTodos();

    Todo addTodo(TodoWOId todoWOId);

    Todo updateTodo(TodoWOId todo, String id);

    Todo findTodoById(String id);

    void deleteTodo(String id);

    void generateStackOfTodos();
}
