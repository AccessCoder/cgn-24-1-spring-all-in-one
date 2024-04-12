package de.neuefische.todobackend.controller;

import de.neuefische.todobackend.dto.TodoWOId;
import de.neuefische.todobackend.model.Todo;
import de.neuefische.todobackend.service.TdService;
import de.neuefische.todobackend.service.TodoService;
import de.neuefische.todobackend.service.TodoServiceWithRestClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
public class TodoController {

    /**
     * In diesem Beispiel halten wir uns die explizite Implementierung
     * offen und "verschieben" sie auf den Constructor (Weshalb wir ihn diesmal nicht über Lombok erzeugen)
     *
     * Warum mache ich das?
     * Allem voran weil es sich gewünscht wurde.
     * Generell gilt:
     * - Habt ihr nur einen möglichen Service für euren Controller und das wird auch so bleiben?
     *   Dann baut ihn direkt "wie bis jetzt auch" explizit in euren Controller ein.
     *
     * - Gibt es die Möglichkeit das auch andere Services in deinem Controller implementiert
     * werden könnten?
     *   Dann lohnt es sich, wie hier, nur das Interface anzugeben und die explizite Implementation
     *   auf später zu verschieben.
     *
     *  Und warum mache ich das jetzt hier im Projekt?
     *  Ich habe 2 Services für unsere Todos, einer benutzt OpenAi mit dem RestClient, der andere nicht.
     *  Solange mein Token noch gültig ist, implementiere ich den openAI Service.
     *  Später gebe ich aber sehr einfach die Möglichkeit auf den Service ohne openAI umzusteigen.
     */
    private final TdService todoService;

    public TodoController(TodoServiceWithRestClient todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoService.findAllTodos();
    }

    @GetMapping("/{id}")
    public Todo getTodoById(@PathVariable String id) {
        return todoService.findTodoById(id);
    }

    @PostMapping
    public Todo postTodo(@RequestBody TodoWOId todoWOId) {
        return todoService.addTodo(todoWOId);
    }

    @PutMapping("/{id}")
    public Todo putTodo(@RequestBody TodoWOId todo, @PathVariable String id) {
        return todoService.updateTodo(todo, id);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable String id) {
        todoService.deleteTodo(id);
    }

    @GetMapping("/initialize")
    public void initializeTopics(){
        todoService.generateStackOfTodos();
    }
}
