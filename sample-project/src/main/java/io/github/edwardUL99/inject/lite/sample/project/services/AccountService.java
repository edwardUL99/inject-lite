package io.github.edwardUL99.inject.lite.sample.project.services;

import io.github.edwardUL99.inject.lite.sample.project.models.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<Account> getAccount(String username);

    List<Account> getAccounts();
}
