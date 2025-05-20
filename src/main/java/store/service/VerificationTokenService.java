package store.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class VerificationTokenService {

    private static final Map<String, VerificationData> verificationStorage = new HashMap<>();

    private static final int EXPIRATION_MINUTES = 10;

    public String generateCode(String mail) {
        String code = String.format("%06d", new Random().nextInt(999999));
        verificationStorage.put(mail, new VerificationData(code, LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES)));
        return code;
    }

    public boolean verifyCode(String mail, String code) {
        VerificationData data = verificationStorage.get(mail);
        if (data == null || data.getExpireAt().isBefore(LocalDateTime.now())) return false;
        return data.getCode().equals(code);
    }

    static class VerificationData {
        private final String code;
        private final LocalDateTime expireAt;

        public VerificationData(String code, LocalDateTime expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getExpireAt() {
            return expireAt;
        }
    }
}
