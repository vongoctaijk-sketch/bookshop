package com.example.Bookshop.service;

import com.example.Bookshop.dto.HoaDonRequest;
import com.example.Bookshop.entity.HoaDon;

public interface HoaDonService extends CrudService<HoaDon, Integer> {
    HoaDon create(HoaDonRequest request);
}
