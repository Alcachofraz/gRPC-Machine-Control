package server;

import io.grpc.*;
import io.grpc.stub.StreamObserver;
import machine_control.Control;
import machine_control.Machine;
import machine_control.MachineControlGrpc;

import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

public class Server extends MachineControlGrpc.MachineControlImplBase {

    public static HashMap<Integer, StreamObserver<Control>> machines;

    public static final Logger logger = Logger.getLogger(Server.class.getName());

    public static int serverPort = 9000;

    public static void main(String[] args) {
        try {
            machines = new HashMap<>();

            final io.grpc.Server svc = ServerBuilder.forPort(serverPort)
                    .addService(new Server())
                    .build()
                    .start();

            logger.info("SERVER: Server started, listening on " + serverPort + "...");

            String instructions = "OPTIONS:\n1 - Send instruction\n2 - Exit";
            Scanner input = new Scanner(System.in);
            String cmd;
            while(true) {
                System.out.println(instructions);
                cmd = input.nextLine();
                if (cmd.equals("1")) {
                    try {
                        System.out.println("MACHINES:");
                        for (int id : machines.keySet()) {
                            System.out.println(id);
                        }
                        int machine = Integer.parseInt(input.nextLine());
                        System.out.println("INSTRUCTION [greeting/farewell]:");
                        String instruction = input.nextLine();
                        machines.get(machine).onNext(Control.newBuilder().setInstruction(instruction).build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (cmd.equals("2")) {
                    break;
                }
            }
            svc.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(Machine request, StreamObserver<Control> responseObserver) {
        if (machines.containsKey(request.getId())) {
            System.out.println("Machine with ID " + request.getId() + " already taken.");
            responseObserver.onError(new StatusException(Status.INVALID_ARGUMENT.withDescription("Machine ID already taken.")));
        }
        else {
            machines.put(request.getId(), responseObserver);
            System.out.println("Machine with ID " + request.getId() + " registered.");
        }
    }
}
