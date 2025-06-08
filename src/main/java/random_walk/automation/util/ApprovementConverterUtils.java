package random_walk.automation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import random_walk.automation.domain.ConfirmApprovementData;
import random_walk.automation.domain.FormApprovementData;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApprovementConverterUtils {

    @Step
    @Title("AND: Строка с данными о тесте из базы данных сформатирована в нужный объект")
    @Description("Данные - {dbData}")
    public static FormApprovementData getFormApprovementData(String dbData) {
        try {
            return new ObjectMapper().readValue(dbData, FormApprovementData.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Step
    @Title("AND: Строка с данными о подтверждении вступления сформатирована в нужный объект")
    @Description("Данные - {dbData}")
    public static ConfirmApprovementData getConfirmApprovementData(String dbData) {
        try {
            return new ObjectMapper().readValue(dbData, ConfirmApprovementData.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }
}
