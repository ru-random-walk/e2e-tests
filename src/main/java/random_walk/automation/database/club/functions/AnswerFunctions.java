package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Answer;
import random_walk.automation.database.club.repos.AnswerRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerFunctions {

    private final AnswerRepository answerRepository;

    public List<Answer> getByApprovementId(UUID approvementId) {
        return answerRepository.findByApprovementId(approvementId);
    }
}
