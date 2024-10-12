package project.api.rest.numbers.result;

import project.api.rest.numbers.status.Status;
import project.api.rest.numbers.type.facts.FactType;

public record Result(String message, Status status, FactType factType) {
    @Override
    public String toString() {

        String status = statusMessage();

        return "Result: " +
                (message == null? status : "fact- '" + message + "'") +
                (factType == null || this.status != Status.GOOD? "" : "; fact-type: " + factType.getType());
    }

    private String statusMessage() {
        return switch (this.status) {
            case GOOD -> null;
            case NOT_FOUND -> "Such fact about the topic " + factType.getType() + " could not be found!";
            case WRONG_COMMAND -> "The command you tried to use is not supported!";
            case WRONG_PARAMETERS -> "The command is used with wrong parameters!";
        };
    }
}
