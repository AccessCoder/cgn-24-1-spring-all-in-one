package de.neuefische.todobackend.dto;

import de.neuefische.todobackend.model.TodoStatus;

public record TodoWOId(
        String description,
        TodoStatus status
) {
}
