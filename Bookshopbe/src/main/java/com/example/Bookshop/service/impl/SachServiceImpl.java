package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.SachRepository;
import com.example.Bookshop.service.SachService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SachServiceImpl extends AbstractCrudServiceImpl<Sach, Integer> implements SachService {

    private final SachRepository sachRepository;

    @Override
    protected JpaRepository<Sach, Integer> getRepository() {
        return sachRepository;
    }

    @Override
    protected void setId(Sach sach, Integer id) {
        sach.setId(null);
        sach.setId(id);
    }
    @Override
    public Page<Sach> searchAndFilter(
            String keyword,
            Integer theLoaiId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<Sach> specification = (root, query, cb) -> null;

        // Tìm kiếm theo tên sách
        if (keyword != null && !keyword.trim().isEmpty()) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.like(
                                    cb.lower(root.get("tenSach")),
                                    "%" + keyword.toLowerCase().trim() + "%"
                            )
            );
        }

        // Lọc theo thể loại
        if (theLoaiId != null) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.equal(
                                    root.get("theLoai").get("id"),
                                    theLoaiId
                            )
            );
        }

        return sachRepository.findAll(specification, pageable);
    }


}
