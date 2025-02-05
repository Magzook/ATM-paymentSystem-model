package org.example.atm.exceptions.by_atm;

public class AtmDeleteAccountWithMoneyException extends Exception{
    public AtmDeleteAccountWithMoneyException(){
        super("Нельзя удалить аккаунт, если на нём лежат деньги");
    }
}
