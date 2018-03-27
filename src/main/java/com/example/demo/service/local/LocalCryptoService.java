package com.example.demo.service.local;

import com.example.demo.controller.ws.Message;
import com.example.demo.domain.Trade;
import com.example.demo.repository.TradesRepository;
import com.example.demo.service.BaseCryptoService;
import com.example.demo.service.CryptoService;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocalCryptoService extends BaseCryptoService implements CryptoService {

    private final TradesRepository tradesRepository;
    private final WalletService walletService;

    // Better use transactions
    @Override
    @PreAuthorize("isAuthenticated()")
    public void trade(Message<Message.Trade> trade) {
        try {
            walletService.withdraw(trade);
            walletService.adjust(trade);
            Trade storedTrade = doStoreTrade(trade);
            Message<Message.Trade> tradeMessage = LocalMessageMapper.tradeToMessage(storedTrade);

            broadcast(tradeMessage);
        } catch (Exception e) {
            walletService.rollback(trade);
        }
    }

    private Trade doStoreTrade(Message<Message.Trade> tradeMessage) {
        return tradesRepository.save(LocalMessageMapper.messageToTrade(tradeMessage));
    }
}
