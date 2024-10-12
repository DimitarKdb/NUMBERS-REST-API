package project.api.rest.numbers.type.facts;

public enum FactType {
    TRIVIA("trivia"),
    MATH("math"),
    DATE("date"),
    YEAR("year"),
    RANDOM("random");

    private final String type;

    FactType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
