package io.fptu.sep490.controller;

import io.fptu.sep490.dto.response.BaseResponse;
import io.fptu.sep490.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {


    @PostMapping
    public ResponseEntity<BaseResponse<?>> test() {
        return ResponseEntity.ok(ResponseUtils.success("ok"));
    }
}
