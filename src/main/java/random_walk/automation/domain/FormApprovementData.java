package random_walk.automation.domain;

import club_service.graphql.model.QuestionInput;
import lombok.Data;

import java.util.List;

@Data
public class FormApprovementData {

    private String type;

    private List<QuestionInput> questions;
}
