package org.example.paymentsystem.server;

import edu.nstu.magzook.tcp.message.request.Request;
import edu.nstu.magzook.tcp.message.response.Response;
import edu.nstu.magzook.tcp.server.Server;
import org.example.customrequest.*;
import org.example.customresponse.common.*;
import org.example.customresponse.specific.*;
import org.example.paymentsystem.database.DatabaseHandler;
import org.example.paymentsystem.database.DatabaseReply;

public class QiwiWallet_2_0_Server {

    private final Server server;
    private final DatabaseHandler database;

    public QiwiWallet_2_0_Server(int port, String sessionsInfoFolder, String jdbcUrl, String jdbcName, String jdbcPassword) {

        server = new Server(port, sessionsInfoFolder) {
            @Override
            protected Response handleRequest(Request request) {
                return QiwiWallet_2_0_Server.this.handleRequest(request);
            }
        };
        server.enableLogging();
        database = new DatabaseHandler(jdbcUrl, jdbcName, jdbcPassword);
    }

    public void launch() {
        server.launch();
    }

    public void shutdown() {
        server.stop();
    }

    private Response handleRequest(Request request) {
        switch (request) {
            case RequestCreateAccount req -> {
                DatabaseReply<?> reply = database.createNewAccount(req.login, req.password_hash);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseCreated();
                    case FAILURE -> new ResponseLoginIsTaken();
                    default -> new ResponseDatabaseNotAvailable();
                };
            }
            case RequestDoesAccountExist req -> {
                DatabaseReply<?> reply = database.findAccount(req.login);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseDoesAccountExist(true);
                    case FAILURE -> new ResponseDoesAccountExist(false);
                    default -> new ResponseDatabaseNotAvailable();
                };
            }
            case RequestAuth req -> {
                DatabaseReply<?> reply = database.auth(req.login, req.password_hash);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseAuth(true, (AccountDetails) reply.data);
                    case FAILURE -> new ResponseAuth(false, null);
                    default -> new ResponseDatabaseNotAvailable();
                };
            }
            case RequestChangeLogin req -> {
                DatabaseReply<?> reply = database.changeLogin(req.login, req.newLogin);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseChanged();
                    case FAILURE -> new ResponseLoginIsTaken();
                    case CONCURRENT_MODIFICATION -> new ResponseDatabaseConcurrentModification();
                    case NO_CONNECTION -> new ResponseDatabaseNotAvailable();
                };
            }
            case RequestChangePassword req -> {
                DatabaseReply<?> reply = database.changePassword(req.login, req.newPasswordHash);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseChanged();
                    case CONCURRENT_MODIFICATION -> new ResponseDatabaseConcurrentModification();
                    default -> new ResponseDatabaseNotAvailable();
                };
            }
            case RequestDeleteAccount req -> {
                DatabaseReply<?> reply = database.deleteAccount(req.login);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseDeleted();
                    case CONCURRENT_MODIFICATION -> new ResponseDatabaseConcurrentModification();
                    default -> new ResponseDatabaseNotAvailable();
                };
            }
            case RequestDeposit req -> {
                DatabaseReply<?> reply = database.deposit(req.receiver, req.sum);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseDeposited();
                    case CONCURRENT_MODIFICATION -> new ResponseDatabaseConcurrentModification();
                    default -> new ResponseDatabaseNotAvailable();
                };
            }
            case RequestWithdraw req -> {
                DatabaseReply<?> reply = database.withdraw(req.login, req.sum);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseWithdrew();
                    case CONCURRENT_MODIFICATION -> new ResponseDatabaseConcurrentModification();
                    default -> new ResponseDatabaseNotAvailable();
                };
            }
            case RequestTransfer req -> {
                DatabaseReply<?> reply = database.transfer(req.sourceLogin, req.destinationLogin, req.sum);
                return switch (reply.status) {
                    case SUCCESS -> new ResponseTransferred();
                    case CONCURRENT_MODIFICATION -> new ResponseDatabaseConcurrentModification();
                    default -> new ResponseDatabaseNotAvailable();
                };
            }
            default -> {
                return new ResponseBadRequest();
            }
        }

    }
}

