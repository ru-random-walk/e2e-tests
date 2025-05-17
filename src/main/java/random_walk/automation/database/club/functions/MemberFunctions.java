package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Member;
import random_walk.automation.database.club.entities.prkeys.MemberPK;
import random_walk.automation.database.club.repos.MemberRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberFunctions {

    private final MemberRepository memberRepository;

    public Member getClubMember(MemberPK memberPK) {
        return memberRepository.findById(memberPK).orElse(null);
    }

    public List<Member> getByClubId(UUID clubId) {
        var result = memberRepository.findByClubId(clubId);
        log.info("Найдены участники клуба {} - {}", clubId, result);
        return result;
    }
}
