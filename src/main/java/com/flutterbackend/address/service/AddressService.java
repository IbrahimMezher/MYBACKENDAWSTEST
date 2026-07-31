package com.flutterbackend.address.service;

import com.flutterbackend.address.domain.Address;
import com.flutterbackend.address.dto.AddressRequest;
import com.flutterbackend.address.dto.AddressResponse;
import com.flutterbackend.user.domain.User;

import java.util.List;

public interface AddressService {
    AddressResponse addAddress(AddressRequest request, User user);
    List<AddressResponse> getMyAddresses(User user);
    AddressResponse setDefault(Long addressId, User user);
    String deleteAddress(Long addressId, User user);
}
