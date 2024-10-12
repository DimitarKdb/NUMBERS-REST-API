package project.api.rest.numbers.httpresponse;

public class HTTPResponse {
    private final String text;
    private final String number;
    private final boolean found;
    private final String type;

    public HTTPResponse(String text, String number, boolean found, String type) {
        this.text = text;
        this.number = number;
        this.found = found;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public String getNumber() {
        return number;
    }

    public boolean isFound() {
        return found;
    }

    public String getType() {
        return type;
    }
}
