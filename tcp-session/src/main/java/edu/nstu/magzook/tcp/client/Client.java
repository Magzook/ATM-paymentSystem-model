package edu.nstu.magzook.tcp.client;

import edu.nstu.magzook.tcp.message.request.Request;
import edu.nstu.magzook.tcp.message.request.RequestHello;
import edu.nstu.magzook.tcp.message.request.RequestHelloAgain;
import edu.nstu.magzook.tcp.message.response.Response;
import edu.nstu.magzook.tcp.message.response.ResponseHello;
import edu.nstu.magzook.tcp.message.response.ResponseHelloAgain;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public abstract class Client {
    private final String serverIP;
    private final int serverPort;
    private final String sessionInfoFilePath;
    private SessionInfo sessionInfo;
    private boolean loggingEnabled = false;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;


    public Client(String serverIP, int serverPort, String sessionInfoFilePath) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.sessionInfoFilePath = sessionInfoFilePath;
    }

    public void openConnection() throws IOException {
        connect();
        createListenerThread();
        loadSessionInfoFromFile();
        Request firstRequest = prepareFirstRequest();
        sendRequest(firstRequest);
    }

    public void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connect() throws IOException {
        socket = new Socket(serverIP, serverPort);
        // Обязательно: сначала вызываем socket.getOutputStream(), потом socket.getInputStream(), иначе thread заблокируется.
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
    }

    private void createListenerThread() {
        new Thread(() -> {
            while (true) {
                try {
                    Response response = (Response) inputStream.readObject();
                    handleAnyResponse(response);
                } catch (SocketException | EOFException e) {
                    closeConnection();
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private Request prepareFirstRequest() {
        Request firstRequest;
        if (sessionInfo == null) {
            firstRequest = new RequestHello();
            sessionInfo = new SessionInfo(null, null);
        }
        else {
            firstRequest = new RequestHelloAgain(
                    sessionInfo.sessionID
            );
        }
        return firstRequest;
    }

    public boolean sendRequest(Request request) {
        try {
            assignIdToRequest(request);
            outputStream.writeObject(request);
            sessionInfo.lastRequest = request;
            saveSessionInfoToFile();
            return true;
        } catch (IOException | NullPointerException e) {
            try {
                openConnection();
                sendRequest(request);
                return true;
            } catch (IOException ex) {
                return false;
            }
        }
    }

    private void assignIdToRequest(Request request) throws NullPointerException {
        if (request instanceof RequestHello) {
            request.requestID = 1;
        }
        else {
            request.requestID = sessionInfo.lastRequest.requestID + 1;
        }
    }

    private void handleAnyResponse(Response response) {
        if (response instanceof ResponseHello resp) {
            handleResponseHello(resp);
        }
        else if (!(response instanceof ResponseHelloAgain resp)) {
            handleResponse(response);
        }
    }

    private void handleResponseHello(ResponseHello hello) {
        sessionInfo.sessionID = hello.sessionID;
        saveSessionInfoToFile();
    }

    protected abstract void handleResponse(Response response);

    private static class SessionInfo implements Serializable {
        private String sessionID;
        private Request lastRequest;
        private SessionInfo(String sessionID, Request lastRequest) {
            this.sessionID = sessionID;
            this.lastRequest = lastRequest;
        }
        @Override
        public String toString() {
            return "sessionID:" + sessionID + " lastRequestID:" + lastRequest.requestID + " lastRequest:" + lastRequest.getClass().getSimpleName();
        }
    }

    private void loadSessionInfoFromFile() {
        try (FileInputStream fis = new FileInputStream(sessionInfoFilePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            sessionInfo = (SessionInfo) ois.readObject();
        } catch (FileNotFoundException e) {

        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveSessionInfoToFile() {
        try (FileOutputStream fis = new FileOutputStream(sessionInfoFilePath);
             ObjectOutputStream oos = new ObjectOutputStream(fis)) {
            oos.writeObject(sessionInfo);
            if (loggingEnabled)
                System.out.println("SAVED" +
                    " sessionID:" + sessionInfo.sessionID +
                    " requestID:" + sessionInfo.lastRequest.requestID +
                    " requestType:" + sessionInfo.lastRequest.getClass().getSimpleName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enableLogging() {loggingEnabled = true;}
    public void disableLogging() {loggingEnabled = false;}
}
