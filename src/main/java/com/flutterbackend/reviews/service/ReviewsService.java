package com.flutterbackend.reviews.service;

import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.reviews.domain.Reviews;
import com.flutterbackend.reviews.dto.BrokerReviewResponse;
import com.flutterbackend.reviews.dto.ReviewRequest;
import com.flutterbackend.reviews.repository.ReviewsRepository;
import com.flutterbackend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class ReviewsService {

    private final ReviewsRepository reviewsRepository;
    private final PoliciesRepository policiesRepository;
    private final com.flutterbackend.transactions.repository.TransactionsRepository transactionsRepository;

    public ReviewsService(ReviewsRepository reviewsRepository,
                          PoliciesRepository policiesRepository,
                          com.flutterbackend.transactions.repository.TransactionsRepository transactionsRepository) {
        this.reviewsRepository = reviewsRepository;
        this.policiesRepository = policiesRepository;
        this.transactionsRepository = transactionsRepository;
    }

    public String create(ReviewRequest body, User user) {
        Policies policy = policiesRepository.findById(body.getPolicyId())
                .orElseThrow(() -> new RuntimeException("Policy not found"));

        if (body.getRating() == null || body.getRating() < 1 || body.getRating() > 5)
            throw new RuntimeException("Rating must be between 1 and 5");

        boolean owns = transactionsRepository
                .existsByUser_UserIdAndPolicy_PolicyIdAndBrokerStatus(
                        user.getUserId(), body.getPolicyId(), "ACCEPTED");
        if (!owns)
            throw new RuntimeException("You can only review a policy you have purchased");

        if (reviewsRepository.existsByUser_UserIdAndPolicy_PolicyId(user.getUserId(), body.getPolicyId()))
            throw new RuntimeException("You have already reviewed this purchased policy");

        Reviews review = new Reviews();
        review.setUser(user);
        review.setPolicy(policy);
        review.setRating(body.getRating());
        review.setReviewText(body.getReviewText());

        reviewsRepository.save(review);
        return "Review submitted successfully.";
    }

    public List<Reviews> getByPolicy(Long policyId) {
        return reviewsRepository.findByPolicy_PolicyId(policyId);
    }

    public List<BrokerReviewResponse> getForBroker(User brokerUser) {
        return reviewsRepository
                .findByPolicy_Broker_User_UserIdOrderByCreatedAtDesc(brokerUser.getUserId())
                .stream()
                .map(this::toBrokerReviewResponse)
                .toList();
    }

    private BrokerReviewResponse toBrokerReviewResponse(Reviews review) {
        User customer = review.getUser();
        Policies policy = review.getPolicy();
        return BrokerReviewResponse.builder()
                .reviewId(review.getReviewId())
                .policyId(policy != null ? policy.getPolicyId() : null)
                .policyName(policy != null ? policy.getPolicyName() : null)
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .createdAt(review.getCreatedAt())
                .customerName(customer != null ? customer.getFullName() : null)
                .customerEmail(customer != null ? customer.getEmail() : null)
                .customerPhone(customer != null ? customer.getPhoneNumber() : null)
                .build();
    }
}
