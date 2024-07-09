package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class CrptApi {
    private final CloseableHttpClient httpClient;
    public final ObjectMapper objectMapper;
    private final Semaphore semaphore;

    public CrptApi(int requestLimit) {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        this.semaphore = new Semaphore(requestLimit);
    }

    public void createDocument(String documentJson) {
        try {
            // Пытаемся захватить разрешение перед выполнением запроса
            if (!semaphore.tryAcquire(10)) {
                throw new IllegalStateException("Превышено ограничение на количество запросов");
            }

            // Создаем POST запрос
            HttpPost postRequest = new HttpPost("https://ismp.crpt.ru/api/v3/lk/documents/create");

            // Устанавливаем заголовки и тело запроса
            postRequest.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
            postRequest.setEntity(new StringEntity(documentJson, ContentType.APPLICATION_JSON));

            // Выполняем запрос и получаем ответ
            httpClient.execute(postRequest, response -> {
                int statusCode = response.getCode();
                if (statusCode == 200) {
                    System.out.println("Запрос успешно выполнен: " + statusCode);
                } else {
                    System.out.println("Не удалось выполнить запрос: " + statusCode);
                }
                return null;
            });

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // В любом случае освобождаем разрешение после выполнения запроса
            semaphore.release();
        }
    }

    // Пример класса для создания документа
    public static class DocumentRequest {
        @JsonProperty("description")
        private final Description description;
        @JsonProperty("doc_id")
        private final String docId;
        @JsonProperty("doc_status")
        private final String docStatus;
        @JsonProperty("doc_type")
        private final String docType;
        @JsonProperty("importRequest")
        private final boolean importRequest;
        @JsonProperty("owner_inn")
        private final String ownerInn;
        @JsonProperty("participant_inn")
        private final String participantInn;
        @JsonProperty("producer_inn")
        private final String producerInn;
        @JsonProperty("production_date")
        private final String productionDate;
        @JsonProperty("production_type")
        private final String productionType;
        @JsonProperty("products")
        private final Product[] products;
        @JsonProperty("reg_date")
        private final String regDate;
        @JsonProperty("reg_number")
        private final String regNumber;

        public DocumentRequest(String docId, String docStatus, String docType, boolean importRequest, String ownerInn, String participantInn, String producerInn, String productionDate, String productionType, Product[] products, String regDate, String regNumber) {
            this.description = new Description(participantInn);
            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
            this.participantInn = participantInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.products = products;
            this.regDate = regDate;
            this.regNumber = regNumber;
        }
    }

    static class Description {
        @JsonProperty("participantInn")
        private final String participantInn;

        public Description(String participantInn) {
            this.participantInn = participantInn;
        }
    }

    public static class Product {
        @JsonProperty("certificate_document")
        private final String certificateDocument;
        @JsonProperty("certificate_document_date")
        private final String certificateDocumentDate;
        @JsonProperty("certificate_document_number")
        private final String certificateDocumentNumber;
        @JsonProperty("owner_inn")
        private final String ownerInn;
        @JsonProperty("producer_inn")
        private final String producerInn;
        @JsonProperty("production_date")
        private final String productionDate;
        @JsonProperty("tnved_code")
        private final String tnvedCode;
        @JsonProperty("uit_code")
        private final String uitCode;
        @JsonProperty("uitu_code")
        private final String uituCode;

        public Product(String certificateDocument, String certificateDocumentDate, String certificateDocumentNumber, String ownerInn, String producerInn, String productionDate, String tnvedCode, String uitCode, String uituCode) {
            this.certificateDocument = certificateDocument;
            this.certificateDocumentDate = certificateDocumentDate;
            this.certificateDocumentNumber = certificateDocumentNumber;
            this.ownerInn = ownerInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.tnvedCode = tnvedCode;
            this.uitCode = uitCode;
            this.uituCode = uituCode;
        }
    }
}
