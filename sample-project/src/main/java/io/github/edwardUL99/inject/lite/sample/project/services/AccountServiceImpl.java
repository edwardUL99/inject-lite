package io.github.edwardUL99.inject.lite.sample.project.services;

import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.sample.project.models.Account;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// will be only available in container with ID accountsContainer
@ContainerInject("accountsContainer")
@Injectable("accountServiceBean")
public class AccountServiceImpl implements AccountService {
    private final Account fakeAccount;

    public AccountServiceImpl() {
        fakeAccount = new Account(1, "username", "e-mail", "password");
    }

    @Override
    public Optional<Account> getAccount(String username) {
        if (username.equals(fakeAccount.getUsername())) {
            return Optional.of(fakeAccount);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Account> getAccounts() {
        return Collections.singletonList(fakeAccount);
    }
}
