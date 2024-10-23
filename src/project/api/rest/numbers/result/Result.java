package project.api.rest.numbers.result;

import project.api.rest.numbers.status.Status;
import project.api.rest.numbers.type.facts.FactType;

public record Result(String message, Status status, FactType factType) {
    @Override
    public String toString() {

        return "Result: " +
                (message == null? status.getStatusMessage() : "fact- '" + message + "'") +
                (factType == null || this.status != Status.GOOD? "" : "; fact-type: " + factType.getType());

    }

}
