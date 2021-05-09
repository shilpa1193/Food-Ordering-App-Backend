package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * The CustomerAddressEntity class is mapped to table 'customer_address' in database
 * All the columns are mapped to its respective attributes of the class
 */

@Entity
@Table(name = "customer_address")
@NamedQueries(
        {
                @NamedQuery(name = "customerAddressByCustomer",
                        query = "select ca from CustomerAddressEntity ca where ca.customer.id = :customerId order by ca.address.id desc "),
                @NamedQuery(name = "customerAddressByCustIDAddrID",
                        query = "select ca from CustomerAddressEntity ca where ca.customer.id = :customerId and ca.address.id = :addressId")
        }
)
public class CustomerAddressEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "CUSTOMER_ID")
    private CustomerEntity customer;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CustomerAddressEntity that = (CustomerAddressEntity) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(customer, that.customer)
                .append(address, that.address)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(customer)
                .append(address)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("customer", customer)
                .append("address", address)
                .toString();
    }
}
