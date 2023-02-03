package com.orderwire.documents;

import com.orderwire.data.DataGenerateInvoice;
import com.orderwire.logging.UtilLogger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class TransmitDocument {
    private final HashMap<String,String> transMap;

    public TransmitDocument(HashMap<String,String> transmitMap){
        transMap = transmitMap;
    }

    public HttpResponse<String> TransmitDocument(){
        String owThreadName = transMap.get("owThreadName");
        DataGenerateInvoice dgi = new DataGenerateInvoice(owThreadName);

        HashMap<String,String> parametersMap = new HashMap<>();
        parametersMap = dgi.getRuntimeParameters();
        String runtimeEnv = parametersMap.get("EnvironmentRuntimeMode").toUpperCase();
        String outgoingUrl = "";
        if (runtimeEnv.equals("S")){
            outgoingUrl = parametersMap.get("OutgoingSandboxURL");
        } else if (runtimeEnv.equals("D")) {
            outgoingUrl = parametersMap.get("OutgoingDevURL");
        } else if (runtimeEnv.equals("P")) {
            outgoingUrl = parametersMap.get("OutgoingProdURL");
        } else {

        }
        HttpResponse<String> response = null;
        try {
            String requestBody = transMap.get("resultString");
            String requestBodyLength = String.valueOf(requestBody.length());
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(outgoingUrl))
                    .header("Content-Type", "application/xml")
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception exce){
            UtilLogger.setDbStatus(owThreadName, "TransmitDocument", "TransmitDocument", "", "Exception", exce.getMessage());
        }

        return response;
    }

}
