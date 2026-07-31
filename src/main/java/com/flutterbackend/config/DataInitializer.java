package com.flutterbackend.config;

import com.flutterbackend.admin.repository.AdminRepository;
import com.flutterbackend.countries.domain.Countries;
import com.flutterbackend.countries.repository.CountriesRepository;
import com.flutterbackend.eveythingPolicies.policy_benefits.domain.Benefits;
import com.flutterbackend.eveythingPolicies.policy_benefits.repository.BenefitsRepository;
import com.flutterbackend.eveythingPolicies.policy_exclusions.domain.ExclusionType;
import com.flutterbackend.eveythingPolicies.policy_exclusions.repository.ExclusionTypeRepository;
import com.flutterbackend.eveythingPolicies.policy_inclusions.domain.Inclusions;
import com.flutterbackend.eveythingPolicies.policy_inclusions.repository.InclusionsRepository;
import com.flutterbackend.eveythingPolicies.policycategories.domain.CategoryField;
import com.flutterbackend.eveythingPolicies.policycategories.domain.PolicyCategories;
import com.flutterbackend.eveythingPolicies.policycategories.repository.CategoryFieldRepository;
import com.flutterbackend.eveythingPolicies.policycategories.repository.PolicyCategoriesRepository;
import com.flutterbackend.eveythingPolicies.policy_duration.domain.PolicyDuration;
import com.flutterbackend.eveythingPolicies.policy_duration.repository.PolicyDurationRepository;
import com.flutterbackend.role.domain.Role;
import com.flutterbackend.role.repository.RoleRepository;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.user.domain.UserStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Value("${app.seed-superadmin.enabled:false}")
    private boolean seedSuperadminEnabled;

    @Value("${app.seed-superadmin.email:}")
    private String seedSuperadminEmail;

    @Value("${app.seed-superadmin.password:}")
    private String seedSuperadminPassword;

    @Bean
    public CommandLineRunner initData(
            RoleRepository roleRepository,
            AdminRepository adminRepository,
            CountriesRepository countriesRepository,
            PolicyCategoriesRepository policyCategoriesRepository,
            PolicyDurationRepository policyDurationRepository,
            BenefitsRepository benefitsRepository,
            InclusionsRepository inclusionsRepository,
            ExclusionTypeRepository exclusionTypeRepository,
            CategoryFieldRepository categoryFieldRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            seedRole(roleRepository, "customer");
            seedRole(roleRepository, "broker");
            seedRole(roleRepository, "admin");
            seedRole(roleRepository, "superadmin");

            if (countriesRepository.count() == 0) {
                Object[][] countries = {
                        {"Lebanon",       "USD", "+961", 11.0},
                        {"Saudi Arabia",  "SAR", "SA", 15.0},
                        {"UAE",           "AED", "AE",  5.0},
                        {"Jordan",        "JOD", "JO", 16.0},
                        {"Egypt",         "EGP", "EG", 14.0},
                        {"Kuwait",        "KWD", "KW",  0.0},
                        {"Qatar",         "QAR", "QA",  0.0},
                        {"Bahrain",       "BHD", "BH", 10.0},
                        {"Oman",          "OMR", "OM",  5.0},
                        {"Iraq",          "IQD", "IQ", 15.0},
                        {"France",        "EUR", "FR", 20.0},
                        {"Germany",       "EUR", "DE", 19.0},
                        {"United States", "USD", "US",  8.5},
                        {"United Kingdom","GBP", "GB", 20.0},
                        {"Turkey",        "TRY", "TR", 18.0},
                };
                for (Object[] c : countries) {
                    Countries country = new Countries();
                    country.setCountryName((String) c[0]);
                    country.setCurrency((String) c[1]);
                    country.setCode((String) c[2]);
                    country.setTaxPercentage((Double) c[3]);
                    countriesRepository.save(country);
                }
            }

            if (policyCategoriesRepository.count() == 0) {
                for (String name : new String[]{"Car", "Health", "Life", "Home", "Travel", "Business"}) {
                    PolicyCategories cat = new PolicyCategories();
                    cat.setCategoryName(name);
                    policyCategoriesRepository.save(cat);
                }
            }

            if (categoryFieldRepository.count() == 0) {

                Object[][][] categoryFieldDefs = {

                        {
                                {"carNumber",      "Car Number / Plate",    "text",   true,  1},
                                {"motorNumber",    "Motor Number",           "text",   true,  2},
                                {"chassisNumber",  "Chassis Number",         "text",   true,  3},
                                {"carMake",        "Car Make (Brand)",       "text",   true,  4},
                                {"carModel",       "Car Model",              "text",   true,  5},
                                {"carYear",        "Manufacturing Year",     "number", true,  6},
                                {"carColor",       "Car Color",              "text",   false, 7},
                                {"engineCapacity", "Engine Capacity (cc)",  "number", false, 8},
                        },

                        {
                                {"bloodType",           "Blood Type",               "text",   true,  1},
                                {"dateOfBirth",         "Date of Birth",            "date",   true,  2},
                                {"gender",              "Gender",                   "text",   true,  3},
                                {"existingConditions",  "Pre-existing Conditions",  "text",   false, 4},
                                {"smokingStatus",       "Smoking Status",           "text",   false, 5},
                                {"height",              "Height (cm)",              "number", false, 6},
                                {"weight",              "Weight (kg)",              "number", false, 7},
                        },

                        {
                                {"beneficiaryName",     "Beneficiary Full Name",    "text",   true,  1},
                                {"beneficiaryRelation", "Beneficiary Relationship", "text",   true,  2},
                                {"beneficiaryPhone",    "Beneficiary Phone",        "text",   false, 3},
                                {"dateOfBirth",         "Date of Birth",            "date",   true,  4},
                                {"smokingStatus",       "Smoking Status",           "text",   false, 5},
                                {"occupation",          "Occupation",               "text",   false, 6},
                        },

                        {
                                {"propertyAddress",  "Property Address",              "text",   true,  1},
                                {"propertyType",     "Property Type (Apartment/House)","text",  true,  2},
                                {"propertyArea",     "Property Area (m²)",            "number", true,  3},
                                {"constructionYear", "Construction Year",             "number", true,  4},
                                {"numberOfRooms",    "Number of Rooms",               "number", false, 5},
                                {"floorNumber",      "Floor Number",                  "number", false, 6},
                                {"buildingAge",      "Building Age (years)",          "number", false, 7},
                        },

                        {
                                {"destination",      "Destination Country",    "text",   true,  1},
                                {"departureDate",    "Departure Date",         "date",   true,  2},
                                {"returnDate",       "Return Date",            "date",   true,  3},
                                {"passportNumber",   "Passport Number",        "text",   true,  4},
                                {"passportExpiry",   "Passport Expiry Date",  "date",   true,  5},
                                {"travelPurpose",    "Travel Purpose",         "text",   false, 6},
                                {"numberOfTravelers","Number of Travelers",   "number", false, 7},
                        },

                        {
                                {"businessName",       "Business Name",              "text",   true,  1},
                                {"businessType",       "Business Type",              "text",   true,  2},
                                {"businessLicense",    "Business License Number",    "text",   true,  3},
                                {"numberOfEmployees",  "Number of Employees",        "number", true,  4},
                                {"annualRevenue",      "Annual Revenue (USD)",       "number", false, 5},
                                {"businessAddress",    "Business Address",           "text",   false, 6},
                                {"yearsInOperation",   "Years in Operation",         "number", false, 7},
                        },
                };

                String[] categoryNames = {"Car", "Health", "Life", "Home", "Travel", "Business"};

                List<PolicyCategories> allCats = policyCategoriesRepository.findAll();

                for (int ci = 0; ci < categoryNames.length; ci++) {
                    final String catName = categoryNames[ci];
                    PolicyCategories category = allCats.stream()
                            .filter(c -> c.getCategoryName().equalsIgnoreCase(catName))
                            .findFirst()
                            .orElse(null);

                    if (category == null) continue;

                    Object[][] fields = categoryFieldDefs[ci];
                    for (Object[] f : fields) {
                        CategoryField cf = new CategoryField();
                        cf.setCategoryId(category.getCategoryId());
                        cf.setFieldName((String) f[0]);
                        cf.setFieldLabel((String) f[1]);
                        cf.setFieldType((String) f[2]);
                        cf.setRequired((Boolean) f[3]);
                        cf.setDisplayOrder((Integer) f[4]);
                        categoryFieldRepository.save(cf);
                    }
                }
            }

            if (policyDurationRepository.count() == 0) {
                String[][] durations = {
                        {"MONTHLY",     "1 Month"},
                        {"QUARTERLY",   "3 Months"},
                        {"SEMI_ANNUAL", "6 Months"},
                        {"ANNUAL",      "1 Year"},
                        {"TWO_YEARS",   "2 Years"},
                        {"THREE_YEARS", "3 Years"},
                };
                for (String[] d : durations) {
                    PolicyDuration pd = new PolicyDuration();
                    pd.setDuration(d[0]);
                    pd.setLabel(d[1]);
                    policyDurationRepository.save(pd);
                }
            }

            if (benefitsRepository.count() == 0) {
                Object[][] benefits = {
                        {"24/7 Customer Support",
                                "Round-the-clock assistance through phone, chat, and email for all your insurance needs."},
                        {"Fast Claims Processing",
                                "Claims are reviewed and processed within 5 business days with direct bank transfer."},
                        {"No Deductible on First Claim",
                                "Your first claim each policy year is fully covered with zero out-of-pocket deductible."},
                        {"Worldwide Coverage",
                                "Full coverage applies globally, ensuring you are protected no matter where you are."},
                        {"Multi-Policy Discount",
                                "Save up to 20% when you bundle two or more insurance policies under the same account."},
                        {"Roadside Assistance",
                                "Free towing, battery jump-start, flat tyre change, and fuel delivery, available 24 hours."},
                        {"Cashless Hospital Network",
                                "Access over 500 partner hospitals and clinics with direct billing, no upfront payment needed."},
                        {"Personal Accident Cover",
                                "Compensation for permanent disability or accidental death included at no extra cost."},
                        {"Natural Disaster Protection",
                                "Covers damage caused by floods, earthquakes, storms, and other natural catastrophes."},
                        {"Legal Assistance",
                                "Access to a network of legal advisors for disputes related to your insured property or vehicle."},
                        {"Loyalty Rewards",
                                "Earn reward points on every premium payment, redeemable for discounts on future renewals."},
                        {"Free Annual Check-up",
                                "One complimentary medical check-up per year at any of our partner clinics, included in health plans."},
                };
                for (Object[] b : benefits) {
                    Benefits benefit = new Benefits();
                    benefit.setTitle((String) b[0]);
                    benefit.setDescription((String) b[1]);
                    benefitsRepository.save(benefit);
                }
            }

            if (inclusionsRepository.count() == 0) {
                Object[][] inclusions = {
                        {"Third-Party Liability",
                                "Covers damages caused to other people, vehicles, or property due to your actions."},
                        {"Fire and Theft",
                                "Protection against loss or damage to the insured item resulting from fire or theft."},
                        {"Accidental Damage",
                                "Covers repair costs for accidental physical damage to the insured property or vehicle."},
                        {"Medical Expenses",
                                "Reimburses hospital, surgery, and treatment costs arising from covered events."},
                        {"Emergency Evacuation",
                                "Covers costs for emergency medical evacuation to the nearest suitable medical facility."},
                        {"Lost Luggage",
                                "Compensation for lost, stolen, or damaged baggage during covered travel periods."},
                        {"Trip Cancellation",
                                "Refunds non-refundable travel expenses if your trip is cancelled due to covered reasons."},
                        {"Home Contents",
                                "Covers the replacement cost of furniture, electronics, and personal belongings in the home."},
                        {"Structural Damage",
                                "Covers the cost of repairing or rebuilding the physical structure of the insured property."},
                        {"Business Interruption",
                                "Compensates for lost income and operating expenses when business operations are disrupted."},
                        {"Cyber Liability",
                                "Covers costs related to data breaches, cyberattacks, and digital asset loss for businesses."},
                        {"Public Liability",
                                "Protects businesses against claims from members of the public for injury or property damage."},
                };
                for (Object[] inc : inclusions) {
                    Inclusions inclusion = new Inclusions();
                    inclusion.setName((String) inc[0]);
                    inclusion.setDescription((String) inc[1]);
                    inclusionsRepository.save(inclusion);
                }
            }

            if (exclusionTypeRepository.count() == 0) {
                Object[][] exclusions = {
                        {"Pre-existing Conditions",
                                "Medical conditions diagnosed or treated before the policy start date are not covered."},
                        {"Intentional Damage",
                                "Any damage caused deliberately by the policyholder or a named insured is excluded."},
                        {"Drunk or Impaired Driving",
                                "Accidents occurring while the driver is under the influence of alcohol or drugs are excluded."},
                        {"Unlicensed Driver",
                                "Damages are not covered if the vehicle was driven by someone without a valid driving licence."},
                        {"War and Terrorism",
                                "Losses resulting from acts of war, invasion, civil unrest, or terrorism are excluded."},
                        {"Nuclear Hazard",
                                "Any damage caused by nuclear reaction, radiation, or radioactive contamination is excluded."},
                        {"Wear and Tear",
                                "Gradual deterioration, rust, corrosion, and mechanical breakdown due to age are not covered."},
                        {"Illegal Activities",
                                "Claims arising while the insured was engaged in any criminal or illegal activity are void."},
                        {"Uncovered Modifications",
                                "Modifications to a vehicle or property that were not declared to the insurer are excluded."},
                        {"Cosmetic Damage",
                                "Minor scratches, dents, or surface blemishes that do not affect functionality are excluded."},
                        {"Racing and Off-road Use",
                                "Damage sustained during racing, rallying, or off-road use is not covered under standard policies."},
                        {"Unapproved Repairs",
                                "Repairs carried out without prior authorisation from the insurer may not be reimbursed."},
                };
                for (Object[] exc : exclusions) {
                    ExclusionType exclusion = new ExclusionType();
                    exclusion.setName((String) exc[0]);
                    exclusion.setDescription((String) exc[1]);
                    exclusionTypeRepository.save(exclusion);
                }
            }

            if (seedSuperadminEnabled
                    && seedSuperadminEmail != null
                    && !seedSuperadminEmail.isBlank()
                    && seedSuperadminPassword != null
                    && !seedSuperadminPassword.isBlank()
                    && !adminRepository.existsByEmailAndRole_Name(seedSuperadminEmail.trim().toLowerCase(), "superadmin")) {
                Role superadminRole = roleRepository.findByNameIgnoreCase("superadmin")
                        .orElseThrow(() -> new RuntimeException("superadmin role missing"));
                Countries firstCountry = countriesRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No countries seeded"));

                User superadmin = new User();
                superadmin.setFullName("Super Admin");
                superadmin.setUsernameField("superadmin");
                superadmin.setEmail(seedSuperadminEmail.trim().toLowerCase());
                superadmin.setPasswordHash(passwordEncoder.encode(seedSuperadminPassword));
                superadmin.setCountry(firstCountry);
                superadmin.setRole(superadminRole);
                superadmin.setEmailVerified(true);
                superadmin.setTwoFaEnabled(true);
                superadmin.setTwoFaMethod("email");
                superadmin.setStatus(UserStatus.ACTIVE);
                adminRepository.save(superadmin);
            }
        };
    }

    private void seedRole(RoleRepository repo, String name) {
        if (!repo.existsByNameIgnoreCase(name)) {
            repo.save(new Role(name));
        }
    }
}
