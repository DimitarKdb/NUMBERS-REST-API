package project.api.rest.numbers.requesthandler;

import com.google.gson.Gson;
import project.api.rest.numbers.httpresponse.HTTPResponse;
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

        if (type == FactType.DATE && !parameters[0].equals("random")) {
            uri = URI.create("http://numbersapi.com/" + parameters[1] + "/" + parameters[0] + "/" + type.getType() + "?json");
        } else {
            uri = URI.create("http://numbersapi.com/" + parameters[0] + "/" + type.getType() + "?json");
        }

        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        CompletableFuture<String> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);

        Gson gson = new Gson();

        HTTPResponse response;

        try {
            response = gson.fromJson(future.get(), HTTPResponse.class);
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
