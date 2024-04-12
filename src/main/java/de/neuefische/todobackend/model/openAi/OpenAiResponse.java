package de.neuefische.todobackend.model.openAi;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * {
 *     "id": "chatcmpl-9CNQNd1RVb04iwQcm5oS0RsZZMCsO",
 *     "choices": [
 *         {
 *             "index": 0,
 *             "message": {
 *                 "role": "assistant",
 *                 "content": "Eine Primzahl ist eine natürliche Zahl größer als 1, die nur durch sich selbst und 1 ohne Rest teilbar ist. Das bedeutet, dass eine Primzahl nur zwei Teiler hat: 1 und sich selbst. Beispiele für Primzahlen sind 2, 3, 5, 7, 11, 13, 17, 19, 23, usw. Es gibt unendlich viele Primzahlen. Die Entdeckung und Untersuchung von Primzahlen ist ein wichtiger Bereich in der Zahlentheorie."
 *             },
 *             "logprobs": null,
 *             "finish_reason": "stop"
 *         }
 *     ]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiResponse {

    private String id;
    private List<OpenAiChoice> choices;

    public String getAnswer(){
        return getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}
