package net.questionbank.service;

import net.questionbank.domain.Member;
import net.questionbank.dto.MemberLoginDTO;
import net.questionbank.dto.MemberRegisterDTO;

public interface MemberServiceIf {
    public Member register(MemberRegisterDTO registerDTO);
    public MemberLoginDTO login(MemberLoginDTO loginDTO);
    public boolean exists(String memberId);
}
