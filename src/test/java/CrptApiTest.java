import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.CrptApi;

public class CrptApiTest {
    public static void main(String[] args) {
        // Пример ограничения на 10 запросов в секунду
        CrptApi crptApi = new CrptApi(10);

        CrptApi.DocumentRequest documentRequest = new CrptApi.DocumentRequest(
                "doc123", "draft", "LP_INTRODUCE_GOODS", true, "ownerInn123", "participantInn123",
                "producerInn123", "2023-07-09", "type123", new CrptApi.Product[] {
                new CrptApi.Product("cert123", "2023-07-09", "certNum123", "ownerInn123",
                        "producerInn123", "2023-07-09", "tnved123", "uit123", "uitu123")
        }, "2023-07-09", "regNum123"
        );

        try {
            // Преобразуем объект запроса в JSON строку
            String documentJson = crptApi.objectMapper.writeValueAsString(documentRequest);

            // Вызываем метод для создания документа
            crptApi.createDocument(documentJson);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

