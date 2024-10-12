package project.api.rest.numbers.commands;

import java.util.Arrays;

public class Command {
    public final static int COMMAND_TOKENS = 3;
    private final String command;
    private final String[] parameters;

    private Command(String command, String[] parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public static Command extractCommand(String input) {
        String command = getCommand(input);
        String[] parameters = getParameters(input);

        return new Command(command, parameters);
    }

    private static String getCommand(String input) {
        return input.split(" ")[0].trim();
    }

    private static String[] getParameters(String input) {
        String[] tokens = input.split(" ");

        tokens = Arrays.stream(tokens).map(String::trim).toArray(String[]::new);

        return Arrays.copyOfRange(tokens, 1, tokens.length);
    }

    public String getCommand() {
        return this.command;
    }

    public String[] getParameters() {
        return this.parameters;
    }
}
