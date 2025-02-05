package org.example.atm.model;

import edu.nstu.magzook.tcp.client.Client;
import edu.nstu.magzook.tcp.message.request.Request;
import edu.nstu.magzook.tcp.message.response.Response;
import org.example.atm.exceptions.by_atm.*;
import org.example.atm.exceptions.by_server.*;
import org.example.customrequest.*;
import org.example.customresponse.common.ResponseDatabaseConcurrentModification;
import org.example.customresponse.specific.*;
import org.example.customresponse.common.ResponseChanged;
import org.example.customresponse.common.ResponseDatabaseNotAvailable;
import org.example.customresponse.common.ResponseLoginIsTaken;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ATM {
    private Client client;
    private final String serverIP;
    private final int serverPort;
    private final String sessionInfoFilePath;

    private Response response;
    private String receiver;
    private boolean authenticated = false;
    private String currentLogin = null;
    private Double currentBalance = null;
    private Policy policy = new Policy(3, 16, 8);
    public final Object reconnectionMonitor = new Object();
    private boolean connectionState = false;

    Connector connector = new Connector(5000);

    public ATM(String serverIP, int serverPort, String sessionInfoFilePath) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.sessionInfoFilePath = sessionInfoFilePath;
    }

    public boolean getConnectionState() {
        return connectionState;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void connectToServer() throws AtmBadIpException, AtmBadPortException {
        client = new Client(serverIP, serverPort, sessionInfoFilePath) {
            @Override
            protected void handleResponse(Response response) {
                ATM.this.response = response;
                synchronized (ATM.this) {
                    ATM.this.notify();
                }
            }
        };
        client.enableLogging();
        connector.startConnectingThread();
    }

    public void disconnectFromServer() {
        connector.terminateConnectingThread();
        client.closeConnection();
    }

    public void createNewAccount(String login, String password)
            throws AtmNoConnectionException, AtmBadLoginException,
            AtmBadPasswordException, LoginIsAlreadyTakenException, DatabaseNotAvailableException {
        policy.checkLoginRequirements(login);
        policy.checkPasswordRequirements(password);

        Request request = new RequestCreateAccount(login, hashPassword(password));
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseCreated ignored -> {}
            case ResponseLoginIsTaken ignored -> throw new LoginIsAlreadyTakenException();
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public void setReceiver(String receiver)
            throws AtmNoConnectionException, NoSuchReceiverException, DatabaseNotAvailableException {
        Request request = new RequestDoesAccountExist(receiver);
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseDoesAccountExist r -> {
                if (r.exists)
                    this.receiver = receiver;
                else
                    throw new NoSuchReceiverException(receiver);
            }
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public void deposit(double sum)
            throws AtmIllegalSumException, AtmReceiverNotSpecifiedException,
            AtmNoConnectionException, DatabaseNotAvailableException, DatabaseConcurrentModificationException {
        if (receiver == null) throw new AtmReceiverNotSpecifiedException();
        if (sum <= 0) throw new AtmIllegalSumException("Сумма должна быть положительным числом!");

        RequestDeposit request = new RequestDeposit(receiver, sum);
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseDeposited ignored -> {}
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            case ResponseDatabaseConcurrentModification ignored -> throw new DatabaseConcurrentModificationException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public void auth(String login, String password)
            throws AtmNoConnectionException, AuthFailedException, DatabaseNotAvailableException {
        Request request = new RequestAuth(login, hashPassword(password));
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseAuth r -> {
                if (r.success) {
                    authenticated = true;
                    currentLogin = login;
                    currentBalance = r.accountDetails.balance();
                }
                else
                    throw new AuthFailedException();
            }
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public void changeLogin(String newLogin)
            throws AtmBadLoginException, AtmNotAuthenticatedException, AtmNoConnectionException,
            LoginIsAlreadyTakenException, DatabaseNotAvailableException, DatabaseConcurrentModificationException {
        if (!authenticated) throw new AtmNotAuthenticatedException();
        policy.checkLoginRequirements(newLogin);

        Request request = new RequestChangeLogin(currentLogin, newLogin);
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseChanged ignored -> currentLogin = newLogin;
            case ResponseLoginIsTaken ignored -> throw new LoginIsAlreadyTakenException();
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            case ResponseDatabaseConcurrentModification ignored -> throw new DatabaseConcurrentModificationException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public void changePassword(String newPassword)
            throws AtmBadPasswordException, AtmNotAuthenticatedException,
            AtmNoConnectionException, DatabaseNotAvailableException, DatabaseConcurrentModificationException {
        if (!authenticated) throw new AtmNotAuthenticatedException();
        policy.checkPasswordRequirements(newPassword);

        Request request = new RequestChangePassword(currentLogin, hashPassword(newPassword));
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseChanged ignored -> {}
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            case ResponseDatabaseConcurrentModification ignored -> throw new DatabaseConcurrentModificationException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public void deleteAccount()
            throws AtmNotAuthenticatedException, AtmDeleteAccountWithMoneyException,
            AtmNoConnectionException, DatabaseNotAvailableException, DatabaseConcurrentModificationException {
        if (!authenticated) throw new AtmNotAuthenticatedException();
        if (currentBalance != 0) throw new AtmDeleteAccountWithMoneyException();

        Request request = new RequestDeleteAccount(currentLogin);
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseDeleted ignored -> logout();
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            case ResponseDatabaseConcurrentModification ignored -> throw new DatabaseConcurrentModificationException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public void withdraw(double sum)
            throws AtmNotAuthenticatedException, AtmIllegalSumException, AtmNoConnectionException,
            DatabaseNotAvailableException, DatabaseConcurrentModificationException {
        if (!authenticated) throw new AtmNotAuthenticatedException();
        if (sum <= 0) throw new AtmIllegalSumException("Сумма должна быть положительным числом!");
        if (sum > currentBalance) throw new AtmIllegalSumException("Сумма не должна превышать баланс!");

        Request request = new RequestWithdraw(currentLogin, sum);
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseWithdrew ignored -> {}
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            case ResponseDatabaseConcurrentModification ignored -> throw new DatabaseConcurrentModificationException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public void transfer(double sum)
            throws AtmNotAuthenticatedException, AtmReceiverNotSpecifiedException, AtmIllegalSumException,
            AtmNoConnectionException, DatabaseNotAvailableException, DatabaseConcurrentModificationException {
        if (!authenticated) throw new AtmNotAuthenticatedException();
        if (sum <= 0) throw new AtmIllegalSumException("Сумма должна быть положительным числом!");
        if (sum > currentBalance) throw new AtmIllegalSumException("Сумма не должна превышать баланс!");

        if (receiver == null) throw new AtmReceiverNotSpecifiedException();

        Request request = new RequestTransfer(currentLogin, receiver, sum);
        Response response = sendRequestAndWaitForResponse(request);

        switch (response) {
            case ResponseTransferred ignored -> {}
            case ResponseDatabaseNotAvailable ignored -> throw new DatabaseNotAvailableException();
            case ResponseDatabaseConcurrentModification ignored -> throw new DatabaseConcurrentModificationException();
            default -> throw new RuntimeException("Unexpected response: " + response.getClass().getSimpleName());
        }
    }

    public String getReceiver() {
        return receiver;
    }

    public double getCurrentBalance() throws AtmNotAuthenticatedException {
        if (!authenticated) throw new AtmNotAuthenticatedException();
        return currentBalance;
    }

    public String getCurrentLogin() throws AtmNotAuthenticatedException {
        if (!authenticated) throw new AtmNotAuthenticatedException();
        return currentLogin;
    }

    public void logout() {
        authenticated = false;
        currentLogin = null;
        currentBalance = null;
    }

    private Response sendRequestAndWaitForResponse(Request request) throws AtmNoConnectionException {
        if (!client.sendRequest(request)) {
            connectionState = false;
            connector.awakenConnectingThread();
            throw new AtmNoConnectionException();
        }

        synchronized (this) {
            try {
                if (response == null) wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Response temp = response;
        response = null;
        return temp;
    }

    private static byte[] hashPassword(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private class Connector {
        private boolean threadIsActive = true;
        private int delay;
        private Connector(int delay) {
            this.delay = delay;
        }
        private final Object monitor = new Object();
        private final Thread connectingThread = new Thread(() -> {
            while (threadIsActive) {
                while (true) {
                    try {
                        client.openConnection();
                        connectionState = true;
                        synchronized (reconnectionMonitor) {
                            reconnectionMonitor.notifyAll();
                        }
                        break;
                    } catch (UnknownHostException | IllegalArgumentException e) {
                    } catch (IOException e) {
                    }
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        threadIsActive = false;
                        break;
                    }
                }
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        threadIsActive = false;
                    }
                }
            }
        });
        private void startConnectingThread() {
            connectingThread.start();
        }
        private void terminateConnectingThread() {
            connectingThread.interrupt();
        }
        private void awakenConnectingThread() {
            synchronized (monitor) {
                monitor.notify();
            }
        }
    }

    public record Policy(int loginLengthBottom, int loginLengthTop, int passwordLengthBottom) {

        public void checkLoginRequirements(String login) throws AtmBadLoginException {
            if (login.length() < loginLengthBottom || login.length() > loginLengthTop) {
                throw new AtmBadLoginException(
                        "Логин должен быть от " + loginLengthBottom + " до " + loginLengthTop + " символов"
                );
            }
        }

        public void checkPasswordRequirements(String password) throws AtmBadPasswordException {
            if (password.length() < passwordLengthBottom) {
                throw new AtmBadPasswordException("Пароль должен быть от " + passwordLengthBottom + " символов");
            }
        }
    }
}
