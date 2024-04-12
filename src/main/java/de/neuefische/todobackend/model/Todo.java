package de.neuefische.todobackend.model;

import de.neuefische.todobackend.model.TodoStatus;

public record Todo(
        String id,
        String description,
        TodoStatus status
) {
}
