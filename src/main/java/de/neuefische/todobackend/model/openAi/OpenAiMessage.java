package de.neuefische.todobackend.model.openAi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *          {
 *  *              "role": "user",
 *  *              "content": "Was ist eine Primzahl"
 *  *       }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiMessage {

    private String role;
    private String content;

    public OpenAiMessage(String question){
        this.role = "user";
        this.content = question;
    }

}
