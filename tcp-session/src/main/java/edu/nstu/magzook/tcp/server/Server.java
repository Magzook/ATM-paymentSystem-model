package edu.nstu.magzook.tcp.server;

import edu.nstu.magzook.tcp.message.request.Request;
import edu.nstu.magzook.tcp.message.request.RequestHello;
import edu.nstu.magzook.tcp.message.request.RequestHelloAgain;
import edu.nstu.magzook.tcp.message.response.Response;
import edu.nstu.magzook.tcp.message.response.ResponseHello;
import edu.nstu.magzook.tcp.message.response.ResponseHelloAgain;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class Server {

    private final int port;
    private final String sessionsInfoFolder;
    private ServerSocket serverSocket;
    private final ArrayList<ClientHandler> clientHandlers;
    protected boolean loggingEnabled = false;

    public Server(int port, String sessionsInfoFolder) {
        this.port = port;
        this.sessionsInfoFolder = sessionsInfoFolder;
        clientHandlers = new ArrayList<>();
    }

    public String getSessionsInfoFolder() {
        return sessionsInfoFolder;
    }

    public void launch() {
        if (!createServerSocket()) return;
        createClientAcceptingThread();
    }

    public void stop() {
        try {
            serverSocket.close();
            clientHandlers.forEach(ClientHandler::closeInput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean createServerSocket() {
        try {
            serverSocket = new ServerSocket(port);
            return true;
        } catch (IllegalArgumentException e) {
            if (loggingEnabled) System.err.println("Failed to launch server: port out of range 0..65535");
        } catch (IOException e) {
            if (loggingEnabled) e.printStackTrace();
        }
        return false;
    }

    private void createClientAcceptingThread() {
        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket, this);
                    clientHandlers.add(clientHandler);
                    clientHandler.start();
                } catch (SocketException e) {
                    break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    protected Response handleAnyRequest(String sessionID, Request request) {
        if (request instanceof RequestHello) {
            return new ResponseHello(sessionID);
        }
        else if (request instanceof RequestHelloAgain req) {
            return new ResponseHelloAgain();
        }
        else {
            return handleRequest(request);
        }
    }

    protected abstract Response handleRequest(Request request);

    public void enableLogging() {loggingEnabled = true;}
    public void disableLogging() {loggingEnabled = false;}
}