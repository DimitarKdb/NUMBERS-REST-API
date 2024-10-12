package project.api.rest.numbers;

import com.google.gson.Gson;
import project.api.rest.numbers.response.Response;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) {

        HttpClient client =
                HttpClient.newBuilder().build(); // configure custom executor or use the default

        //URI uri = new URI("http://numbersapi.com/random/year?json");
        try {
            URI uri = new URI("http://numbersapi.com/69/year?default=Boring+number+is+boring&json");
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

            CompletableFuture<String> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(x -> {
                        System.out.println("Thread executing thenApply(): " + Thread.currentThread().getName());
                        return x.body();
                    });


            //System.out.println(future.get());

            Gson gson = new Gson();

            Response r = gson.fromJson(future.get(), Response.class);

            System.out.println(r.getNumber());
        } catch (URISyntaxException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
