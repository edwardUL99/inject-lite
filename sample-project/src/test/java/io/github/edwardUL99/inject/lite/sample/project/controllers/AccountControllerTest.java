package io.github.edwardUL99.inject.lite.sample.project.controllers;

import io.github.edwardUL99.inject.lite.sample.project.models.Account;
import io.github.edwardUL99.inject.lite.sample.project.services.AccountService;
import io.github.edwardUL99.inject.lite.testing.MockDependency;
import io.github.edwardUL99.inject.lite.testing.TestInject;
import io.github.edwardUL99.inject.lite.testing.junit.TestInjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(TestInjectionExtension.class)
public class AccountControllerTest {
    // replace account service bean with a mock
    @MockDependency("accountServiceBean")
    private AccountService mockAccountService;

    // instantiate the variable, injecting dependencies
    @TestInject
    private AccountController accountController;

    @Test
    public void shouldAuthenticate() {
        String username = "username";
        String password = "password";
        Account account = new Account(1, username, "email", password);

        when(mockAccountService.getAccount(username))
                .thenReturn(Optional.of(account));

        boolean authenticated = accountController.authenticate(username, password);

        assertTrue(authenticated);
        verify(mockAccountService).getAccount(username);
    }

    @Test
    public void shouldNotAuthenticateIfIncorrectPassword() {
        String username = "username";
        String password = "password";
        Account account = new Account(1, username, "email", password);

        when(mockAccountService.getAccount(username))
                .thenReturn(Optional.of(account));

        boolean authenticated = accountController.authenticate(username, password + "1");

        assertFalse(authenticated);
        verify(mockAccountService).getAccount(username);
    }

    @Test
    public void shouldNotAuthenticateIfAccountNotFound() {
        String username = "username";
        String password = "password";

        when(mockAccountService.getAccount(username))
                .thenReturn(Optional.empty());

        boolean authenticated = accountController.authenticate(username, password);

        assertFalse(authenticated);
        verify(mockAccountService).getAccount(username);
    }

    @Test
    public void shouldFindAccountById() {
        int id = 1;
        String username = "username";
        String password = "password";
        Account account = new Account(id, username, "email", password);

        when(mockAccountService.getAccounts())
                .thenReturn(Collections.singletonList(account));

        Account found = accountController.findAccountById(id);

        assertEquals(found, account);
        verify(mockAccountService).getAccounts();
    }
}
