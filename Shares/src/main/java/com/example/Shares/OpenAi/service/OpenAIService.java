//package com.example.Shares.OpenAi.service;
//
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//public class OpenAIService {
//    private final String API_URL = "https://api.openai.com/v1/chat/completions";
//
//    @Autowired
//
//    @Value("${openai.api.key}")
//    private String API_KEY;
//
//    public Map<String, String> getChatGPTResponse(String prompt) {
//        // Retrieve and format vehicle list
//        List<VehicleEntity> vehicles = vehicleService.getAllVehicles();
//
//        // Convert vehicle list to a formatted string
//        String vehicleListString = vehicles.stream()
//                .map(VehicleEntity::toString) // Ensure VehicleEntity has a meaningful toString method
//                .collect(Collectors.joining(", "));
//
//        // Create instructions with vehicle list
//        String instructions = "In Kuwait, analyze the following cars: " + vehicleListString + ". Determine the best car to buy or finance under these conditions:\n" +
//                "\n" +
//                "### Conditions:\n" +
//                "- Only consider the cars listed above.\n" +
//                "- Use the car prices provided in the list (do not modify them).\n" +
//                "- Evaluate based on:\n" +
//                "  - Current salary.\n" +
//                "  - Financial obligations.\n" +
//                "  - Down payment amount.\n" +
//                "  - Desired monthly installments.\n" +
//                "  - Total family size (including myself).\n" +
//                "  - Main purpose of the car (e.g., commuting, family trips, off-road use).\n" +
//                "  - Zero-interest financing.\n" +
//                "\n" +
//                "### Calculations:\n" +
//                "1. **Monthly Installments** = (Price of Car - Down Payment) / Desired Installment Period (Months)\n" +
//                "2. **Allowed Monthly Installments** = 40% of Salary - Financial Obligations\n" +
//                "3. Only recommend cars where Monthly Installments ≤ Allowed Monthly Installments.\n" +
//                "\n" +
//                "### Recommendations:\n" +
//                "For each car that fits the criteria, only return in the format below dont add thing else:\n" +
//                " id:\n" +
//                " Car Name:\n" +
//                " Monthly Installments:\n" +
//                " Price:\n" +
//                " Reasons for recommendation: (clear and concise).\n";
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        // Prepare headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + API_KEY);
//        headers.set("Content-Type", "application/json");
//
//        // Prepare request body
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "gpt-3.5-turbo");
//        Object messageBody = new Object[]{
//                Map.of("role", "system", "content", instructions),
//                Map.of("role", "user", "content", prompt)
//        };
//        requestBody.put("messages", messageBody);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//
//        try {
//            // Make API call
//            ResponseEntity<String> response =
//                    restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
//
//            // Parse response body
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode jsonNode = mapper.readTree(response.getBody());
//            String content = jsonNode.path("choices").get(0).path("message").path("content").asText();
//
//            // Parse content from plain text response
//            Map<String, String> parsedResponse = parseResponse(content);
//
//            return parsedResponse; // Return extracted details as a map
//
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("id", "");
//            errorResponse.put("carName", "");
//            errorResponse.put("price", "");
//            errorResponse.put("installments", "");
//            errorResponse.put("reasons", "Error: " + e.getMessage());
//            return errorResponse;
//        }
//    }
//
//    private Map<String, String> parseResponse(String response) {
//        Map<String, String> result = new HashMap<>();
//
//        for (String line : response.split("\n")) {
//            String[] parts = line.split(": ", 2);
//            if (parts.length == 2) {
//                switch (parts[0].trim()) {
//                    case "id":
//                        result.put("id", parts[1].trim());
//                        break;
//                    case "Car Name":
//                        result.put("carName", parts[1].trim());
//                        break;
//                    case "Monthly Installments":
//                        result.put("installments", parts[1].trim());
//                        break;
//                    case "Price":
//                        result.put("price", parts[1].trim());
//                        break;
//                    case "Reasons for recommendation":
//                        result.put("reasons", parts[1].trim());
//                        break;
//                    default:
//                        break;
//                }
//            }
//        }
//
//        return result;
//    }
//}
