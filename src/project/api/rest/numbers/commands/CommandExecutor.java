package project.api.rest.numbers.commands;

import project.api.rest.numbers.requesthandler.RequestHandler;
import project.api.rest.numbers.result.Result;
import project.api.rest.numbers.status.Status;
import project.api.rest.numbers.type.facts.FactType;

public class CommandExecutor {

    private static final int TRIVIA_PARAMETERS = 1;
    private static final int MATH_PARAMETERS = 1;
    private static final int YEAR_PARAMETERS = 1;
    private static final int DATE_PARAMETERS = 2;

    private static final RequestHandler client = RequestHandler.getInstance();

    public static Result runCommand(Command command) {

        String[] commandTokens = command.getCommand().split("-");
        FactType type = null;

        if (commandTokens.length == Command.COMMAND_TOKENS) {
            String commandType = commandTokens[1];

            for (FactType t : FactType.values()) {
                if (t.getType().equalsIgnoreCase(commandType)) {
                    type = t;
                    break;
                }
            }
        }

        return switch (type) {
            case FactType.TRIVIA -> getTriviaFact(command.getParameters());
            case FactType.MATH -> getMathFact(command.getParameters());
            case FactType.YEAR -> getYearFact(command.getParameters());
            case FactType.DATE -> getDateFact(command.getParameters());
            case FactType.RANDOM -> getRandomFact(command.getParameters());
            case null -> new Result(null, Status.WRONG_COMMAND, null);
        };
    }

    private static Result getTriviaFact(String[] parameters) {
        if (parameters.length != TRIVIA_PARAMETERS) {
            return new Result(null, Status.WRONG_PARAMETERS, FactType.TRIVIA);
        }

        return client.apiResponse(parameters, FactType.TRIVIA);
    }


    private static Result getMathFact(String[] parameters) {
        if (parameters.length != MATH_PARAMETERS) {
            return new Result(null, Status.WRONG_PARAMETERS, FactType.MATH);
        }

        return client.apiResponse(parameters, FactType.MATH);
    }

    private static Result getYearFact(String[] parameters) {
        if (parameters.length != YEAR_PARAMETERS) {
            return new Result(null, Status.WRONG_PARAMETERS, FactType.YEAR);
        }

        return client.apiResponse(parameters, FactType.YEAR);
    }

    private static Result getDateFact(String[] parameters) {
        if (parameters.length != DATE_PARAMETERS) {
            return new Result(null, Status.WRONG_PARAMETERS, FactType.DATE);
        }

        return client.apiResponse(parameters, FactType.DATE);
    }

    private static Result getRandomFact(String[] parameters) {
        if (parameters.length != 0) {
            return new Result(null, Status.WRONG_PARAMETERS, FactType.RANDOM);
        }

        int random = (int) (Math.random() * 100) % 4;

        FactType type = switch (random) {
            case 0 -> FactType.YEAR;
            case 1 -> FactType.DATE;
            case 2 -> FactType.MATH;
            default -> FactType.TRIVIA;
        };

        return client.apiResponse(new String[]{"random"}, type);
    }

}
