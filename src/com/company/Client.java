package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {
    private JTextField userInputText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String message = "";
    private String serverIP;
    private Socket socket;

    public Client(String host){
        super("Клиентская часть.");
        serverIP = host;
        userInputText = new JTextField();
        userInputText.setEditable(false);
        userInputText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendMessage(e.getActionCommand());
                        userInputText.setText("");
                    }
                }
        );
        add(userInputText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        chatWindow.setBackground(Color.LIGHT_GRAY);
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 600);
        setVisible(true);
    }


    public void startClient(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }
        catch (IOException ioexception){
            showMessage("\nКлиент разорвал соединение");
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        finally {
            closeConnection();
        }
    }



    private void connectToServer() throws IOException{
        showMessage("Попытка подключения...\n");
        socket = new Socket(InetAddress.getByName(serverIP), 7777);
        showMessage("Подключение к " + socket.getInetAddress().getHostName() + " установалено");
    }


    private void setupStreams() throws IOException{
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        showMessage("\nПотоки настроены");
    }




    private void whileChatting()throws IOException{
            readyToType(true);
        do{
            try{
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            }
            catch (ClassNotFoundException classNotFoundException){
                showMessage("\nОшибка ввода");
            }
        }while (!message.equals("СЕРВЕР- *"));
    }


    private void closeConnection(){
        showMessage("\nЗакрытие соединения");
        readyToType(false);
        try{
            outputStream.close();
            inputStream.close();
            socket.close();
        }
        catch (IOException ioexception){
            ioexception.printStackTrace();
        }
    }


    private  void sendMessage(String message){
        try{
            outputStream.writeObject("КЛИЕНТ- " + message);
            outputStream.flush();
            showMessage("\nКЛИЕНТ- " + message);
        }
        catch (IOException ioexception){
            chatWindow.append("\nОшибка отправки сообщения...");
        }
    }



    private void showMessage(final String message){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(message);
                    }
                }
        );
    }


    private void readyToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userInputText.setEditable(tof);
                    }
                }
        );
    }

}
