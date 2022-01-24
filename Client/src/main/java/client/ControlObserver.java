package client;

import io.grpc.stub.StreamObserver;
import machine_control.Control;

import java.util.Scanner;

public class ControlObserver implements StreamObserver<Control> {

    Scanner input;

    public ControlObserver() {
        input = new Scanner(System.in);
        System.out.println("Machine is waiting for instructions...");
    }

    @Override
    public void onNext(Control control) {
        switch (control.getInstruction()) {
            case "greeting":
                System.out.println("Hello!");
                break;
            case "farewell":
                System.out.println("Goodbye!");
                break;
            default:
                System.out.println("Cheese.");
                break;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        System.out.println("Machine has successfully executed a succession of instructions.");
    }
}
