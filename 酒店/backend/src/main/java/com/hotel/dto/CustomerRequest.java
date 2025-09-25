package com.hotel.dto;

import com.hotel.enums.Gender;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequest {

    @NotBlank(message = "姓名不能为空")
    private String name;

    private Gender gender;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3456789]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^\\d{15}$|^\\d{18}$|^\\d{17}[Xx]$", message = "身份证号格式不正确")
    private String idCard;

    private String address;

    private LocalDate birthDate;

    private String nationality = "中国";
}

