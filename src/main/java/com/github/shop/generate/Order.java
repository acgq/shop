package com.github.shop.generate;

import java.time.LocalDate;

public class Order {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.user_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.shop_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private Long shopId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.total_price
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private Long totalPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.address
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private String address;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.express_company
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private String expressCompany;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.express_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private String expressId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.status
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.create_time
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private LocalDate createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column shop_order.update_time
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    private LocalDate updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.id
     *
     * @return the value of shop_order.id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.id
     *
     * @param id the value for shop_order.id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.user_id
     *
     * @return the value of shop_order.user_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.user_id
     *
     * @param userId the value for shop_order.user_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.shop_id
     *
     * @return the value of shop_order.shop_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.shop_id
     *
     * @param shopId the value for shop_order.shop_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.total_price
     *
     * @return the value of shop_order.total_price
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public Long getTotalPrice() {
        return totalPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.total_price
     *
     * @param totalPrice the value for shop_order.total_price
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.address
     *
     * @return the value of shop_order.address
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public String getAddress() {
        return address;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.address
     *
     * @param address the value for shop_order.address
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.express_company
     *
     * @return the value of shop_order.express_company
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public String getExpressCompany() {
        return expressCompany;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.express_company
     *
     * @param expressCompany the value for shop_order.express_company
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany == null ? null : expressCompany.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.express_id
     *
     * @return the value of shop_order.express_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public String getExpressId() {
        return expressId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.express_id
     *
     * @param expressId the value for shop_order.express_id
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setExpressId(String expressId) {
        this.expressId = expressId == null ? null : expressId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.status
     *
     * @return the value of shop_order.status
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.status
     *
     * @param status the value for shop_order.status
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.create_time
     *
     * @return the value of shop_order.create_time
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public LocalDate getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.create_time
     *
     * @param createTime the value for shop_order.create_time
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setCreateTime(LocalDate createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column shop_order.update_time
     *
     * @return the value of shop_order.update_time
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public LocalDate getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column shop_order.update_time
     *
     * @param updateTime the value for shop_order.update_time
     *
     * @mbg.generated Sun Jun 14 00:26:35 CST 2020
     */
    public void setUpdateTime(LocalDate updateTime) {
        this.updateTime = updateTime;
    }
}