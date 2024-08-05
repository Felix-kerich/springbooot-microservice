package com.ecommerce.payment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Service
public class MpesaService {

    @Autowired
    private PaymentRepo paymentRepo;

    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    private static final Logger logger = LoggerFactory.getLogger(MpesaService.class);

    private String generatePassword(String shortCode, String passkey, String timestamp) {
        String dataToEncode = shortCode + passkey + timestamp;
        return Base64.getEncoder().encodeToString(dataToEncode.getBytes());
    }

    private String getAccessToken() throws IOException {
        String consumerKey = "w2ABSyiW0KFRn8ZxODClm2AALpO15jO4uhZ0jFmtGtyWPAFQ";
        String consumerSecret = "aBSGz6o2w2Sgjn5kY7tieUONCPQAEq4YBuPOCaoDp0wW6XgGNsHsvG6TFjJmj9Zm";
        String auth = Credentials.basic(consumerKey, consumerSecret);

        Request request = new Request.Builder()
            .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
            .get()
            .addHeader("Authorization", auth)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                logger.info("Access token response: {}", responseBody);
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                return jsonObject.get("access_token").getAsString();
            } else {
                String responseBody = response.body().string();
                logger.error("Failed to fetch access token, response: {}", responseBody);
                throw new IOException("Failed to fetch access token, response: " + responseBody);
            }
        }
    }

    public String performStkPush(String phoneNumber, String username, double amount) throws IOException {
        String shortCode = "174379";
        String passkey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String password = generatePassword(shortCode, passkey, timestamp);
        String accessToken = getAccessToken();

        MediaType mediaType = MediaType.parse("application/json");
        String json = "{"
            + "\"BusinessShortCode\": \"" + shortCode + "\","
            + "\"Password\": \"" + password + "\","
            + "\"Timestamp\": \"" + timestamp + "\","
            + "\"TransactionType\": \"CustomerPayBillOnline\","
            + "\"Amount\": " + amount + ","
            + "\"PartyA\": \"" + phoneNumber + "\","
            + "\"PartyB\": \"" + shortCode + "\","
            + "\"PhoneNumber\": \"" + phoneNumber + "\","
            + "\"CallBackURL\": \"https://eaf9-102-215-32-244.ngrok-free.app/api/mpesa/callback\","
            + "\"AccountReference\": \"" + username + "\","
            + "\"TransactionDesc\": \"Payment of product\""
            + "}";

        logger.info("STK Push request JSON: {}", json);

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
            .url("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer " + accessToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body().string();
                logger.error("STK Push failed with status: {} and response body: {}", response.code(), responseBody);
                throw new IOException("Failed STK Push, response: " + responseBody);
            }
            String responseBody = response.body().string();
            logger.info("STK Push response: {}", responseBody);
            return responseBody;
        }
    }

    public JsonObject processMpesaCallback(String callbackJson) {
        logger.info("Received callback JSON: {}", callbackJson);

        JsonObject jsonObject = JsonParser.parseString(callbackJson).getAsJsonObject();
        JsonObject body = jsonObject.getAsJsonObject("Body");
        JsonObject stkCallback = body.getAsJsonObject("stkCallback");
        int resultCode = stkCallback.get("ResultCode").getAsInt();

        logger.info("Parsed ResultCode: {}", resultCode);

        PaymentModel payment = new PaymentModel();
        payment.setStatus(resultCode == 0 ? "Success" : "Failed");

        if (resultCode == 0) {
            JsonObject callbackMetadata = stkCallback.getAsJsonObject("CallbackMetadata");
            JsonObject item0 = callbackMetadata.getAsJsonArray("Item").get(0).getAsJsonObject();
            JsonObject item1 = callbackMetadata.getAsJsonArray("Item").get(1).getAsJsonObject();
            JsonObject item4 = callbackMetadata.getAsJsonArray("Item").get(4).getAsJsonObject();

            String transactionCode = item1.get("Value").getAsString();
            String phoneNumber = item4.get("Value").getAsString();
            String name = item0.get("Value").getAsString();

            logger.info("Parsed Transaction Code: {}", transactionCode);
            logger.info("Parsed Phone Number: {}", phoneNumber);
            logger.info("Parsed Name: {}", name);

            payment.setPhoneNumber(phoneNumber);
            payment.setUsername(name);
            payment.setTransactionCode(transactionCode);
        }

        paymentRepo.save(payment);

        // Prepare JSON response with payment details
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("transactionCode", payment.getTransactionCode());
        responseJson.addProperty("phoneNumber", payment.getPhoneNumber());
        responseJson.addProperty("username", payment.getUsername());
        responseJson.addProperty("status", payment.getStatus());

        return responseJson;
    }

    public String saveMpesaDetails(PaymentModel payment) {
        paymentRepo.save(payment);
        return "Details Saved Successfully";
    }
}
