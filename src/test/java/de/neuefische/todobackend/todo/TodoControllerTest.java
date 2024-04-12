package de.neuefische.todobackend.todo;

import de.neuefische.todobackend.model.Todo;
import de.neuefische.todobackend.repository.TodoRepository;
import de.neuefische.todobackend.model.TodoStatus;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TodoRepository todoRepository;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void backendProps (DynamicPropertyRegistry registry){
        registry.add("BASE_URL", () -> mockWebServer.url("/").toString());
        registry.add("API_KEY", () -> "");
    }

    @Test
    void getAllTodos_shouldReturnEmptyList_whenCalledInitially() throws Exception {
        //GIVEN

        //WHEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/todo"))

                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            []
                        """));

    }

    @Test
    void postTodo_shouldReturnNewTodoWithId_WhenCalledWithDto() throws Exception {
        //GIVEN

        //WHEN
        mockMvc.perform(post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "description": "test-description",
                                        "status": "OPEN"
                                    }
                                """)
                )
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            {
                                "description": "test-description",
                                "status": "OPEN"
                            }
                        """))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void putTodo_shouldReturnUpdatedTodo_WhenCalledWithValidIdAndUpdatedInformation() throws Exception {
        //GIVEN
        Todo existingTodo = new Todo("1", "test-description", TodoStatus.OPEN);

        todoRepository.save(existingTodo);

        //WHEN
        mockMvc.perform(put("/api/todo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "description": "test-description-2",
                                        "status": "IN_PROGRESS"
                                    }
                                """))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            {
                                "id": "1",
                                "description": "test-description-2",
                                "status": "IN_PROGRESS"
                            }
                        """));
    }

    @Test
    void getById_shouldReturnTodo_WhenCalledWithValidId() throws Exception {
        //GIVEN
        Todo existingTodo = new Todo("1", "test-description", TodoStatus.OPEN);
        todoRepository.save(existingTodo);

        //WHEN
        mockMvc.perform(get("/api/todo/1"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            {
                                "id": "1",
                                "description": "test-description",
                                "status": "OPEN"
                            }
                        """));

    }

    @Test
    void getByIdTest_shouldReturnStatus404_whenGivenInvalidId() throws Exception {
        //GIVEN
        //WHEN & THEN

        mockMvc.perform(get("/api/todo/1"))
                .andExpect(status().isNotFound());

    }


    @Test
    void deleteTodoById_shouldReturnStatus200_whenCalledWithValidId() throws Exception {
        //GIVEN
        Todo existingTodo = new Todo("1", "test-description", TodoStatus.OPEN);
        todoRepository.save(existingTodo);

        //WHEN & THEN
        mockMvc.perform(delete("/api/todo/1"))
                .andExpect(status().isOk());
        //Double Check that the Item is really removed from the Backend
        mockMvc.perform(get("/api/todo/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void initializeTopics_shouldCallApiAndSafeNewTodos_whenCalled() throws Exception {
        //GIVEN
        mockWebServer.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "id": "chatcmpl-9D8RQiHKWoKhis7O00MvWFMWPnSiI",
                            "object": "chat.completion",
                            "created": 1712917740,
                            "model": "gpt-3.5-turbo-0125",
                            "choices": [
                                {
                                    "index": 0,
                                    "message": {
                                        "role": "assistant",
                                        "content": "1. Staubsaugen in allen Zimmern\\n2. Wäsche waschen und bügeln\\n3. Küche aufräumen und abwaschen\\n4. Müll entsorgen und Mülltonnen rausstellen\\n5. Badezimmer putzen (WC, Dusche, Waschbecken)\\n6. Fenster putzen\\n7. Pflanzen gießen\\n8. Einkaufsliste für Lebensmittel erstellen\\n9. Schränke und Schubladen ausmisten und ordnen\\n10. Boden wischen in allen Räumen"
                                    },
                                    "logprobs": null,
                                    "finish_reason": "stop"
                                }
                            ],
                            "usage": {
                                "prompt_tokens": 23,
                                "completion_tokens": 138,
                                "total_tokens": 161
                            },
                            "system_fingerprint": "fp_b28b39ffa8"
                        }
                        """));

        //WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/todo/initialize"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/todo"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                            {
                                "description": "Staubsaugen in allen Zimmern",
                                "status": "OPEN"
                            },
                            {
                                "description": "Wäsche waschen und bügeln",
                                "status": "OPEN"
                            },
                            {
                                "description": "Küche aufräumen und abwaschen",
                                "status": "OPEN"
                            },
                            {
                                "description": "Müll entsorgen und Mülltonnen rausstellen",
                                "status": "OPEN"
                            },
                            {
                                "description": "Badezimmer putzen (WC, Dusche, Waschbecken)",
                                "status": "OPEN"
                            },
                            {
                                "description": "Fenster putzen",
                                "status": "OPEN"
                            },
                            {
                                "description": "Pflanzen gießen",
                                "status": "OPEN"
                            },
                            {
                                "description": "Einkaufsliste für Lebensmittel erstellen",
                                "status": "OPEN"
                            },
                            {
                                "description": "Schränke und Schubladen ausmisten und ordnen",
                                "status": "OPEN"
                            },
                            {
                                "description": "Boden wischen in allen Räumen",
                                "status": "OPEN"
                            }
                        ]
                        """))
                .andExpect(jsonPath("$[0].id").exists());


    }
}
