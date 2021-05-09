package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CommonValidation commonValidation;

    /**
     * RestController method called when the request pattern is of type '/address'
     * and the incoming request is of 'POST' type
     * Persists SaveAddressRequest details in the database
     *
     * @param authorization      - String represents authorization token
     * @param saveAddressRequest - SaveAddressRequest details to be persisted in the database
     * @return - ResponseEntity (SaveAddressResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if the access token is not valid/ customer has already logged out/
     *                                      *                                      the session has already expired
     * @throws SaveAddressException         - if any required field is empty, or pincode is invalid
     * @throws AddressNotFoundException     - if no state exists with the provided state uuid
     */
    @RequestMapping(method = RequestMethod.POST, path = "/address",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(
            @RequestHeader("authorization") final String authorization,
            @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        final StateEntity state = addressService.getStateByUUID(saveAddressRequest.getStateUuid());

        // Set fields into AddressEntity
        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setActive(1);
        addressEntity.setState(state);

        final AddressEntity savedAddressEntity = addressService.saveAddress(addressEntity, customerEntity);

        final SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
                .id(savedAddressEntity.getUuid())
                .status("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(saveAddressResponse, HttpStatus.CREATED);
    }

    /**
     * RestController method called when the request pattern is of type '/address/customer'
     * and the incoming request is of 'GET' type
     * Get all the saved address from the database for a customer
     *
     * @param authorization - String represents authorization token
     * @return - ResponseEntity (AddressListResponse containing customer's address, along with HTTP status code)
     * @throws AuthorizationFailedException - if the access token is not valid/ customer has already logged out/
     *                                      the session has already expired
     */
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllAddress(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        final List<AddressEntity> allAddress = addressService.getAllAddress(customerEntity);
        final List<AddressList> addressLists = new ArrayList<>();
        if (!allAddress.isEmpty()) {
            allAddress.forEach(
                    address -> addressLists.add(createAddressList(address)));
        }

        final AddressListResponse addressListResponse = new AddressListResponse()
                .addresses(addressLists);

        return new ResponseEntity<>(addressListResponse, HttpStatus.OK);

    }

    /**
     * Method to set all AddressEntity field into AddressList
     *
     * @param address - AddressEntity object
     * @return - AddressList
     */
    private AddressList createAddressList(Object address) {
        final AddressList addressList = new AddressList();
        AddressEntity addressEntity = (AddressEntity) address;
        addressList.id(UUID.fromString(addressEntity.getUuid()));
        addressList.flatBuildingName(addressEntity.getFlatBuilNo());
        addressList.locality(addressEntity.getLocality());
        addressList.city(addressEntity.getCity());
        addressList.pincode(addressEntity.getPincode());
        AddressListState addressListState = new AddressListState();
        addressListState.id(UUID.fromString(addressEntity.getState().getUuid()))
                .stateName(addressEntity.getState().getStateName());
        addressList.state(addressListState);
        return addressList;
    }

    /**
     * RestController method called when the request pattern is of type '/address/{address_id}'
     * and the incoming request is of 'DELETE' type
     * Delete the address from the database for a customer
     *
     * @param authorization - String represents authorization token
     * @param addressId     - Address UUID to be deleted from the database
     * @return - ResponseEntity (DeleteAddressResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if the access token is not valid/ customer has already logged out/
     *                                      the session has already expired
     * @throws AddressNotFoundException     - if the address UUID is empty, or incorrect
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("address_id") final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {

        final String accessToken = authorization.split("Bearer ")[1];
        final CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        // Throw exception if the address UUID is empty
        if (commonValidation.isEmptyFieldValue(addressId)) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }

        final AddressEntity addressEntity = addressService.getAddressByUUID(addressId, customerEntity);
        final AddressEntity deletedAddressEntity = addressService.deleteAddress(addressEntity);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddressEntity.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<>(deleteAddressResponse, HttpStatus.OK);

    }

    /**
     * RestController method called when the request pattern is of type '/states'
     * and the incoming request is of 'GET' type
     * Retrieve all states from the database
     * Any user can access this stateList
     *
     * @return - ResponseEntity(StatesListResponse along with HTTP status code)
     */
    @RequestMapping(method = RequestMethod.GET, path = "/states",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() {

        final List<StateEntity> stateEntities = addressService.getAllStates();

        final StatesListResponse statesListResponse = new StatesListResponse();
        if (!stateEntities.isEmpty()) {
            final List<StatesList> statesLists = new ArrayList<>();
            stateEntities.forEach(stateEntity -> {
                final StatesList statesList = new StatesList()
                        .id(UUID.fromString(stateEntity.getUuid()))
                        .stateName(stateEntity.getStateName());
                statesLists.add(statesList);
            });
            statesListResponse.states(statesLists);
        }

        return new ResponseEntity<>(statesListResponse, HttpStatus.OK);
    }
}