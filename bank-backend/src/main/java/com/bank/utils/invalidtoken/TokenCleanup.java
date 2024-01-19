package com.bank.utils.invalidtoken;

import com.bank.entities.invalidtoken.InvalidToken;
import com.bank.entities.invalidtoken.interfaces.InvalidTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanup {

    private final InvalidTokenRepository invalidTokenRepository;

    @Scheduled(fixedRate = 1000L*60L*2L) //cleanup expired tokens every 2 min, time should be longer in production
    public void cleanupTokens() {
        log.info("Cleaning expired tokens");
        List<InvalidToken> invalidTokens = invalidTokenRepository.getAllByExpirationDateBefore(Instant.now());
        log.info("Deleting {} tokens", invalidTokens.size());
        invalidTokenRepository.deleteAll(invalidTokens);
    }
}
