package client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import machine_control.Machine;
import machine_control.MachineControlGrpc;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static final Logger logger = Logger.getLogger(Client.class.getName());

    public static String serverIP = "localhost";
    public static int serverPort = 9000;

    public static int MACHINE_ID = 2;

    public static void main(String[] args) {
        ManagedChannel channel = null;

        try {
            System.out.println("Awaiting server endpoint...");

            // Setup Channel to RingManager
            channel = ManagedChannelBuilder.forAddress(serverIP, serverPort)
                    // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                    // needing certificates.
                    .usePlaintext()
                    .build();

            MachineControlGrpc.MachineControlBlockingStub blockingStub = MachineControlGrpc.newBlockingStub(channel);
            MachineControlGrpc.MachineControlStub nonBlockingStub = MachineControlGrpc.newStub(channel);

            nonBlockingStub.register(Machine.newBuilder().setId(MACHINE_ID).build(), new ControlObserver());

            String instructions = "Enter \"exit\" to close this client.";
            System.out.println(instructions);
            Scanner input = new Scanner(System.in);
            while (!input.nextLine().equals("exit")) {
                System.out.println(instructions);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error:" + ex.getMessage());
        }

        if (channel != null) {
            logger.log(Level.INFO, "Shutdown channel to Server.");
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
