package com.example.Bookshop.service;

import com.example.Bookshop.dto.AdminCreateUserRequest;
import com.example.Bookshop.entity.NguoiDung;

public interface NguoiDungService extends CrudService<NguoiDung, Integer> {
    NguoiDung createByAdmin(AdminCreateUserRequest request);
}
