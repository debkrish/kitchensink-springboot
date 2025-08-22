package com.example.kitchensink.service;

import com.example.kitchensink.exception.BusinessValidationException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public boolean deleteById(String id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return true; // indicate success
        }
        return false; // no such record
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member createMember(Member member) {
        validateMember(member, null);
        return memberRepository.save(member);
    }

    public Optional<Member> updateMember(String id, Member updatedMember) {
        validateMember(updatedMember, id);
        return memberRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedMember.getName());
                    existing.setEmail(updatedMember.getEmail());
                    existing.setPhoneNumber(updatedMember.getPhoneNumber());
                    return memberRepository.save(existing);
                });
    }

    private void validateMember(Member member, String currentMemberId) {
        // Email uniqueness
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(currentMemberId)) {
                        throw new BusinessValidationException("Email is already in use.");
                    }
                });

    }

}
