package com.flutterbackend.address.web;

import com.flutterbackend.address.dto.AddressRequest;
import com.flutterbackend.address.dto.AddressResponse;
import com.flutterbackend.address.service.AddressService;
import com.flutterbackend.user.domain.User;
import com.flutterbackend.util.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@CrossOrigin(origins = "*")
public class AddressController {

    private final AddressService addressService;
    private final CurrentUser currentUser;

    public AddressController(AddressService addressService,
                             CurrentUser currentUser) {
        this.addressService = addressService;
        this.currentUser = currentUser;
    }

    @PostMapping
    @PreAuthorize("hasRole('customer')")
    public AddressResponse addAddress(@RequestBody AddressRequest request,
                                      HttpServletRequest servletRequest) {
        User user = currentUser.getCurrentUser(servletRequest);
        return addressService.addAddress(request, user);
    }

    @GetMapping
    @PreAuthorize("hasRole('customer')")
    public List<AddressResponse> getMyAddresses(HttpServletRequest servletRequest) {
        User user = currentUser.getCurrentUser(servletRequest);
        return addressService.getMyAddresses(user);
    }

    @PutMapping("/{addressId}/default")
    @PreAuthorize("hasRole('customer')")
    public AddressResponse setDefault(@PathVariable Long addressId,
                                      HttpServletRequest servletRequest) {
        User user = currentUser.getCurrentUser(servletRequest);
        return addressService.setDefault(addressId, user);
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('customer')")
    public String deleteAddress(@PathVariable Long addressId,
                                HttpServletRequest servletRequest) {
        User user = currentUser.getCurrentUser(servletRequest);
        return addressService.deleteAddress(addressId, user);
    }
}
