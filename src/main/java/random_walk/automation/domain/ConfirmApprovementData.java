package random_walk.automation.domain;

import lombok.Data;

@Data
public class ConfirmApprovementData {

    private String type;

    private Integer approversToNotifyCount;

    private Integer requiredConfirmationNumber;
}
