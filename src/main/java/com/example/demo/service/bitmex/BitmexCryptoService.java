package com.example.demo.service.bitmex;

import com.example.demo.service.BaseCryptoService;
import com.example.demo.service.CryptoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class BitmexCryptoService extends BaseCryptoService implements CryptoService {
    private static final String SERVICE_URI = "wss://www.bitmex.com/realtime?subscribe=instrument:XBTUSD,trade:XBTUSD";

    private final WebSocketSession session;

    public BitmexCryptoService() throws ExecutionException, InterruptedException {
        StandardWebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        this.session = simpleWebSocketClient.doHandshake(
                new BitmexWebSocketHandler(this::broadcast),
                SERVICE_URI)
        .get();
    }

    @PreDestroy
    public void cleanUp() throws IOException {
        session.close();
    }
}
