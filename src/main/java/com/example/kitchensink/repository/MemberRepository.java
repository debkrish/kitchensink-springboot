package com.example.kitchensink.repository;

import com.example.kitchensink.model.Member;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member, ObjectId> {
    Optional<Member> findByEmail(String email);
}
