package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.Customer;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    
    @Select("SELECT * FROM customers WHERE phone = #{phone} AND deleted = 0")
    Customer findByPhone(@Param("phone") String phone);
    
    @Select("SELECT * FROM customers WHERE id_card = #{idCard} AND deleted = 0")
    Customer findByIdCard(@Param("idCard") String idCard);
    
    @Select({
        "<script>",
        "SELECT * FROM customers WHERE deleted = 0",
        "<if test='name != null and name != \"\"'>",
        "  AND name LIKE CONCAT('%', #{name}, '%')",
        "</if>",
        "<if test='phone != null and phone != \"\"'>",
        "  AND phone LIKE CONCAT('%', #{phone}, '%')",
        "</if>",
        "<if test='idCard != null and idCard != \"\"'>",
        "  AND id_card LIKE CONCAT('%', #{idCard}, '%')",
        "</if>",
        "ORDER BY created_time DESC",
        "</script>"
    })
    IPage<Customer> selectPageWithCondition(Page<Customer> page, 
                                          @Param("name") String name,
                                          @Param("phone") String phone,
                                          @Param("idCard") String idCard);
}



