package project.api.rest.numbers.server;

import project.api.rest.numbers.commands.Command;
import project.api.rest.numbers.requesthandler.RequestHandler;
import project.api.rest.numbers.result.Result;
import project.api.rest.numbers.status.Status;
import project.api.rest.numbers.type.facts.FactType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class ServerNIO {

    public static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final String STANDARD_CHARSET = "UTF-8";
    private static final int BUFFER_SIZE = 1024;

    private static final int TRIVIA_PARAMETERS = 1;
    private static final int MATH_PARAMETERS = 1;
    private static final int YEAR_PARAMETERS = 1;
    private static final int DATE_PARAMETERS = 2;

    private static final RequestHandler client = RequestHandler.getInstance();

    public static void main(String[] args) {

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

            boolean hasClients = true;
            int clientCounter = 0;

            System.out.println("Server has been set up!");

            while (hasClients) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();


                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        buffer.clear();
                        int query = socketChannel.read(buffer);

                        if (query < 0) {
                            System.out.println("Client has closed the connection");
                            socketChannel.close();
                            clientCounter--;

                            if(clientCounter == 0) {
                                hasClients = false;
                            }

                            continue;
                        }

                        buffer.flip();

                        byte[] byteArray = new byte[buffer.remaining()];
                        buffer.get(byteArray);
                        String input = new String(byteArray, STANDARD_CHARSET);

                        Command command = Command.extractCommand(input);

                        Result result = runCommand(command);

                        buffer.clear();
                        buffer.put(result.toString().getBytes());

                        buffer.flip();
                        socketChannel.write(buffer);

                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                        System.out.println("A new client has connected!");
                        clientCounter++;
                    }

                    keyIterator.remove();
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Result runCommand(Command command) {

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

        return client.apiResponse(new String[] {"random"}, type);
    }

}