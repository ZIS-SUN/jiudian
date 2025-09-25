package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.Customer;
import com.hotel.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Result<IPage<Customer>> getCustomers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String idCard) {
        try {
            Page<Customer> page = new Page<>(current, size);
            IPage<Customer> result = customerService.pageWithCondition(page, name, phone, idCard);
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("获取客户列表失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Customer> getCustomerById(@PathVariable Long id) {
        try {
            Customer customer = customerService.getById(id);
            return Result.success(customer);
        } catch (Exception e) {
            System.err.println("获取客户详情失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    public Result<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            customerService.createCustomer(customer);
            return Result.success("创建客户成功", customer);
        } catch (Exception e) {
            System.err.println("创建客户失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            customer.setId(id);
            customerService.updateById(customer);
            return Result.success("更新客户成功", customer);
        } catch (Exception e) {
            System.err.println("更新客户失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.removeById(id);
            return Result.success("删除客户成功");
        } catch (Exception e) {
            System.err.println("删除客户失败: " + e.getMessage());
            return Result.error(e.getMessage());
        }
    }
}



