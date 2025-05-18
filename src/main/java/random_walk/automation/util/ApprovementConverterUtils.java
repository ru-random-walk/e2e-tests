package random_walk.automation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import random_walk.automation.domain.ConfirmApprovementData;
import random_walk.automation.domain.FormApprovementData;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApprovementConverterUtils {

    public static FormApprovementData getFormApprovementData(String dbData) {
        try {
            return new ObjectMapper().readValue(dbData, FormApprovementData.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public static ConfirmApprovementData getConfirmApprovementData(String dbData) {
        try {
            return new ObjectMapper().readValue(dbData, ConfirmApprovementData.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }
}
