package com.java.JustThree.controller;

import com.java.JustThree.dto.chat.ChatInfoResponse;
import com.java.JustThree.dto.chat.ChatListResponse;
import com.java.JustThree.dto.chat.ChatResponse;
import com.java.JustThree.service.ChatService;
import com.java.JustThree.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/list/{master_id}")
    public ResponseEntity<List<ChatResponse>> recentChatList(@PathVariable Long master_id, @RequestHeader(name = "Authorization") String token){
        return ResponseEntity.ok(chatService.findChatInWebtoon(master_id));
    }

    @GetMapping("/info/{master_id}")
    public ResponseEntity<ChatInfoResponse> chatInfo(@PathVariable Long master_id){
        return ResponseEntity.ok(chatService.findChatInfo(master_id));
    }

    //권한 여부 확인 필요
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ChatListResponse>> chatRoom(@PathVariable Integer type, @RequestHeader(name = "Authorization", required = false) String token){
        // 1: 전체, 2: 실시간, 3: 인기웹툰순, 4: 해당 사용자가 메시지 보낸 채팅방
        List<ChatListResponse> list = chatService.findChatRoom(type, token);
        System.out.println(list);
        return token == null && type == 4 ? ResponseEntity.ok(new ArrayList<>()) : ResponseEntity.ok(list);
    }
}
