package project.api.rest.numbers.commands;

import project.api.rest.numbers.fact.cache.FactCacheSystem;
import project.api.rest.numbers.requesthandler.RequestHandler;
import project.api.rest.numbers.result.Result;
import project.api.rest.numbers.status.Status;
import project.api.rest.numbers.type.facts.FactType;

public class CommandExecutor {

    private static final int YEAR_MATH_TRIVIA_PARAMETERS = 1;

    private static final int DATE_PARAMETERS = 2;

    private static final RequestHandler client = RequestHandler.getInstance();

    private static FactCacheSystem factCache = null;

    public static Result runCommand(Command command) {

        String[] commandTokens = command.getCommand().split("-");
        FactType type = null;
        boolean hasCache = factCache != null;

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
            case FactType.TRIVIA, FactType.MATH, FactType.YEAR-> getFact(command.getParameters(), hasCache, type);
            case FactType.DATE -> getDateFact(command.getParameters(), hasCache);
            case FactType.RANDOM -> getRandomFact(command.getParameters());
            case null -> new Result(null, Status.WRONG_COMMAND, null);
        };
    }

    private static Result getFact(String[] parameters, boolean hasCache, FactType type) {
        if (parameters.length != YEAR_MATH_TRIVIA_PARAMETERS) {
            return new Result(null, Status.WRONG_PARAMETERS, type);
        }

        Result result = null;

        if (hasCache) {
            result = factCache.retrieveFact(parameters, type);
        }

        if (result == null) {
            result = client.apiResponse(parameters, type);

            if (result.status() != Status.NOT_FOUND) {
                factCache.loadFact(result.message(), parameters[0], type);
            }

        }

        return result;
    }


    private static Result getDateFact(String[] parameters, boolean hasCache) {
        if (parameters.length != DATE_PARAMETERS) {
            return new Result(null, Status.WRONG_PARAMETERS, FactType.DATE);
        }

        Result result = null;

        if (hasCache) {
            result = factCache.retrieveFact(parameters, FactType.DATE);
        }

        if (result == null) {

            result = client.apiResponse(parameters, FactType.DATE);

            if (result.status() != Status.NOT_FOUND) {
                factCache.loadFact(result.message(), parameters[1] + "/" + parameters[0], FactType.DATE);
            }

        }

        return result;
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

    public static void setCacheSystem(FactCacheSystem cacheSystem) {
        factCache = cacheSystem;
    }

}
