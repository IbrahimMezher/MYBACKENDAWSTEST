package com.flutterbackend.reviews.web;

import com.flutterbackend.reviews.domain.Reviews;
import com.flutterbackend.reviews.dto.BrokerReviewResponse;
import com.flutterbackend.reviews.dto.ReviewRequest;
import com.flutterbackend.reviews.service.ReviewsService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewsController {

    private final ReviewsService reviewsService;
    private final CurrentUser currentUser;

    public ReviewsController(ReviewsService reviewsService,
                             CurrentUser currentUser) {
        this.reviewsService = reviewsService;
        this.currentUser = currentUser;
    }

    @PostMapping("/create")
    public String createReview(@RequestBody ReviewRequest body,
                               HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return reviewsService.create(body, user);
    }

    @GetMapping("/policies/{policyId}")
    public List<Reviews> getPolicyReviews(@PathVariable Long policyId) {
        return reviewsService.getByPolicy(policyId);
    }

    @GetMapping("/broker")
    @PreAuthorize("hasAnyRole('broker','admin','superadmin')")
    public List<BrokerReviewResponse> getBrokerReviews(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return reviewsService.getForBroker(user);
    }
}
