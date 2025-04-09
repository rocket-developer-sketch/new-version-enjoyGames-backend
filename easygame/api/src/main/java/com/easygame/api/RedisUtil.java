package com.easygame.api;

import com.easygame.api.exception.DuplicateSubmissionException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RedisUtil {
    private final RedisProvider redisProvider;

    public boolean isDuplicateSubmission(String nickName, String gameTypeStr) throws DuplicateSubmissionException {
        String key = "score:lock:" + nickName + ":" + gameTypeStr;

        if (Boolean.FALSE.equals(redisProvider.isFirstWithinTTL(key))) {
            throw new DuplicateSubmissionException("Score already submitted for this game.");
        }

        return false;
    }
}
