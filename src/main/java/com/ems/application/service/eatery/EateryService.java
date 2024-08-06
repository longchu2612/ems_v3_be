package com.ems.application.service.eatery;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ems.application.dto.eatery.EateryResponse;
import com.ems.application.dto.eatery.NewEateryRequest;
import com.ems.application.entity.Eatery;
import com.ems.application.mapping.eatery.EateryMapping;
import com.ems.application.repository.EateryRepository;
import com.ems.application.service.BaseService;
import com.ems.application.util.HashIdsUtils;

@Service

public class EateryService extends BaseService {

    private final EateryRepository eateryRepository;
    private final HashIdsUtils hashIdsUtils;

    public EateryService(EateryRepository eateryRepository,
            HashIdsUtils hashIdsUtils) {
        this.eateryRepository = eateryRepository;
        this.hashIdsUtils = hashIdsUtils;
    }

    public ResponseEntity<EateryResponse> getEateryById(String id) {
        Eatery eatery = eateryRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        // check eatery is null
        if (eatery == null) {
            return ResponseEntity.notFound().build();
        }
        // convert eatery to DTO
        return ResponseEntity.ok(EateryMapping.convertToDto(eatery, hashIdsUtils));
    }

    public ResponseEntity<EateryResponse> updateEatery(String id, NewEateryRequest eateryRequest) {
        // lấy danh mục từ cơ sở dữ liệu bằng ID
        Eatery eatery = eateryRepository.findById(hashIdsUtils.decodeId(id)).orElse(null);
        // Kiểm tra xem danh mục có tồn tại không
        if (eatery == null) {
            return ResponseEntity.notFound().build();
        }
        // Chuyển đổi thông tin mới của danh mục thành 1 đôi tượng Eatery
        Eatery updatedEatery = EateryMapping.convertToEntity(eateryRequest, eatery);
        // Lưu danh mục đã được cập nhập vào cơ sở dữ liệu
        eateryRepository.save(updatedEatery);
        // Trả về danh mục đã được cập nhập dang DTO và trạng thái OK 200
        return ResponseEntity.ok(EateryMapping.convertToDto(updatedEatery, hashIdsUtils));
    }

}