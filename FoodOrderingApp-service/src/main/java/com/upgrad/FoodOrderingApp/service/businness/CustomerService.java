package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.common.CommonValidation;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private CommonValidation commonValidation;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$";
    private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$";

    /**
     * Method to save customer information once it has passed all validations
     *
     * @param customerEntity - CustomerEntity to be persisted in the database
     * @return - persisted CustomerEntity
     * @throws SignUpRestrictedException - if the required field information is missing, or does not pass validations,
     *                                   or customer with contact number already exists in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity) throws SignUpRestrictedException {

        // Throw exception if customer record exists in the database with the given contact number
        final CustomerEntity custEntityByPhnNum = customerDao.getCustomerByContactNum(customerEntity.getContactNumber());
        if (custEntityByPhnNum != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

        // Throw exception if email Id pattern does not match the pattern
        final boolean isValidEmail = isValidPattern(EMAIL_PATTERN, customerEntity.getEmail());
        if (!isValidEmail) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        // Throw exception if the contact number is not valid
        if (customerEntity.getContactNumber().length() != 10
                || !StringUtils.isNumeric(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        // Throw exception if password does not match the pattern
        if (!isValidPattern(PASSWORD_PATTERN, customerEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        // Set encrypted passowrd into CustomerEntity object
        setEncryptedPassword(customerEntity, customerEntity.getPassword());
        return customerDao.saveCustomer(customerEntity);

    }

    /**
     * Method to authenticate customer and generate JWT auth token, if the provided credential is valid
     *
     * @param contactNum - String represents the contact of customer
     * @param password   - String represents the password of the customer
     * @return - CustomerAuthEntity
     * @throws AuthenticationFailedException - If the credentials provided is not valid
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contactNum, final String password)
            throws AuthenticationFailedException {

        final CustomerEntity customerEntity = customerDao.getCustomerByContactNum(contactNum);

        // If customer does not exists with the provided contact, throw exception
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        // If the password provided is incorrect, throw exception
        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }

        // Generate JWT auth token
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);

        final CustomerAuthEntity authEntity = new CustomerAuthEntity();
        authEntity.setUuid(UUID.randomUUID().toString());
        authEntity.setCustomer(customerEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        authEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
        authEntity.setLoginAt(now);
        authEntity.setExpiresAt(expiresAt);

        customerDao.createCustomerAuth(authEntity);

        return authEntity;
    }

    /**
     * Method to logout customer if valid access token is provided
     *
     * @param accessToken - String represents access token
     * @return - CustomerAuthEntity, if customer successfully logged out
     * @throws AuthorizationFailedException - if the access token is not valid/ customer has already logged out/
     *                                      the session has already expired
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        final CustomerAuthEntity authEntity = commonValidation.validateCustomerAuthEntity(accessToken);
        final ZonedDateTime now = ZonedDateTime.now();
        authEntity.setLogoutAt(now);
        customerDao.updateCustomerAuth(authEntity);
        return authEntity;
    }

    /**
     * Method to retrieve Customer details if access token provided is valid
     *
     * @param accessToken - String represents access token
     * @return - CustomerEntity, if access token provided is valid
     * @throws AuthorizationFailedException - if the access token is not valid/ customer has already logged out/
     *                                      the session has already expired
     */
    public CustomerEntity getCustomer(final String accessToken) throws AuthorizationFailedException {
        final CustomerAuthEntity authEntity = commonValidation.validateCustomerAuthEntity(accessToken);
        return authEntity.getCustomer();
    }

    /**
     * Method to update CustomerEntity in the database
     *
     * @param customerEntity - CustomerEntity object to be updated
     * @return - updated CustomerEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final CustomerEntity customerEntity) {
        return customerDao.updateCustomer(customerEntity);
    }

    /**
     * Method to update CustomerEntity if correct passwords information are provided
     *
     * @param oldPassword    - String representing old password
     * @param newPassword    - String representing new password
     * @param customerEntity - CustomerEntity currently stored in the database
     * @return - updated CustomerEntity
     * @throws UpdateCustomerException - if the new passowrd does not match the required pattern,
     *                                 or the old password does not match with the password present in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(final String oldPassword, final String newPassword,
                                                 final CustomerEntity customerEntity)
            throws UpdateCustomerException {

        // Throw exception if new password does not match the pattern
        if (!isValidPattern(PASSWORD_PATTERN, newPassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        // If the old password provided is incorrect, throw exception
        final String encryptedPassword = PasswordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword())) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }

        // Set encrypted password into CustomerEntity object
        setEncryptedPassword(customerEntity, newPassword);
        return customerDao.updateCustomer(customerEntity);

    }

    /**
     * Method to check if a field matches the required pattern
     *
     * @param reqPattern - String that represents the pattern to be matched with
     * @param field      - String that represents the value to matched against the pattern
     * @return - true if the patterm matches, else return false
     */
    private boolean isValidPattern(final String reqPattern, final String field) {
        Pattern pattern = Pattern.compile(reqPattern);
        Matcher matcher = pattern.matcher(field);
        return matcher.matches();
    }

    /**
     * Method to set encrypted password into CustomerEntity
     *
     * @param customerEntity - CustomerEntity object
     * @param password       - Password to be encrypted
     */
    private void setEncryptedPassword(final CustomerEntity customerEntity, final String password) {
        String[] encryptedText = cryptographyProvider.encrypt(password);
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
    }
}
