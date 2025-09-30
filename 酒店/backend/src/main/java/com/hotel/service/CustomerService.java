package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hotel.entity.Customer;

public interface CustomerService extends IService<Customer> {

    /**
     * 根据手机号查找客户
     */
    Customer findByPhone(String phone);

    /**
     * 根据身份证号查找客户
     */
    Customer findByIdCard(String idCard);

    /**
     * 分页查询客户（带条件）
     */
    IPage<Customer> pageWithCondition(Page<Customer> page, String name, String phone, String idCard);

    /**
     * 创建客户
     */
    boolean createCustomer(Customer customer);
}

