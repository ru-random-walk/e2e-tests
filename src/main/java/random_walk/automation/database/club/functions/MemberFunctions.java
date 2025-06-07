package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Member;
import random_walk.automation.database.club.entities.prkeys.MemberPK;
import random_walk.automation.database.club.repos.MemberRepository;
import ru.testit.annotations.Description;
import ru.testit.annotations.Step;
import ru.testit.annotations.Title;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberFunctions {

    private final MemberRepository memberRepository;

    @Step
    @Title("AND: Получена информация о пристутствии пользователя в клубе")
    @Description("Запись из таблицы member по ключу = {memberPK}")
    public Member getClubMember(MemberPK memberPK) {
        return memberRepository.findById(memberPK).orElse(null);
    }

    @Step
    @Title("AND: Получены участники клуба из базы данных")
    @Description("Записи из таблицы member по club_id = {clubId}")
    public List<Member> getByClubId(UUID clubId) {
        var result = memberRepository.findByClubId(clubId);
        log.info("Найдены участники клуба {} - {}", clubId, result);
        return result;
    }
}
