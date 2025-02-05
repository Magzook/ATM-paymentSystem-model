Requires:
1) JavaFX
2) JDBC driver + PostgreSQL

How to use:
1. Create a database. Inside it create a table like this:
   CREATE TABLE account(
     id SERIAL PRIMARY KEY,
     login VARCHAR(16) UNIQUE,
     password_hash BYTEA,
     balance NUMERIC(14, 2) DEFAULT 0.00 CHECK (balance >= 0.00)
   );
2. Go to paymentSystem/src/main/resources/settings.json. Specify the JDBC url, name and password (example is provided). Specify the folder which will save info about sessions (example is provided)
3. Go to ATM/src/main/resources/settings.json. Specify the file which will save info about a certain ATM session (example is provided).
4. Go to paymentSystem\src\main\java\org\example\paymentsystem\Main.java. Launch the payment system server
5. Go to ATM\src\main\java\org\example\atm\Main.java. Launch the ATM.

You can:
1) Create, edit and delete accounts
2) Deposit, withdraw and transfer money

Modules:
1. paymentSystem - the server which accepts requests from ATM and sends responses
2. ATM - an ATM with GUI
3. tcp-session - custom classes for the Client-Server model.
4. message - classes of messages which ATM and paymentSystem use to communicate.


More detailed documentation with examples (Russian):
[РГЗ ИПО Тараторкин вар 9.pdf](https://github.com/user-attachments/files/18671587/9.pdf)
