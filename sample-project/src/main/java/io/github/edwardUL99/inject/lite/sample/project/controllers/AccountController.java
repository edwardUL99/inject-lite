package io.github.edwardUL99.inject.lite.sample.project.controllers;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Name;
import io.github.edwardUL99.inject.lite.sample.project.models.Account;
import io.github.edwardUL99.inject.lite.sample.project.services.AccountService;
import io.github.edwardUL99.inject.lite.sample.project.services.ConfigService;

@ContainerInject("accountsContainer")
@Injectable("accountControllerBean")
public class AccountController {
    private final AccountService accountService;

    @Inject
    public AccountController(AccountService accountService, @Name("configServiceBean") ConfigService configService) {
        this.accountService = accountService;
        System.out.println("Injected ConfigService: " + configService + ". Debug enabled: " + configService.getConfig().isDebugEnabled());
    }

    public boolean authenticate(String username, String password) {
        Account account = accountService.getAccount(username).orElse(null);

        if (account == null) {
            return false;
        } else {
            return account.getPassword().equals(password);
        }
    }

    public Account findAccountById(int id) {
        return accountService.getAccounts()
                .stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
