package random_walk.automation.database.club.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import random_walk.automation.database.club.entities.Member;
import random_walk.automation.database.club.entities.prkeys.MemberPK;
import random_walk.automation.database.club.repos.MemberRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberFunctions {

    private final MemberRepository memberRepository;

    public Member getClubMember(MemberPK memberPK) {
        return memberRepository.findById(memberPK).orElse(null);
    }

    public List<Member> getByClubId(UUID clubId) {
        return memberRepository.findByClubId(clubId);
    }
}
