package io.github.edwardUL99.inject.lite.sample.project.controllers;

import io.github.edwardUL99.inject.lite.annotations.ConstructedHook;
import io.github.edwardUL99.inject.lite.annotations.ContainerInject;
import io.github.edwardUL99.inject.lite.annotations.Inject;
import io.github.edwardUL99.inject.lite.annotations.Injectable;
import io.github.edwardUL99.inject.lite.annotations.Lazy;
import io.github.edwardUL99.inject.lite.annotations.Name;
import io.github.edwardUL99.inject.lite.annotations.PreConstructHook;
import io.github.edwardUL99.inject.lite.hooks.Constructed;
import io.github.edwardUL99.inject.lite.hooks.PreConstruct;
import io.github.edwardUL99.inject.lite.injector.Injector;
import io.github.edwardUL99.inject.lite.sample.project.models.Account;
import io.github.edwardUL99.inject.lite.sample.project.services.AccountService;
import io.github.edwardUL99.inject.lite.sample.project.services.ConfigService;

@ContainerInject("accountsContainer")
@Injectable("accountControllerBean")
public class AccountController implements PreConstruct, Constructed {
    private final AccountService accountService;

    @Inject
    public AccountController(AccountService accountService, @Name("configServiceBean") @Lazy ConfigService configService) {
        // The config service will be lazily injected as a proxy. When we use the getConfig method here, config service will be instantiated and the method request forwarded to it
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

    @Override
    public void constructed(Injector injector) {
        System.out.println("AccountController constructed. It is now ready to use");
    }

    // can't be enforced by interface, but PreConstruct marker interface marks the class as having this method.
    // enforced at runtime
    public static void preConstruct() {
        System.out.println("AccountController pre construction");
    }

    @PreConstructHook
    public static void preConstructHook() { System.out.println("Annotated PreConstruct hook"); }

    @ConstructedHook
    public void constructedHook(Injector injector) { System.out.println("Annotated Constructed hook");}
}
