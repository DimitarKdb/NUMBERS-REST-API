package project.api.rest.numbers.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientNIO {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static final int BUFFER_SIZE = 512;
    private static final String STANDARD_CHARSET = "UTF-8";
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            System.out.println("Your connection to the server has been successful!");

            while (true) {
                System.out.print("Enter a command: ");
                String command = scanner.nextLine();

                if (command.equals("disconnect")) {
                    break;
                }

                buffer.clear();
                buffer.put(command.getBytes());
                buffer.flip();
                socketChannel.write(buffer);

                System.out.println("Your request has been made!");

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();

                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, STANDARD_CHARSET);

                System.out.println(reply);
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }

    }

}
