package com.flutterbackend.address.service.impl;

import com.flutterbackend.address.domain.Address;
import com.flutterbackend.address.dto.AddressRequest;
import com.flutterbackend.address.dto.AddressResponse;
import com.flutterbackend.address.repository.AddressRepository;
import com.flutterbackend.address.service.AddressService;
import com.flutterbackend.user.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public AddressResponse addAddress(AddressRequest request, User user) {

        if (request.isDefault()) {
            addressRepository.findByUser_UserIdAndIsDefaultTrue(user.getUserId())
                    .ifPresent(existing -> {
                        existing.setDefault(false);
                        addressRepository.save(existing);
                    });
        }

        Address address = new Address();
        address.setUser(user);
        address.setFullName(request.getFullName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefault(request.isDefault());

        Address saved = addressRepository.save(address);
        return toResponse(saved);
    }

    @Override
    public List<AddressResponse> getMyAddresses(User user) {
        return addressRepository.findByUser_UserId(user.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AddressResponse setDefault(Long addressId, User user) {

        addressRepository.findByUser_UserIdAndIsDefaultTrue(user.getUserId())
                .ifPresent(existing -> {
                    existing.setDefault(false);
                    addressRepository.save(existing);
                });

        Address address = addressRepository
                .findByAddressIdAndUser_UserId(addressId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setDefault(true);
        return toResponse(addressRepository.save(address));
    }

    @Override
    public String deleteAddress(Long addressId, User user) {
        Address address = addressRepository
                .findByAddressIdAndUser_UserId(addressId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressRepository.delete(address);
        return "Address deleted successfully.";
    }

    private AddressResponse toResponse(Address address) {
        return AddressResponse.builder()
                .addressId(address.getAddressId())
                .fullName(address.getFullName())
                .phoneNumber(address.getPhoneNumber())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .isDefault(address.isDefault())
                .build();
    }
}
