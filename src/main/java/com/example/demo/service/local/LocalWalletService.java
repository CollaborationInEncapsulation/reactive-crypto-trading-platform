package com.example.demo.service.local;

import java.security.Principal;

import com.example.demo.controller.ws.Message;
import com.example.demo.repository.WalletRepository;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocalWalletService implements WalletService {

	private final WalletRepository walletRepository;

	@Override
	public Flux<Message<Float>> stateStream() {
		return currentUser()
				.flatMapMany(walletRepository::findAllByOwner)
                .map(LocalMessageMapper::walletToMessage);
	}

	private static Mono<String> currentUser() {
		return ReactiveSecurityContextHolder.getContext()
		                                    .map(SecurityContext::getAuthentication)
		                                    .map(Principal::getName);
	}
}
