package com.flutterbackend.reviews.repository;

import com.flutterbackend.reviews.domain.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findByPolicy_PolicyId(Long policyId);

    List<Reviews> findByPolicy_Broker_User_UserIdOrderByCreatedAtDesc(Long userId);

    long countByPolicy_PolicyId(Long policyId);

    boolean existsByUser_UserIdAndPolicy_PolicyId(Long userId, Long policyId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Reviews r WHERE r.policy.policyId = :policyId")
    double averageRatingByPolicy(@Param("policyId") Long policyId);

    @Query("SELECT r.policy.policyId, AVG(r.rating), COUNT(r) FROM Reviews r GROUP BY r.policy.policyId")
    List<Object[]> aggregateRatingsForAllPolicies();
}
