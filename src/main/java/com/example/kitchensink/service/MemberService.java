package com.example.kitchensink.service;

import com.example.kitchensink.exception.BusinessValidationException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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

    public Optional<Member> findById(ObjectId id) {
        return memberRepository.findById(id);
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public boolean deleteById(ObjectId id) {
        memberRepository.deleteById(id);
        return false;
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member createMember(Member member) {
        validateMember(member, null);
        return memberRepository.save(member);
    }

    public Optional<Member> updateMember(ObjectId id, Member updatedMember) {
        validateMember(updatedMember, id);
        return memberRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedMember.getName());
                    existing.setEmail(updatedMember.getEmail());
                    existing.setPhoneNumber(updatedMember.getPhoneNumber());
                    return memberRepository.save(existing);
                });
    }

    private void validateMember(Member member, ObjectId currentMemberId) {
        // Email uniqueness
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(currentMemberId)) {
                        throw new BusinessValidationException("Email is already in use.");
                    }
                });

        // Phone format check
        if (!member.getPhoneNumber().matches("^(\\+\\d{1,3}[- ]?)?\\d{10}$")) {
            throw new BusinessValidationException("Invalid phone number format.");
        }
    }

}
