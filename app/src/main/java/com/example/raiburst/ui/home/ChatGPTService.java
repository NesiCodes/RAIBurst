package com.example.raiburst.ui.home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatGPTService {
    // OpenAI API Key
    private static final String API_KEY = "VENDOSNI_API_KEY_KETU";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private String previousIntent = "none";
    private boolean successContactAndAmount = false;

//    public static void main(String[] args) {
//        try {
//            String userInput = "Send $100 to Alex";
//            String apiResponse = getChatGPTResponse(userInput);
//
//            // Extract intent and entities from the response
//            String intent = extractIntent(apiResponse);
//            HashMap<String, String> entities = extractEntities(apiResponse);
//
//            // Print extracted data
//            System.out.println("Intent: " + intent);
//            System.out.println("Entities:");
//            entities.forEach((key, value) -> System.out.println(key + ": " + value));
//
//            if (intent.equals("send_money")){
//                char currencySymbol = entities.get("MONEY").charAt(0);
//                // Extract the numeric part, parse it to a double
//                double amount = Double.parseDouble(entities.get("MONEY").substring(1));
//                String person = entities.get("PERSON");
//                // TODO: Validate inputs
//                // TODO: check if the person is a friend of the user and chatgpt asks user to verify its email
//                // TODO: chatgpt asks user if he wants to proceed  and asks him to enter pin
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void getChatGPTResponseAsync(String userMessage, ChatGPTCallback callback) {

        new Thread(() -> {
            try {

                String systemInstruction = "You are a conversational banking assistant that detects intent and extracts entities (e.g., MONEY, PERSON) "
                        + "from user input. Output intent and entities clearly.\n\nExample:\n"
                        + "Input: \"Send $100 to Alex\"\n"
                        + "Output: Intent: send_money, Entities: MONEY: $100, PERSON: Alex \n\n"
                        + "Here is a list of data for you to train on to detect intents, the training_sentences contains the messages and the intents contains the corresponding intent for each sentence, the first entry in training_sentences corresponds to the first entry in the intents and so on: \n"
                        + "training_sentences = [\"Send $100 to Alex\", \"Check my balance\", \"Transfer $50 to Bob\", \"That is right\", \"Help me pay my electricity bill\", \"Correct\", \"Yes\", \"Ok\"] \n"
                        + "intents = [\"send_money\", \"check_balance\", \"send_money\", \"user_approval\", \"pay_bill\", \"user_approval\", \"user_approval\", \"user_approval\"]\n" + "\n"
                        + "Also use these facts as training data: \n"
                        + "1) If the user has entered a 8 digit number the intent is user_pin \n"
                        + "2) If the message does not fall in any of the above intents than provide not_recognized as the intent: \n";
                String apiResponse = getChatGPTResponse(userMessage, systemInstruction);
                String intent = extractIntent(apiResponse);
                HashMap<String, String> entities = extractEntities(apiResponse);
                System.out.println("money: " + entities.get("MONEY"));
                System.out.println("person: " + entities.get("PERSON"));
            if (intent.equals("send_money")){
                if (entities.get("MONEY") == null || entities.get("PERSON") == null){
                    systemInstruction = "You are a conversational banking assistant. The user just entered a message where he wants to send money but he didnt provide either the person he wants to send it to or the amount. Tell him what he did wrong and what he should provide";
                    apiResponse = getChatGPTResponse("None only system message this time", systemInstruction);
                    callback.onResponse(apiResponse);
                    return;
                }
                char currencySymbol = entities.get("MONEY").charAt(0);
                // Extract the numeric part, parse it to a double
                double amount = Double.parseDouble(entities.get("MONEY").substring(1));
                String person = entities.get("PERSON");
                // TODO: Validate amount
                if (isFriend(person)){
                    systemInstruction = "You are a conversational banking assistant. The user has send a message: " + userMessage + " we have found an entry in the users contact list for " + entities.get("PERSON") +  "with email alex123@gmail.com, also we know that the users balance is " + currencySymbol + "5000. Ask the user to confirm the details of his transaction and friend also tell him the new balance he would be on after the transaction. and than ask him to enter his 8-digit pin to verify";
                    apiResponse = getChatGPTResponse("None only system message this time", systemInstruction);
                    successContactAndAmount = true;
                }else{
                    systemInstruction = "You are a conversational banking assistant. The user has send a message: " + userMessage + " we have not found entry in the users contact list for " + entities.get("PERSON") + " so we cant make this transaction. Tell him why he cant make this transfer";
                    apiResponse = getChatGPTResponse("None only system message this time", systemInstruction);
                }
            }else if (intent.equals("user_pin") && (previousIntent.equals("user_pin") || previousIntent.equals("send_money")) && successContactAndAmount){
                if (userMessage.equals("12345678")){ //supozojme per momentin
                    systemInstruction = "You are a conversational banking assistant. The user just entered his correct pin. Tell him that his transaction was successfull and if he needs any further assistance";
                    apiResponse = getChatGPTResponse("None only system message this time", systemInstruction);
                }else{
                    systemInstruction = "You are a conversational banking assistant. The user just entered an incorrect pin and we cant continue with his transaction. Tell him to try his pin again";
                    apiResponse = getChatGPTResponse("None only system message this time", systemInstruction);
                }
            }else if (intent.equals("not_recognized")){
                systemInstruction = "You are a conversational banking assistant. The user just entered an message we dont understand. Tell him that either we dont understand his message and tell him to try to provide a more detailed message";
                apiResponse = getChatGPTResponse("None only system message this time", systemInstruction);
            }else{
                systemInstruction = "You are a conversational banking assistant. The user just entered an message we dont understand. Tell him that either we dont understand his message and tell him to try to provide a more detailed message";
                apiResponse = getChatGPTResponse("None only system message this time", systemInstruction);
            }
            previousIntent = intent;
                callback.onResponse(apiResponse);
            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private boolean isFriend(String person) {
        return person.equals("Alex");
    }

    // Function to call ChatGPT API for intent and entity extraction
    private static String getChatGPTResponse(String userMessage, String systemInstruction) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        // JSON payload with the system instruction and user input
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemInstruction);

        JsonObject userMessageObject = new JsonObject();
        userMessageObject.addProperty("role", "user");
        userMessageObject.addProperty("content", userMessage);

        JsonObject payload = new JsonObject();
        payload.addProperty("model", "gpt-3.5-turbo"); // Use your model version
        payload.add("messages", gson.toJsonTree(new JsonObject[]{systemMessage, userMessageObject}));

        // Request body with JSON payload
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), gson.toJson(payload));

        // HTTP POST request
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // Execute the request and handle the response
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code: " + response);
        }

        // Parse and return the response
        String responseBody = response.body().string();
        JsonObject responseObject = gson.fromJson(responseBody, JsonObject.class);

        return responseObject.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
    }

    // Extract the intent from the API response
    private static String extractIntent(String response) {
        Pattern pattern = Pattern.compile("Intent:\\s*(\\w+)");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null; // Default if no intent is found
    }

    // Extract the entities from the API response
    private static HashMap<String, String> extractEntities(String response) {
        HashMap<String, String> entities = new HashMap<>();
        Pattern pattern = Pattern.compile("([A-Z]+):\\s*([^,\\n]+)");
        Matcher matcher = pattern.matcher(response);

        while (matcher.find()) {
            String entityType = matcher.group(1);  // Entity type (e.g., MONEY, PERSON)
            String entityValue = matcher.group(2); // Entity value
            entities.put(entityType, entityValue);
        }
        return entities;
    }
}
