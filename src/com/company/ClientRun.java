package com.company;

import javax.swing.*;

public class ClientRun {
    public static void main(String[] args) {
        Client client = new Client("192.168.31.112");
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.startClient();
    }
}
