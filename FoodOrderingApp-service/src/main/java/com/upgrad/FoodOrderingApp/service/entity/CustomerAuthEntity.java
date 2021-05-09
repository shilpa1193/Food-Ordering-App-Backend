package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * The CustomerAuthEntity class is mapped to table 'customer_auth' in database
 * All the columns are mapped to its respective attributes of the class
 */

@Entity
@Table(name = "customer_auth")
@NamedQueries(
        {
                @NamedQuery(name = "customerAuthByAccessToken",
                        query = "select ca from CustomerAuthEntity ca where ca.accessToken = :accessToken ")
        }
)
public class CustomerAuthEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    @NotNull
    private CustomerEntity customer;

    @Column(name = "ACCESS_TOKEN")
    @Size(max = 500)
    private String accessToken;

    @Column(name = "LOGIN_AT")
    private ZonedDateTime loginAt;

    @Column(name = "LOGOUT_AT")
    private ZonedDateTime logoutAt;

    @Column(name = "EXPIRES_AT")
    private ZonedDateTime expiresAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CustomerAuthEntity that = (CustomerAuthEntity) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(uuid, that.uuid)
                .append(customer, that.customer)
                .append(accessToken, that.accessToken)
                .append(loginAt, that.loginAt)
                .append(logoutAt, that.logoutAt)
                .append(expiresAt, that.expiresAt)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(uuid)
                .append(customer)
                .append(accessToken)
                .append(loginAt)
                .append(logoutAt)
                .append(expiresAt)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("uuid", uuid)
                .append("customer", customer)
                .append("accessToken", accessToken)
                .append("loginAt", loginAt)
                .append("logoutAt", logoutAt)
                .append("expiresAt", expiresAt)
                .toString();
    }
}
