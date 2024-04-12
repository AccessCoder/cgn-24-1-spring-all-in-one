package de.neuefische.todobackend.service;

import de.neuefische.todobackend.dto.TodoWOId;
import de.neuefische.todobackend.model.Todo;
import de.neuefische.todobackend.model.TodoStatus;
import de.neuefische.todobackend.model.openAi.OpenAiRequest;
import de.neuefische.todobackend.model.openAi.OpenAiResponse;
import de.neuefische.todobackend.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TodoServiceWithRestClient implements TdService{

    private final TodoRepository todoRepository;
    private final IdService idService;

    private final RestClient client;

    public TodoServiceWithRestClient(TodoRepository todoRepository,
                                     IdService idService,
                                     @Value("${BASE_URL}") String baseUrl,
                                     @Value("${API_KEY}") String authKey) {
        this.todoRepository = todoRepository;
        this.idService = idService;
        this.client = RestClient.builder()
                .defaultHeader("Authorization", "Bearer "+authKey)
                .baseUrl(baseUrl)
                .build();
    }

    public List<Todo> findAllTodos() {
        return todoRepository.findAll();
    }

    public Todo addTodo(TodoWOId todoWOId) {
        String id = idService.randomId();
        //Prüfung der Schreibweise durch ChatGPT -> BETA
        //String spellCheckedName = checkSpelling(todoWOId.description());
        //Todo todoToSave = new Todo(id, spellCheckedName, todoWOId.status());

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

    public void generateStackOfTodos(){
        OpenAiRequest request = new OpenAiRequest("Generiere mir eine Liste mit 10 todos für das Thema: Haushalt");
        String response = client.post()
                .body(request)
                .retrieve()
                .body(OpenAiResponse.class)
                .getAnswer();

        //Die "Liste" die ChatGPT schickt ist ein String mit nummerierten Todos, das \n ist ein Zeilenumbruch
        //Wir wollen also für jeden Umbruch einen neuen Eintrag in unserer Liste, deshalb Arrays.asList().
        List<String> todos = Arrays.asList(response.split("\n"));
        for (String s:todos) {
            //z. B. "1. Putzen" steht aktuell im String, wir wollen aber nur "Putzen", weshalb wir den Index des
            //Leerzeichens wissen wollen.
            int spaceIndex = s.indexOf(' ');
            //substring erstellt uns einen neuen String aus einem Teil des Originals. wir geben an ab welcher Stelle
            //des Originals der substring starten soll, nämlich ein Zeichen nach dem Leerzeichen,
            //so wird aus "1. Putzen" -> "Putzen"
            addTodo(new TodoWOId(s.substring(spaceIndex+1), TodoStatus.OPEN));
        }
    }


    //Nach 2h versuchen eine konstante Antwort zu erhalten: Diese Methode ist noch in der Beta :D
    private String checkSpelling(String textToCheckSpelling){
        OpenAiRequest request = new OpenAiRequest("Wie schreibt man: "+ textToCheckSpelling + "Gib mir die korrekte Schreibweise bitte in ");
        return client.post()
                .body(request)
                .retrieve()
                .body(OpenAiResponse.class)
                .getAnswer();

    }
}
