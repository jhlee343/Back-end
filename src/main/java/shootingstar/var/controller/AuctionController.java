package shootingstar.var.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shootingstar.var.Service.AuctionService;
import shootingstar.var.dto.req.AuctionCreateReqDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vip/auction")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@Valid @RequestBody AuctionCreateReqDto reqDto, HttpServletRequest request) {
        auctionService.create(reqDto, request);
        return ResponseEntity.ok().body("경매 생성 성공");
    }
}
