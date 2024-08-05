package com.ecommerce.payment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@CrossOrigin
@RequestMapping("/api/mpesa")
public class MpesaController {

    private static final Logger logger = LoggerFactory.getLogger(MpesaController.class);

    @Autowired
    private MpesaService mpesaService;

    @Autowired
    private PaymentRepo mpesaTransactionRepository;
    @GetMapping("/home")
    public String home(){
        return "hello there";
    }
    public String getMethodName(@RequestParam String param) {
        return new String();
    }
     
  @PostMapping("/stkpush")
public ResponseEntity<String> initiateStkPush(@RequestBody PaymentModel payment) {
    try {
        // Validate input fields if necessary
        if (payment.getPhoneNumber() == null || payment.getUsername() == null || payment.getAmount() <= 0) {
            logger.warn("Invalid payment details: {}", payment);
            return new ResponseEntity<>("Invalid payment details", HttpStatus.BAD_REQUEST);
        }

        // Perform STK push
        String response = mpesaService.performStkPush(payment.getPhoneNumber(), payment.getUsername(), payment.getAmount());
        
        // Log and return the response from the STK push
        logger.info("STK Push response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (IOException e) {
        // Log the error and save payment details for future reference
        logger.error("Error initiating STK Push: ", e);
        mpesaService.saveMpesaDetails(payment);
        return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
        // Catch any other exceptions and return a generic error response
        logger.error("Unexpected error occurred: ", e);
        return new ResponseEntity<>("Unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


    @PostMapping("/callback")
    public ResponseEntity<JsonObject> handleMpesaCallback(@RequestBody String callbackJson) {
        try {
            logger.info("Received callback JSON: {}", callbackJson);

            JsonObject jsonObject = JsonParser.parseString(callbackJson).getAsJsonObject();
            JsonObject body = jsonObject.getAsJsonObject("Body");
            JsonObject stkCallback = body.getAsJsonObject("stkCallback");
            int resultCode = stkCallback.get("ResultCode").getAsInt();

            logger.info("Parsed ResultCode: {}", resultCode);

            JsonObject callbackMetadata = stkCallback.getAsJsonObject("CallbackMetadata");
            JsonObject item0 = callbackMetadata.getAsJsonArray("Item").get(0).getAsJsonObject();
            JsonObject item1 = callbackMetadata.getAsJsonArray("Item").get(1).getAsJsonObject();
            JsonObject item4 = callbackMetadata.getAsJsonArray("Item").get(4).getAsJsonObject();

            String merchantRequestId = stkCallback.get("MerchantRequestID").getAsString();
            String checkoutRequestId = stkCallback.get("CheckoutRequestID").getAsString();
            String transactionId = item1.get("Value").getAsString();
            double amount = item0.get("Value").getAsDouble();
            String userPhoneNumber = item4.get("Value").getAsString();

            // Check if the transaction was successful
            if (resultCode == 0) {
                // Store the transaction details in the database
                PaymentModel transaction = new PaymentModel();
                transaction.setMerchantRequestId(merchantRequestId);
                transaction.setCheckoutRequestId(checkoutRequestId);
                transaction.setResultCode(resultCode);
                transaction.setAmount(amount);
                transaction.setMpesaReceiptNumber(transactionId);
                transaction.setPhoneNumber(userPhoneNumber);

                mpesaTransactionRepository.save(transaction);

                return new ResponseEntity<>(createResponse("Transaction successful"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(createResponse("Transaction failed"), HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error("Error processing callback: ", e);
            return new ResponseEntity<>(createResponse("Error processing callback: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private JsonObject createResponse(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("message", message);
        return response;
    }
}





// @PostMapping("/callback")
//     public ResponseEntity<JsonObject> handleMpesaCallback(@RequestBody String callbackJson) {
//         try {
//             JsonObject response = mpesaService.processMpesaCallback(callbackJson);
//             return new ResponseEntity<>(response, HttpStatus.OK);
//         } catch (Exception e) {
//             logger.error("Error processing callback: ", e);
//             JsonObject errorResponse = new JsonObject();
//             errorResponse.addProperty("error", "Error processing callback");
//             errorResponse.addProperty("message", e.getMessage());
//             return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
//         }
//     }