package project.api.rest.numbers.server;

import project.api.rest.numbers.commands.Command;
import project.api.rest.numbers.requesthandler.RequestHandler;
import project.api.rest.numbers.result.Result;
import project.api.rest.numbers.status.Status;
import project.api.rest.numbers.type.facts.FactType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
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

    private static boolean hasClients;
    private static int clientCount;

    private static final RequestHandler client = RequestHandler.getInstance();

    public static void main(String[] args) {

        establishServer();

    }

    private static void establishServer() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {

            bindServerSocketChannel(serverSocketChannel);
            registerSelector(selector, serverSocketChannel);

            ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

            clientCount = 0;
            hasClients = true;

            System.out.println("Server has been set up!");

            handleClients(selector, buffer);
        } catch (IOException e) {
            throw new RuntimeException("There was a problem with server communication!", e);
        }
    }

    private static void handleClients(Selector selector, ByteBuffer buffer) {
        try {
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

                        serviceReadableKey(key, buffer);

                    } else if (key.isAcceptable()) {

                        serviceAcceptableKey(key, selector);

                    }

                    keyIterator.remove();
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while handling clients requests!", e);
        }

        System.out.println("No more clients, server shutting down...");
    }

    private static void bindServerSocketChannel(ServerSocketChannel serverSocketChannel) {
        try {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            throw new RuntimeException("Could not bind server socket channel!", e);
        }
    }

    private static void registerSelector(Selector selector, ServerSocketChannel serverSocketChannel) {
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            throw new RuntimeException("Error while trying to register the server socket in the selector!", e);
        }
    }

    private static void serviceReadableKey(SelectionKey key, ByteBuffer buffer) {
        try {

            SocketChannel socketChannel = (SocketChannel) key.channel();

            buffer.clear();
            int query = socketChannel.read(buffer);

            if (query < 0) {
                clientCount--;
                System.out.println("Client " + socketChannel.getRemoteAddress() + " has closed the connection, clients remaining: " + clientCount + "!");
                socketChannel.close();

                if (clientCount == 0) {
                    hasClients = false;
                }

                return;
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
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to service a readable key!", e);
        }
    }

    private static void serviceAcceptableKey(SelectionKey key, Selector selector) {
        try {
            ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
            SocketChannel accept = sockChannel.accept();
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ);
            clientCount++;
            System.out.println("A new client " + accept.getRemoteAddress() + " has connected, clients connected: " + clientCount + "!");
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while trying to service an acceptable key!", e);
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

        return client.apiResponse(new String[]{"random"}, type);
    }

}