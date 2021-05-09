package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Method to handle SignUpRestrictedException if the customer already exists in the database
     * with the given contact number during signup, or does not pass the validations
     *
     * @param exp     - SignUpRestrictedException
     * @param request - WebRequest
     * @return - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictionException(
            final SignUpRestrictedException exp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(exp.getCode())
                .message(exp.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Method to handle AuthenticationFailedException if invalid credentials are provided during login,
     * or does not pass the validations
     *
     * @param exp     - AuthenticationFailedException
     * @param request - WebRequest
     * @return - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(
            final AuthenticationFailedException exp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(exp.getCode())
                .message(exp.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Method to handle AuthorizationFailedException if invalid/ expired authorization token is provided during logout,
     *
     * @param exp     - AuthorizationFailedException
     * @param request - WebRequest
     * @return - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(
            final AuthorizationFailedException exp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(exp.getCode())
                .message(exp.getErrorMessage()), HttpStatus.FORBIDDEN);
    }

    /**
     * Method to handle UpdateCustomerException if any mandatory fields in updated customer object is not present
     *
     * @param exp     - UpdateCustomerException
     * @param request - WebRequest
     * @return - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> updateCustomerException(
            final UpdateCustomerException exp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(exp.getCode())
                .message(exp.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Method to handle SaveAddressException if any mandatory fields address object is not present
     *
     * @param exp     - SaveAddressException
     * @param request - WebRequest
     * @return - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> saveAddressException(
            final SaveAddressException exp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(exp.getCode())
                .message(exp.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Method to handle AddressNotFoundException if any mandatory fields address object is not present
     *
     * @param exp     - AddressNotFoundException
     * @param request - WebRequest
     * @return - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> addressNotFoundException(
            final AddressNotFoundException exp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(exp.getCode())
                .message(exp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle CouponNotFoundException if coupon does not exist in the database
     *
     * @param excp      - CouponNotFoundException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> couponNotFoundException(
            final CouponNotFoundException excp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle CategoryNotFoundException if category does not exist in the database
     *
     * @param excp      - CategoryNotFoundException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> categoryNotFoundException(
            final CategoryNotFoundException excp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle RestaurantNotFoundException if restaurant id is empty, or restaurant does not exist in the database
     *
     * @param excp      - RestaurantNotFoundException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> restaurantNotFoundException(
            final RestaurantNotFoundException excp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle PaymentMethodNotFoundException if payment method does not exist in the database
     *
     * @param excp      - PaymentMethodNotFoundException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ErrorResponse> paymentMethodNotFoundException(
            final PaymentMethodNotFoundException excp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle ItemNotFoundException if item does not exist in the database
     *
     * @param excp      - ItemNotFoundException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> itemNotFoundException(
            final ItemNotFoundException excp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Method to handle InvalidRatingException if customer did not enter valid rating
     *
     * @param excp      - InvalidRatingException
     * @param request   - WebRequest
     * @return          - ResponseEntity (ErrorResponse along with Http status code
     */
    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> invalidRatingException(
            final InvalidRatingException excp, final WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse().code(excp.getCode())
                .message(excp.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }
}
