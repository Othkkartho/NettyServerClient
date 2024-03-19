package org.example.util;

import org.example.noclosetest.client.EchoClient;

import java.util.Scanner;

public class testClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EchoClient echoClient = new EchoClient();

        String message;

        do {
            message = scanner.nextLine();
            message.concat("\n");

            echoClient.start(message);

        } while (!"quit".equals(message));

        echoClient.close();
    }
}
