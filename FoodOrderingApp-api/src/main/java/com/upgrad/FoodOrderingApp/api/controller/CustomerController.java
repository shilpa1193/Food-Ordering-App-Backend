package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CommonValidation commonValidation;

    /**
     * RestController method called when the request pattern is of type '/customer/signup'
     * and the incoming request is of 'POST' type
     * Persists SignupCustomerRequest details in the database
     *
     * @param signupCustomerRequest - signup customer details
     * @return - ResponseEntity (SignupCustomerResponse along with HTTP status code)
     * @throws SignUpRestrictedException - if the required field information is missing, or does not pass validations,
     *                                   or customer with contact number already exists in the database
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(
            @RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest)
            throws SignUpRestrictedException {

        // Throw exception if any of the mandatory field value does not exist
        if (commonValidation.isEmptyFieldValue(signupCustomerRequest.getFirstName())
                || commonValidation.isEmptyFieldValue(signupCustomerRequest.getEmailAddress())
                || commonValidation.isEmptyFieldValue(signupCustomerRequest.getContactNumber())
                || commonValidation.isEmptyFieldValue(signupCustomerRequest.getPassword())) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        // Set CustomerEntity fields using SignupCustomerRequest object
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        final CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);

        final SignupCustomerResponse customerResponse = new SignupCustomerResponse()
                .id(createdCustomerEntity.getUuid())
                .status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
    }

    /**
     * RestController method called when the request pattern is of type '/customer/login'
     * and the incoming request is of 'POST' type
     * Login customer if valid credentials are provided and generate JWT auth token
     *
     * @param authorization - String representing the contact number and password of the customer
     * @return - ResponseEntity (LoginResponse along with HTTP status code)
     * @throws AuthenticationFailedException - if the contact number/ password provided is incorrect
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {

        if (!authorization.startsWith("Basic ")) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        if (decodedArray.length != 2) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }

        final CustomerAuthEntity authEntity = customerService.authenticate(decodedArray[0], decodedArray[1]);
        final CustomerEntity customer = authEntity.getCustomer();

        final LoginResponse loginResponse = new LoginResponse()
                .id(customer.getUuid())
                .message("LOGGED IN SUCCESSFULLY")
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .emailAddress(customer.getEmail())
                .contactNumber(customer.getContactNumber());
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", authEntity.getAccessToken());
        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);

        return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type '/customer/logout'
     * and the incoming request is of 'POST' type
     * Sign out customer if valid authorization token is provided
     *
     * @param authorization - String represents authorization token
     * @return - ResponseEntity (LogoutResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if the access token is not valid/ customer has already logged out/
     *                                      the session has already expired
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerAuthEntity authEntity = customerService.logout(accessToken);
        final CustomerEntity customerEntity = authEntity.getCustomer();

        final LogoutResponse logoutResponse = new LogoutResponse()
                .id(customerEntity.getUuid())
                .message("LOGGED OUT SUCCESSFULLY");

        return new ResponseEntity<>(logoutResponse, HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type '/customer'
     * and the incoming request is of 'PUT' type
     * Update customer details if valid authorization token and all mandatory fields are provided
     *
     * @param authorization         - String represents authorization token
     * @param updateCustomerRequest - UpdateCustomerRequest containing updated customer info
     * @return - ResponseEntity (UpdateCustomerResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if the access token is not valid/ customer has already logged out/
     *                                      the session has already expired
     * @throws UpdateCustomerException      - if first name is not provided
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/customer",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomerDetails(
            @RequestHeader("authorization") final String authorization,
            @RequestBody(required = false) final UpdateCustomerRequest updateCustomerRequest)
            throws AuthorizationFailedException, UpdateCustomerException {

        // Throw exception if first name of customer is not provided
        if (commonValidation.isEmptyFieldValue(updateCustomerRequest.getFirstName())) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());
        final CustomerEntity updatedCustomerEntity = customerService.updateCustomer(customerEntity);

        final UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse()
                .id(updatedCustomerEntity.getUuid())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY")
                .firstName(updatedCustomerEntity.getFirstName())
                .lastName(updatedCustomerEntity.getLastName());

        return new ResponseEntity<>(updateCustomerResponse, HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type '/customer/password'
     * and the incoming request is of 'PUT' type
     * Update customer password if valid authorization token and all mandatory fields are provided
     *
     * @param authorization         - String represents authorization token
     * @param updatePasswordRequest - UpdatePasswordRequest containing old and new password info
     * @return - ResponseEntity (UpdatePasswordResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if the access token is not valid/ customer has already logged out/
     *                                      the session has already expired
     * @throws UpdateCustomerException      - if old and new password info is empty, or the new password is not of valid format,
     *                                      or old password is invalid
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updateCustomerPassword(
            @RequestHeader("authorization") final String authorization,
            @RequestBody(required = false) final UpdatePasswordRequest updatePasswordRequest)
            throws AuthorizationFailedException, UpdateCustomerException {

        final String oldPassword = updatePasswordRequest.getOldPassword();
        final String newPassword = updatePasswordRequest.getNewPassword();

        // Throw exception if the old or new password is not provided
        if (commonValidation.isEmptyFieldValue(oldPassword)
                || commonValidation.isEmptyFieldValue(newPassword)) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }

        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        final CustomerEntity updatedCustomerEntity = customerService.updateCustomerPassword(
                oldPassword, newPassword, customerEntity);

        final UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse()
                .id(updatedCustomerEntity.getUuid())
                .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

        return new ResponseEntity<>(updatePasswordResponse, HttpStatus.OK);
    }

}
