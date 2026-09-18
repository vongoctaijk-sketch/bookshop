package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.entity.TacGia;
import com.example.Bookshop.entity.TheLoai;
import com.example.Bookshop.entity.NhaXuatBan;
import com.example.Bookshop.repository.NhaXuatBanRepository;
import com.example.Bookshop.repository.SachRepository;
import com.example.Bookshop.repository.TacGiaRepository;
import com.example.Bookshop.repository.TheLoaiRepository;
import com.example.Bookshop.service.CloudinaryService;
import com.example.Bookshop.service.SachService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SachServiceImpl extends AbstractCrudServiceImpl<Sach, Integer> implements SachService {

    private final SachRepository sachRepository;
    private final TheLoaiRepository theLoaiRepository;
    private final NhaXuatBanRepository nhaXuatBanRepository;
    private final TacGiaRepository tacGiaRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    protected JpaRepository<Sach, Integer> getRepository() {
        return sachRepository;
    }

    @Override
    protected void setId(Sach sach, Integer id) {
        sach.setId(id);
    }

    @Override
    public Sach create(Sach sach) {
        return create(sach, null);
    }

    @Override
    public Sach create(Sach sach, MultipartFile fileAnh) {
        if (sach == null) {
            throw new IllegalArgumentException("Dữ liệu sách không được null");
        }

        validateSach(sach);
        resolveSachReferences(sach);

        if (fileAnh != null && !fileAnh.isEmpty()) {
            sach.setHinhAnh(cloudinaryService.uploadFile(fileAnh));
        }

        return sachRepository.save(sach);
    }

    @Override
    public Optional<Sach> update(Integer id, Sach sach) {
        if (sach == null) {
            throw new IllegalArgumentException("Dữ liệu sách không được null");
        }

        return sachRepository.findById(id)
                .map(currentEntity -> {
                    validateSach(sach);
                    resolveSachReferences(sach);

                    if (sach.getHinhAnh() == null || sach.getHinhAnh().isBlank()) {
                        sach.setHinhAnh(currentEntity.getHinhAnh());
                    }

                    setId(sach, id);
                    return sachRepository.save(sach);
                });
    }
  @Override
  public List<Sach> topsachBanChayNhat() {

      Pageable pageable = PageRequest.of(0, 3);

      return sachRepository.topSachBanChayNhat(pageable);
  }

    public Optional<Sach> update(Integer id, Sach sach, MultipartFile fileAnh) {
        if (sach == null) {
            throw new IllegalArgumentException("Dữ liệu sách không được null");
        }

        return sachRepository.findById(id)
                .map(currentEntity -> {
                    validateSach(sach);
                    resolveSachReferences(sach);

                    if (fileAnh != null && !fileAnh.isEmpty()) {
                        sach.setHinhAnh(cloudinaryService.uploadFile(fileAnh));
                    } else if (sach.getHinhAnh() == null || sach.getHinhAnh().isBlank()) {
                        sach.setHinhAnh(currentEntity.getHinhAnh());
                    }

                    setId(sach, id);
                    return sachRepository.save(sach);
                });
    }

    public Sach resolveSachReferences(Sach sach) {
        if (sach == null) {
            return null;
        }

        if (sach.getTheLoai() != null && sach.getTheLoai().getId() != null) {
            TheLoai theLoai = theLoaiRepository.findById(sach.getTheLoai().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Thể loại không tồn tại"));
            sach.setTheLoai(theLoai);
        }

        if (sach.getNhaXuatBan() != null && sach.getNhaXuatBan().getId() != null) {
            NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(sach.getNhaXuatBan().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Nhà xuất bản không tồn tại"));
            sach.setNhaXuatBan(nhaXuatBan);
        }

        if (sach.getTacGias() != null) {
            List<TacGia> resolvedTacGias = new ArrayList<>();
            for (TacGia tacGia : sach.getTacGias()) {
                if (tacGia == null) {
                    continue;
                }
                if (tacGia.getId() != null) {
                    resolvedTacGias.add(tacGiaRepository.findById(tacGia.getId())
                            .orElseThrow(() -> new IllegalArgumentException("Tác giả không tồn tại")));
                } else {
                    resolvedTacGias.add(tacGia);
                }
            }
            sach.setTacGias(resolvedTacGias);
        }

        return sach;
    }

    private void validateSach(Sach sach) {
        if (sach.getTenSach() == null || sach.getTenSach().isBlank()) {
            throw new IllegalArgumentException("Tên sách không được để trống");
        }
        if (sach.getGiaBan() == null || sach.getGiaBan().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0");
        }
        if (sach.getSoLuongTon() == null || sach.getSoLuongTon() < 0) {
            throw new IllegalArgumentException("Số lượng tồn không được âm");
        }
        if (sach.getNamXuatBan() == null || sach.getNamXuatBan() < 1900) {
            throw new IllegalArgumentException("Năm xuất bản phải từ 1900 trở lên");
        }
        if (sach.getDangKinhDoanh() == null) {
            throw new IllegalArgumentException("Trạng thái kinh doanh không được để trống");
        }
        if (sach.getTheLoai() == null || sach.getTheLoai().getId() == null) {
            throw new IllegalArgumentException("Thể loại không được để trống");
        }
        if (sach.getNhaXuatBan() == null || sach.getNhaXuatBan().getId() == null) {
            throw new IllegalArgumentException("Nhà xuất bản không được để trống");
        }
        if (sach.getTacGias() == null || sach.getTacGias().isEmpty()) {
            throw new IllegalArgumentException("Sách phải có ít nhất một tác giả");
        }
    }

    @Override
    public Page<Sach> searchAndFilter(
            String keyword,
            Integer theLoaiId,
            int page,
            int size
    ) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = size > 0 ? size : 10;

        Pageable pageable = PageRequest.of(normalizedPage, normalizedSize);

        Specification<Sach> specification = (root, query, cb) -> null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            specification = specification.and(
                    (root, query, cb) ->
                            cb.like(
                                    cb.lower(root.get("tenSach")),
                                    "%" + keyword.toLowerCase().trim() + "%"
                            )
            );
        }

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
