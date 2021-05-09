package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * The CustomerEntity class is mapped to table 'customer' in database
 * All the columns are mapped to its respective attributes of the class
 */

@Entity
@Table(name = "customer")
@NamedQueries(
        {
                @NamedQuery(name = "customerByContactNum",
                        query = "select c from CustomerEntity c where c.contactNumber = :contactNum"),
        }
)
public class CustomerEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "UUID")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "FIRSTNAME")
    @Size(max = 30)
    @NotNull
    private String firstName;

    @Column(name = "LASTNAME")
    @Size(max = 30)
    private String lastName;

    @Column(name = "EMAIL")
    @Size(max = 50)
    private String email;

    @Column(name = "CONTACT_NUMBER")
    @Size(max = 30)
    @NotNull
    private String contactNumber;

    @Column(name = "PASSWORD")
    @Size(max = 255)
    @NotNull
    @ToStringExclude
    private String password;

    @Column(name = "SALT")
    @Size(max = 255)
    @NotNull
    @ToStringExclude
    private String salt;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "customers")
    private List<AddressEntity> address;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<AddressEntity> getAddress() {
        return address;
    }

    public void setAddress(List<AddressEntity> address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CustomerEntity that = (CustomerEntity) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(uuid, that.uuid)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(email, that.email)
                .append(contactNumber, that.contactNumber)
                .append(password, that.password)
                .append(salt, that.salt)
                .append(address, that.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(uuid)
                .append(firstName)
                .append(lastName)
                .append(email)
                .append(contactNumber)
                .append(password)
                .append(salt)
                .append(address)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("uuid", uuid)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("contactNumber", contactNumber)
                .append("password", password)
                .append("salt", salt)
                .append("address", address)
                .toString();
    }

}