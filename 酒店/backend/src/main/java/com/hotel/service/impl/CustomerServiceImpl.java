package com.hotel.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.Customer;
import com.hotel.mapper.CustomerMapper;
import com.hotel.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public Customer findByPhone(String phone) {
        return customerMapper.findByPhone(phone);
    }

    @Override
    public Customer findByIdCard(String idCard) {
        return customerMapper.findByIdCard(idCard);
    }

    @Override
    public IPage<Customer> pageWithCondition(Page<Customer> page, String name, String phone, String idCard) {
        return customerMapper.selectPageWithCondition(page, name, phone, idCard);
    }

    @Override
    public boolean createCustomer(Customer customer) {
        // 检查手机号是否已存在
        if (findByPhone(customer.getPhone()) != null) {
            throw new RuntimeException("手机号已存在");
        }

        // 检查身份证号是否已存在
        if (customer.getIdCard() != null && findByIdCard(customer.getIdCard()) != null) {
            throw new RuntimeException("身份证号已存在");
        }

        customer.setRegistrationDate(LocalDateTime.now());
        return save(customer);
    }
}

