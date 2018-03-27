package com.example.demo.service.local;

import com.example.demo.controller.ws.Message;
import com.example.demo.domain.Wallet;
import com.example.demo.repository.WalletRepository;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocalWalletService implements WalletService {

	private final ConcurrentHashMap<String, Consumer<Message<Float>>> subscribers = new ConcurrentHashMap<>();
	private final WalletRepository walletRepository;

	@Override
	public Callable<Void> subscriberForStateStream(Consumer<Message<Float>> subscriber) {
		String id = UUID.randomUUID().toString();
		subscribers.put(id, subscriber);

        walletRepository.findAllByOwner(currentUser())
                .forEach(w -> subscriber.accept(LocalMessageMapper.walletToMessage(w)));

		return () -> {
			subscribers.remove(id);
			return null;
		};
	}

	@Override
	public void withdraw(Message<Message.Trade> trade) {
		processWallet(
				wallet -> wallet.withdraw(calculateWithdraw(trade)),
				() -> withdrawCurrency(trade));
	}

	@Override
	public void adjust(Message<Message.Trade> trade) {
		processWallet(
				wallet -> wallet.adjust(calculateAdjust(trade)),
				() -> adjustCurrency(trade));
	}

	@Override
	public void rollback(Message<Message.Trade> trade) {
		processWallet(
				wallet -> wallet.adjust(calculateWithdraw(trade)),
				() -> withdrawCurrency(trade));
	}

	private void processWallet(
			Function<Wallet, Wallet> processor,
			Supplier<String> currencySupplier) {
		String owner = currentUser();

		Wallet wallet = walletRepository.findByOwnerAndCurrency(owner, currencySupplier.get());
		Wallet newWalletState = processor.apply(wallet);
		Wallet afterSave = walletRepository.save(newWalletState);

		broadcastMessage(afterSave);
	}

	private void broadcastMessage(Wallet afterSave) {
		Message<Float> message = LocalMessageMapper.walletToMessage(afterSave);
		subscribers.values()
				.forEach(consumer -> consumer.accept(message));
	}

	private static String currentUser() {
		return SecurityContextHolder.getContext()
				.getAuthentication()
				.getName();
	}

	private static String withdrawCurrency(Message<Message.Trade> trade) {
		boolean isBuyBitcoin = trade.getData()
				.getAmount() > 0;
		return isBuyBitcoin ? "USD" : trade.getCurrency();
	}

	private static String adjustCurrency(Message<Message.Trade> trade) {
		boolean isBuyBitcoin = trade.getData()
				.getAmount() > 0;
		return isBuyBitcoin ? trade.getCurrency() : "USD";
	}

	private static float calculateWithdraw(Message<Message.Trade> trade) {
		boolean isBuyBitcoin = trade.getData()
				.getAmount() > 0;
		return Math.abs(trade.getData()
				.getAmount()) * (isBuyBitcoin ? trade.getData()
				.getPrice() : 1f);
	}

	private static float calculateAdjust(Message<Message.Trade> trade) {
		boolean isBuyBitcoin = trade.getData()
				.getAmount() > 0;
		return Math.abs(trade.getData()
				.getAmount()) * (isBuyBitcoin ? 1f : trade.getData()
				.getPrice());
	}
}
