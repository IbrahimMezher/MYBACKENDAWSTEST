package com.flutterbackend.eveythingPolicies.policies.service;

import com.flutterbackend.broker.domain.Broker;
import com.flutterbackend.broker.repository.BrokerRepository;
import com.flutterbackend.eveythingPolicies.coverage_tiers.domain.CoverageTier;
import com.flutterbackend.eveythingPolicies.coverage_tiers.repository.CoverageTierRepository;
import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.ExclusionType;
import com.flutterbackend.eveythingPolicies.policy_exclusions.repository.ExclusionTypeRepository;
import com.flutterbackend.eveythingPolicies.policies.domain.Policies;
import com.flutterbackend.eveythingPolicies.policies.dto.PolicyRequest;
import com.flutterbackend.eveythingPolicies.policies.dto.PolicyResponse;
import com.flutterbackend.eveythingPolicies.policies.repository.PoliciesRepository;
import com.flutterbackend.reviews.repository.ReviewsRepository;
import com.flutterbackend.eveythingPolicies.policycategories.domain.PolicyCategories;
import com.flutterbackend.eveythingPolicies.policycategories.repository.PolicyCategoriesRepository;
import com.flutterbackend.eveythingPolicies.policy_duration.domain.PolicyDuration;
import com.flutterbackend.eveythingPolicies.policy_duration.repository.PolicyDurationRepository;
import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.PolicyExclusion;
import com.flutterbackend.eveythingPolicies.policy_exclusions.repository.PolicyExclusionRepository;
import com.flutterbackend.eveythingPolicies.policy_benefits.domain.Benefits;
import com.flutterbackend.eveythingPolicies.policy_benefits.domain.PolicyBenefits;
import com.flutterbackend.eveythingPolicies.policy_benefits.repository.BenefitsRepository;
import com.flutterbackend.eveythingPolicies.policy_benefits.repository.PolicyBenefitsRepository;
import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.Inclusions;
import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.PolicyInclusions;
import com.flutterbackend.eveythingPolicies.policy_inclusions.repository.InclusionsRepository;
import com.flutterbackend.eveythingPolicies.policy_inclusions.repository.PolicyInclusionsRepository;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class PoliciesService {

    private final PoliciesRepository policiesRepository;
    private final BrokerRepository brokerRepository;
    private final PolicyCategoriesRepository policyCategoriesRepository;
    private final PolicyDurationRepository policyDurationRepository;
    private final PolicyExclusionRepository policyExclusionRepository;
    private final ExclusionTypeRepository exclusionTypeRepository;
    private final CoverageTierRepository coverageTierRepository;
    private final EmailService emailService;
    private final BenefitsRepository benefitsRepository;
    private final PolicyBenefitsRepository policyBenefitsRepository;
    private final InclusionsRepository inclusionsRepository;
    private final PolicyInclusionsRepository policyInclusionsRepository;
    private final ReviewsRepository reviewsRepository;
    private final com.flutterbackend.countries.repository.CountriesRepository countriesRepository;

    public PoliciesService(PoliciesRepository policiesRepository,
                           BrokerRepository brokerRepository,
                           PolicyCategoriesRepository policyCategoriesRepository,
                           PolicyDurationRepository policyDurationRepository,
                           PolicyExclusionRepository policyExclusionRepository,
                           ExclusionTypeRepository exclusionTypeRepository,
                           CoverageTierRepository coverageTierRepository,
                           EmailService emailService,
                           BenefitsRepository benefitsRepository,
                           PolicyBenefitsRepository policyBenefitsRepository,
                           InclusionsRepository inclusionsRepository,
                           PolicyInclusionsRepository policyInclusionsRepository,
                           ReviewsRepository reviewsRepository,
                           com.flutterbackend.countries.repository.CountriesRepository countriesRepository) {
        this.policiesRepository = policiesRepository;
        this.brokerRepository = brokerRepository;
        this.policyCategoriesRepository = policyCategoriesRepository;
        this.policyDurationRepository = policyDurationRepository;
        this.policyExclusionRepository = policyExclusionRepository;
        this.exclusionTypeRepository = exclusionTypeRepository;
        this.coverageTierRepository = coverageTierRepository;
        this.emailService = emailService;
        this.benefitsRepository = benefitsRepository;
        this.policyBenefitsRepository = policyBenefitsRepository;
        this.inclusionsRepository = inclusionsRepository;
        this.policyInclusionsRepository = policyInclusionsRepository;
        this.reviewsRepository = reviewsRepository;
        this.countriesRepository = countriesRepository;
    }

    public String create(PolicyRequest body, User user) {
        Broker broker = brokerRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));
        PolicyCategories category = policyCategoriesRepository.findById(body.categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        PolicyDuration policyDuration = policyDurationRepository.findById(body.policyDurationId)
                .orElseThrow(() -> new RuntimeException("Policy duration not found"));

        Policies policy = new Policies();
        policy.setBroker(broker);
        policy.setCategory(category);
        policy.setPolicyDuration(policyDuration);
        if (body.countryId != null) {
            policy.setCountry(countriesRepository.findById(body.countryId)
                    .orElseThrow(() -> new RuntimeException("Country not found")));
        }
        policy.setDeliveryPrice(body.deliveryPrice);
        policy.setPolicyName(body.policyName);
        policy.setDescription(body.description);
        policy.setDocumentUrl(cleanOptional(body.documentUrl));
        if (body.waitingPeriodDays != null)   policy.setWaitingPeriodDays(body.waitingPeriodDays);
        if (body.deductiblePerClaim != null)  policy.setDeductiblePerClaim(body.deductiblePerClaim);
        if (body.deductiblePerYear != null)   policy.setDeductiblePerYear(body.deductiblePerYear);
        if (body.maxClaimsPerYear != null)    policy.setMaxClaimsPerYear(body.maxClaimsPerYear);
        if (body.maxClaimAmount != null)      policy.setMaxClaimAmount(body.maxClaimAmount);
        if (body.minAge != null)              policy.setMinAge(body.minAge);
        if (body.maxAge != null)              policy.setMaxAge(body.maxAge);
        if (body.claimProcessingDays != null) policy.setClaimProcessingDays(body.claimProcessingDays);

        policy.setStatus("PENDING_APPROVAL");
        policy.setRejectionReason(null);
        policiesRepository.save(policy);

        saveExclusions(policy, body.exclusionTypeIds);
        saveBenefits(policy, body.benefitIds);
        saveInclusions(policy, body.inclusionIds);
        saveCoverageTiers(policy, body.coverageTiers);
        return "Policy created successfully.";
    }

    public String update(Long id, PolicyRequest body, User user) {
        Broker broker = brokerRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Broker not found"));
        Policies policy = policiesRepository.findById(id)
                .filter(p -> p.getBroker().getBrokerId().equals(broker.getBrokerId()))
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        if (body.policyName != null)  policy.setPolicyName(body.policyName);
        if (body.description != null) policy.setDescription(body.description);
        if (body.documentUrl != null) policy.setDocumentUrl(cleanOptional(body.documentUrl));

        if (body.categoryId != null) {
            PolicyCategories cat = policyCategoriesRepository.findById(body.categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found: " + body.categoryId));
            policy.setCategory(cat);
        }

        if (body.policyDurationId != null)
            policyDurationRepository.findById(body.policyDurationId).ifPresent(policy::setPolicyDuration);

        if (body.countryId != null)
            countriesRepository.findById(body.countryId).ifPresent(policy::setCountry);
        if (body.deliveryPrice != null) policy.setDeliveryPrice(body.deliveryPrice);

        if (body.waitingPeriodDays != null)   policy.setWaitingPeriodDays(body.waitingPeriodDays);
        if (body.deductiblePerClaim != null)  policy.setDeductiblePerClaim(body.deductiblePerClaim);
        if (body.deductiblePerYear != null)   policy.setDeductiblePerYear(body.deductiblePerYear);
        if (body.maxClaimsPerYear != null)    policy.setMaxClaimsPerYear(body.maxClaimsPerYear);
        if (body.maxClaimAmount != null)      policy.setMaxClaimAmount(body.maxClaimAmount);
        if (body.minAge != null)              policy.setMinAge(body.minAge);
        if (body.maxAge != null)              policy.setMaxAge(body.maxAge);
        if (body.claimProcessingDays != null) policy.setClaimProcessingDays(body.claimProcessingDays);

        policy.setStatus("PENDING_APPROVAL");
        policy.setRejectionReason(null);

        policiesRepository.save(policy);

        if (body.coverageTiers != null) {
            List<CoverageTier> existing = coverageTierRepository.findByPolicy_PolicyId(policy.getPolicyId());
            List<Map<String, Object>> incoming = body.coverageTiers;
            for (int i = 0; i < Math.min(existing.size(), incoming.size()); i++) {
                CoverageTier tier = existing.get(i);
                Map<String, Object> t = incoming.get(i);
                tier.setTierName(t.get("tierName").toString());
                tier.setCoverageLimit(new BigDecimal(t.get("coverageLimit").toString()));
                tier.setPremiumPrice(new BigDecimal(t.get("premiumPrice").toString()));
                coverageTierRepository.save(tier);
            }
            for (int i = existing.size(); i < incoming.size(); i++) {
                Map<String, Object> t = incoming.get(i);
                CoverageTier tier = new CoverageTier();
                tier.setPolicy(policy);
                tier.setTierName(t.get("tierName").toString());
                tier.setCoverageLimit(new BigDecimal(t.get("coverageLimit").toString()));
                tier.setPremiumPrice(new BigDecimal(t.get("premiumPrice").toString()));
                coverageTierRepository.save(tier);
            }
        }
        if (body.benefitIds != null) {
            policyBenefitsRepository.deleteByPolicy_PolicyId(policy.getPolicyId());
            saveBenefits(policy, body.benefitIds);
        }
        if (body.inclusionIds != null) {
            policyInclusionsRepository.deleteByPolicy_PolicyId(policy.getPolicyId());
            saveInclusions(policy, body.inclusionIds);
        }
        return "Policy updated successfully.";
    }

    public List<PolicyResponse> getAll() {
        List<Policies> policies = policiesRepository.findAll();
        Map<Long, double[]> ratings = ratingMap();
        return policies.stream().map(p -> toResponse(p, ratings)).toList();
    }

    public List<PolicyResponse> getAllByCountry(Long countryId) {
        Map<Long, double[]> ratings = ratingMap();
        return policiesRepository.findAll().stream()
                .filter(p -> p.getCountry() != null
                        && p.getCountry().getCountryId().equals(countryId))
                .map(p -> toResponse(p, ratings))
                .toList();
    }

    public PolicyResponse getById(Long id) {
        Policies p = policiesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        double avg = reviewsRepository.averageRatingByPolicy(p.getPolicyId());
        long count = reviewsRepository.countByPolicy_PolicyId(p.getPolicyId());
        return PolicyResponse.from(p, avg, count);
    }

    public List<PolicyResponse> getByBroker(User user) {
        Broker broker = brokerRepository.findByUser_UserId(user.getUserId()).orElseThrow(() -> new RuntimeException("Broker not found"));
        List<Policies> policies = policiesRepository.findByBroker_BrokerId(broker.getBrokerId());
        Map<Long, double[]> ratings = ratingMap();
        return policies.stream().map(p -> toResponse(p, ratings)).toList();
    }

    public List<PolicyResponse> getActiveByBrokerId(Long brokerId) {
        Map<Long, double[]> ratings = ratingMap();
        return policiesRepository.findByBroker_BrokerId(brokerId).stream()
                .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
                .map(p -> toResponse(p, ratings))
                .toList();
    }

    public List<PolicyResponse> getPendingApproval() {
        Map<Long, double[]> ratings = ratingMap();
        return policiesRepository.findByStatus("PENDING_APPROVAL").stream()
                .map(p -> toResponse(p, ratings))
                .toList();
    }

    public String approvePolicy(Long id) {
        Policies policy = policiesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        policy.setStatus("ACTIVE");
        policy.setRejectionReason(null);
        policiesRepository.save(policy);
        notifyBroker(policy, true, null);
        return "Policy approved and published.";
    }

    public String rejectPolicy(Long id, String reason) {
        Policies policy = policiesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found"));
        policy.setStatus("REJECTED");
        policy.setRejectionReason(reason != null && !reason.isBlank()
                ? reason : "Did not meet marketplace requirements.");
        policiesRepository.save(policy);
        notifyBroker(policy, false, policy.getRejectionReason());
        return "Policy rejected.";
    }

    private void notifyBroker(Policies policy, boolean approved, String reason) {
        try {
            if (policy.getBroker() == null || policy.getBroker().getUser() == null) return;
            String email = policy.getBroker().getUser().getEmail();
            String name = policy.getBroker().getUser().getFullName();
            if (approved) {
                emailService.sendPolicyApprovalEmail(email, name, policy.getPolicyName());
            } else {
                emailService.sendPolicyRejectionEmail(email, name, policy.getPolicyName(), reason);
            }
        } catch (Exception e) {
            System.out.println("Policy notification email failed: " + e.getMessage());
        }
    }

    private Map<Long, double[]> ratingMap() {
        Map<Long, double[]> map = new HashMap<>();
        for (Object[] row : reviewsRepository.aggregateRatingsForAllPolicies()) {
            Long policyId = ((Number) row[0]).longValue();
            double avg = row[1] == null ? 0.0 : ((Number) row[1]).doubleValue();
            long count = row[2] == null ? 0L : ((Number) row[2]).longValue();
            map.put(policyId, new double[]{avg, count});
        }
        return map;
    }

    private PolicyResponse toResponse(Policies p, Map<Long, double[]> ratings) {
        double[] agg = ratings.getOrDefault(p.getPolicyId(), new double[]{0.0, 0.0});
        return PolicyResponse.from(p, agg[0], (long) agg[1]);
    }

    private String cleanOptional(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void saveBenefits(Policies policy, List<Long> ids) {
        if (ids == null) return;
        for (Long id : ids) {
            Benefits b = benefitsRepository.findById(id).orElseThrow(() -> new RuntimeException("Benefit not found: " + id));
            PolicyBenefits pb = new PolicyBenefits(); pb.setPolicy(policy); pb.setBenefit(b);
            policyBenefitsRepository.save(pb);
        }
    }
    private void saveInclusions(Policies policy, List<Long> ids) {
        if (ids == null) return;
        for (Long id : ids) {
            Inclusions inc = inclusionsRepository.findById(id).orElseThrow(() -> new RuntimeException("Inclusion not found: " + id));
            PolicyInclusions pi = new PolicyInclusions(); pi.setPolicy(policy); pi.setInclusion(inc);
            policyInclusionsRepository.save(pi);
        }
    }
    private void saveExclusions(Policies policy, List<Long> ids) {
        if (ids == null) return;
        for (Long id : ids) {
            ExclusionType t = exclusionTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Exclusion not found"));
            PolicyExclusion pe = new PolicyExclusion(); pe.setPolicy(policy); pe.setExclusionType(t);
            policyExclusionRepository.save(pe);
        }
    }
    private void saveCoverageTiers(Policies policy, List<Map<String, Object>> tiers) {
        if (tiers == null) return;
        for (Map<String, Object> t : tiers) {
            CoverageTier tier = new CoverageTier(); tier.setPolicy(policy);
            tier.setTierName(t.get("tierName").toString());
            tier.setCoverageLimit(new BigDecimal(t.get("coverageLimit").toString()));
            tier.setPremiumPrice(new BigDecimal(t.get("premiumPrice").toString()));
            coverageTierRepository.save(tier);
        }
    }
}
