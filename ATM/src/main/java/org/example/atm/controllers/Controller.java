package org.example.atm.controllers;

import org.example.atm.model.ATM;
import org.example.atm.view.AtmView;

public abstract class Controller {
    protected ATM atm;
    protected AtmView atmView;
    public void setAtm(ATM atm) {
        this.atm = atm;
    }
    public void setAtmView(AtmView atmView) {
        this.atmView = atmView;
    }
    public abstract void fillWithInitialData();
}
