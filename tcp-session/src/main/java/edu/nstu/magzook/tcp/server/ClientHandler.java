package edu.nstu.magzook.tcp.server;

import edu.nstu.magzook.tcp.message.request.Request;
import edu.nstu.magzook.tcp.message.request.RequestHello;
import edu.nstu.magzook.tcp.message.request.RequestHelloAgain;
import edu.nstu.magzook.tcp.message.response.Response;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

public class ClientHandler extends Thread {

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Server server;
    private String sessionID;

    protected ClientHandler(Socket _socket, Server _server) {
        socket = _socket;
        try {
            // Обязательно: сначала вызываем socket.getOutputStream(), потом socket.getInputStream(), иначе thread заблокируется.
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server = _server;
    }

    public void run() {
        while (true) {
            try {
                Request request = (Request) inputStream.readObject();
                if (request instanceof RequestHello) {
                    sessionID = UUID.randomUUID().toString();
                    if (server.loggingEnabled)
                        System.out.println("Unknown client connected. New session ID: " + sessionID);
                }
                else if (request instanceof RequestHelloAgain req) {
                    sessionID = req.sessionID;
                    if (server.loggingEnabled)
                        System.out.println("Client " + sessionID + " connected");
                }
                Response response = server.handleAnyRequest(sessionID, request);
                sendResponse(response);
                saveSessionInfoToFile(new SessionInfo(request.requestID, response));

            } catch (SocketException | EOFException e) {
                if (server.loggingEnabled)
                    System.out.println("Client " + sessionID + " disconnected");
                closeAll();
                break;
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void closeInput() {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void closeAll() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendResponse(Response response) {
        try {
            outputStream.writeObject(response);
        } catch (SocketException ignored) {
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static class SessionInfo implements Serializable {
        private int requestID;
        private Response response;
        private SessionInfo(int requestID, Response response) {
            this.requestID = requestID;
            this.response = response;
        }
        @Override
        public String toString() {
            return "lastRequestID:" + requestID + " response:" + response.getClass().getSimpleName();
        }
    }

    private void saveSessionInfoToFile(SessionInfo sessionInfo) {
        String filePath = server.getSessionsInfoFolder() + sessionID + ".bin";
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(sessionInfo);
            if (server.loggingEnabled)
                System.out.println("SAVED for sessionID " + sessionID + ":" +
                    " requestID:" + sessionInfo.requestID +
                    " responseType:" + sessionInfo.response.getClass().getSimpleName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
