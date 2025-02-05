package org.example.atm.model;

import org.example.atm.exceptions.by_atm.AtmBadLoginException;
import org.example.atm.exceptions.by_atm.AtmBadPasswordException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AtmTest {
    ATM atm;
    @BeforeEach
    public void setUp() {
        atm = new ATM("127.0.0.1", 26356, null);
        atm.setPolicy(new ATM.Policy(3, 16, 8));
    }
    @Test
    void checkLoginRequirements() {
        String badLogin = "ww";
        String goodLogin = "SomeGoodLogin";
        try {
            Assertions.assertThrows(AtmBadLoginException.class, () -> atm.getPolicy().checkLoginRequirements(badLogin));
            Assertions.assertDoesNotThrow(() -> atm.getPolicy().checkLoginRequirements(goodLogin));
        } catch (Exception ignored) {}
    }
    @Test
    void checkPasswordRequirements() {
        String badPassword = "qwerty";
        String goodPassword = "11111111";
        try {
            Assertions.assertThrows(AtmBadPasswordException.class, () -> atm.getPolicy().checkPasswordRequirements(badPassword));
            Assertions.assertDoesNotThrow(() -> atm.getPolicy().checkPasswordRequirements(goodPassword));
        } catch (Exception ignored) {}
    }
}
