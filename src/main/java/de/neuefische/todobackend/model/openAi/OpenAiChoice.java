package de.neuefische.todobackend.model.openAi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *  *             "message": {
 *  *                 "role": "assistant",
 *  *                 "content": "Eine Primzahl ist eine natürliche Zahl größer als 1, die nur durch sich selbst und 1 ohne Rest teilbar ist. Das bedeutet, dass eine Primzahl nur zwei Teiler hat: 1 und sich selbst. Beispiele für Primzahlen sind 2, 3, 5, 7, 11, 13, 17, 19, 23, usw. Es gibt unendlich viele Primzahlen. Die Entdeckung und Untersuchung von Primzahlen ist ein wichtiger Bereich in der Zahlentheorie."
 *  *             }
 *  *         }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiChoice {

    private OpenAiMessage message;

}
