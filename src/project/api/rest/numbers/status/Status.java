package project.api.rest.numbers.status;

public enum Status {
    GOOD(null),
    WRONG_COMMAND("The command you tried to use is not supported!"),
    WRONG_PARAMETERS("The command is used with wrong parameters!"),
    NOT_FOUND("Such fact about this topic could not be found!");

    Status(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    private final String statusMessage;
}
