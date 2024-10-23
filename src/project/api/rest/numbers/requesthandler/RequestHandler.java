package project.api.rest.numbers.requesthandler;

import com.google.gson.Gson;
import project.api.rest.numbers.factdata.FactData;
import project.api.rest.numbers.result.Result;
import project.api.rest.numbers.status.Status;
import project.api.rest.numbers.type.facts.FactType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RequestHandler {
    private final HttpClient client;

    private RequestHandler() {
        client = HttpClient.newBuilder().build();
    }

    public static RequestHandler getInstance() {
        return new RequestHandler();
    }

    public Result apiResponse(String[] parameters, FactType type) {

        URI uri;
        String parameter;

        if (type == FactType.DATE && !parameters[0].equals("random")) {
            parameter = parameters[1] + "/" + parameters[0];
            uri = URI.create("http://numbersapi.com/" + parameter + "/" + type.getType() + "?json");
        } else {
            parameter = parameters[0];
            uri = URI.create("http://numbersapi.com/" + parameter + "/" + type.getType() + "?json");
        }

        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        CompletableFuture<String> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);

        Gson gson = new Gson();

        FactData response;

        try {
            response = gson.fromJson(future.get(), FactData.class);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Problem with the HTTP request. No response is given.", e);
        }

        if (response.isFound()) {
            return new Result(response.getText(), Status.GOOD, type);
        } else {
            return new Result(null, Status.NOT_FOUND, type);
        }
    }
}
