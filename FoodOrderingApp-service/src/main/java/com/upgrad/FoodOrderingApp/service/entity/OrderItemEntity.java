package com.upgrad.FoodOrderingApp.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * The OrderItemEntity class is mapped to table 'order_item' in database
 * All the columns are mapped to its respective attributes of the class
 */

@Entity
@Table(name = "order_item")
@NamedQueries(
        {
                @NamedQuery(name = "orderItemsByOrder",
                        query = "select oi from OrderItemEntity oi where oi.order.uuid = :orderUuid")
        }
)
public class OrderItemEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "ITEM_ID")
    private ItemEntity item;

    @Column(name = "QUANTITY")
    @NotNull
    private Integer quanity;

    @Column(name = "PRICE")
    @NotNull
    private Integer price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public Integer getQuanity() {
        return quanity;
    }

    public void setQuanity(Integer quanity) {
        this.quanity = quanity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrderItemEntity that = (OrderItemEntity) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(order, that.order)
                .append(item, that.item)
                .append(quanity, that.quanity)
                .append(price, that.price)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(order)
                .append(item)
                .append(quanity)
                .append(price)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("order", order)
                .append("item", item)
                .append("quanity", quanity)
                .append("price", price)
                .toString();
    }
}
