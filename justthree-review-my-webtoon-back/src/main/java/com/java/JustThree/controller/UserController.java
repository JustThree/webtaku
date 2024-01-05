package com.java.JustThree.controller;

import com.java.JustThree.dto.*;
import com.java.JustThree.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
@Slf4j
public class UserController {

    private final UsersService usersService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            usersService.usercheck(loginRequest);
            return ResponseEntity.ok("로그인 성공");
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }

    }


    @PostMapping(value = "/join")
    public Long join(@RequestBody JoinRequest joinDTO){

        return usersService.insertUsers(joinDTO);
    }

    @PostMapping(value = "/logout")
    public void logout(@RequestBody LoginRequest loginRequest) {
        log.info(loginRequest.getUsersEmail());

        usersService.logout(loginRequest.getUsersEmail());
    }

    @GetMapping(value = "/auth/accessoken")
    public String recreateAccessToken(@RequestHeader("Authorization") String token){

        return usersService.getNewAccessToken(token);
    }

    @PostMapping("/email-verification")
    public ResponseEntity<Object> joinEmailVerification(
            @RequestParam("email") String email,
            @RequestParam("type") String type) {
        try {

            // 이메일 중복 검사 (존재하면 true)
            boolean isEmail = usersService.validateDuplicateEmail(email);

            // 이메일 전송 (인증번호 or 토큰)
            if (type.equals("join")) {
                if (isEmail) {
                    return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
                }
                usersService.sendJoinEmail(email);
            } else if (type.equals("reset-password")) {
                if (!isEmail) {
                    return ResponseEntity.badRequest().body("존재하지 않는 이메일입니다.");
                }
                usersService.sendResetPasswordEmail(email);
            } else {
                return ResponseEntity.badRequest().body("잘못된 요청입니다.");
            }
            return ResponseEntity.ok("성공적으로 이메일이 전송되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이메일 전송에 실패하였습니다.");
        }
    }

    @RequestMapping("/verify-code")
    public ResponseEntity<Object> verifyJoinCode(
            @RequestParam("email") String email,
            @RequestParam("code") String code) {
        try {
            String getCodeEmail = usersService.validateEmailVerification(code);
            if (!getCodeEmail.equals(email)) {
                return ResponseEntity.badRequest().body("인증 코드가 일치하지 않습니다.");
            }
            return ResponseEntity.ok("인증번호가 일치합니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("문제가 발생하였습니다.");
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(
            @RequestParam("email") String email,
            @RequestHeader(value = "secretCode", required = true) String secretCode,
            ResetPWRequest resetPWDTO) {

        if (!secretCode.equals("webtakcu-justThree-1")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("허용되지 않은 접근입니다.");
        }
        // '비밀번호', '비밀번호 확인' 두 값이 같은 지 확인
        if (!usersService.validatePassword(resetPWDTO)) {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
        // 비밀번호 변경
        try {
            usersService.setPassword(email, resetPWDTO.getPassword());
            return ResponseEntity.ok("비밀번호가 변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/check-nickname")
    public ResponseEntity<Object> checkDuplicateNickname(@RequestParam("nickname") String nickname) {
        boolean isNickname = usersService.validateDuplicateNickname(nickname);
        return isNickname ? ResponseEntity.badRequest().body("이미 존재하는 닉네임입니다.") : ResponseEntity.ok("사용 가능한 닉네임입니다.");
    }

    @GetMapping("/getUserId")
    public ResponseEntity<Object> getUsersId(@RequestHeader("Authorization") String token){
        long usersId;
        try{
            usersId = usersService.getUserInfo(token).getUsersId();
        }catch (Exception e){
            return ResponseEntity.badRequest().body("유저가 없습니다");
        }
        return ResponseEntity.ok(usersId);

    }

    @GetMapping("/getUserList")
    public ResponseEntity<?> getUserList(@PageableDefault() Pageable pageable,@RequestParam(required = false) String search,@RequestParam(required = false,defaultValue = "All") String type){
        try{
            return ResponseEntity.ok().body( usersService.getUserList(pageable,search,type));

        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/deleteUser")
    public ResponseEntity<String> deleteUserId(@RequestBody String UsersId){
        log.info("유저 삭제");
        log.info(UsersId);
        try {
            usersService.deleteUser(Long.parseLong(UsersId));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("삭제완료");
    }




}
