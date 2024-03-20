package shootingstar.var.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shootingstar.var.Service.EmailService;
import shootingstar.var.Service.UserService;
import shootingstar.var.dto.req.CheckAuthCodeReqDto;
import shootingstar.var.dto.req.SendAuthCodeReqDto;
import shootingstar.var.dto.req.UserSignupReqDto;
import shootingstar.var.exception.ErrorResponse;

@Tag(name = "AllUserController", description = "로그인하지 않아도 접속 가능한 컨트롤러")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/all")
public class AllUserController {
    private final UserService userService;
    private final EmailService emailService;

    @Operation(summary = "회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
    })
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupReqDto reqDto) {

        userService.signup(reqDto);

        return ResponseEntity.ok().body("회원가입 성공");
    }

    @Operation(summary = "닉네임 중복검사 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복 닉네임인 경우 true, 사용가능한 닉네임인 경우 false 를 반환", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 닉네임 : 1003", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/duplicate/{nickname}")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@NotBlank @PathVariable String nickname){
        return ResponseEntity.ok(userService.checkNicknameDuplicate(nickname));
    }

    @Operation(summary = "인증 이메일 발송 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송에 성공하였을 때", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 이메일 : 1001", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/email/sendAuthCode")
    public ResponseEntity<String> sendAuthCode(@Valid @RequestBody SendAuthCodeReqDto reqDto) {
        emailService.sendAuthCodeEmail(reqDto.getEmail());
        return ResponseEntity.ok().body("인증코드를 발송하였습니다.");
    }

    @Operation(summary = "이메일 인증 코드 검증 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드 검증에 성공하였을 때", content = {
                    @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 형식의 이메일 : 1001", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "잘못된 인증 코드 : 1101", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/email/checkAuthCode")
    public ResponseEntity<String> checkAuthCode(@Valid @RequestBody CheckAuthCodeReqDto reqDto) {
        emailService.validateCode(reqDto.getEmail(), reqDto.getCode());
        return ResponseEntity.ok().body("이메일 인증에 성공하였습니다.");
    }
}
