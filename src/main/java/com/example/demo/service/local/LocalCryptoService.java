package com.example.demo.service.local;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.domain.Trade;
import com.example.demo.repository.TradesRepository;
import com.example.demo.service.CryptoService;
import com.example.demo.service.Message;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocalCryptoService implements CryptoService {

	final TradesRepository tradesRepository;

	@Override
	public List<Message<?>> retrieve(Long timestamp) {
		List<Message<?>> result = new ArrayList<>();
		List<Trade> queryResult = tradesRepository.findAllByTimestampIsGreaterThanEqual(timestamp);

		for (Trade t : queryResult) {
			result.add(TradesMapper.tradeToMessage(t));
		}

		return result;
	}

	@Override
	public void store(Message<Message.Trade> tradeOffer) {
		tradesRepository.save(TradesMapper.messageToTrade(tradeOffer));
	}
}
