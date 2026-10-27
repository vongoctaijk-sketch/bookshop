package com.example.Bookshop.service;

import com.example.Bookshop.entity.PhieuNhap;
import com.example.Bookshop.dto.PhieuNhapRequest;

public interface PhieuNhapService extends CrudService<PhieuNhap, Integer> {
    PhieuNhap createFromRequest(PhieuNhapRequest request);
    PhieuNhap updateFromRequest(Integer id, PhieuNhapRequest request);
    PhieuNhap cancel(Integer id);
}
