package com.jbase;

public class Main {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        System.out.println(new Main().getGreeting());
    }
}
