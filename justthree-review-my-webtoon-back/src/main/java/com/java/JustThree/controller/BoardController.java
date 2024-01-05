package com.java.JustThree.controller;

import com.java.JustThree.dto.board.request.*;
import com.java.JustThree.dto.board.response.GetBoardListResponse;
import com.java.JustThree.dto.board.response.GetBoardOneResponse;
import com.java.JustThree.exception.BoardNotFoundException;
import com.java.JustThree.service.board.BoardLikeService;
import com.java.JustThree.service.board.BoardReplyService;
import com.java.JustThree.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final BoardLikeService boardLikeService;

    //커뮤니티 글 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> saveBoard(@ModelAttribute AddBoardRequest addBoardRequest,
                                            @RequestHeader(value = "Authorization", required = false) String token){
        try{
            Long res = boardService.addBoard(addBoardRequest, token);
            log.info("글 등록 pk"+res);
            return ResponseEntity.ok("1");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // 커뮤니티 글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getBoardOne(@PathVariable("id") long id,
                                         @RequestHeader(value = "Authorization", required = false) String token){
        try{
            GetBoardOneResponse boardOneRes = boardService.getBoardOne(id,token);
            //찾는 글 없을 때
            if( boardOneRes != null){
                return ResponseEntity.status(HttpStatus.OK).body(boardOneRes);
            }else{
                throw new BoardNotFoundException(id+"Not Found");
            }
        }catch (BoardNotFoundException bnfe){
            log.error(bnfe.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(bnfe.getMessage());
        }
    }
    // 커뮤니티 글 수정
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateBoard(@PathVariable("id") long boardId,
                                              @ModelAttribute UpdateBoardRequest updateBoardRequest){
        updateBoardRequest.setBoardId(boardId);
        try{
           Long res = boardService.updateBoard(updateBoardRequest);
            return ResponseEntity.ok().body("글 수정 "+res);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //커뮤니티 글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeBoard(@PathVariable("id")long id){
        try{
            String res = boardService.removeBoard(id);
            return ResponseEntity.ok(id+"글 삭제 "+res);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //커뮤니티 게시글 목록(noticeYn=0) 조회
   @GetMapping
    Page<GetBoardListResponse> getBoardList(@RequestParam(name = "page", defaultValue = "1") int page,
                                            @RequestParam(name = "size", defaultValue = "10") int size,
                                            @RequestParam(name = "sortingType", defaultValue = "sortDesc") String sortingType,
                                            @RequestParam(name = "keyword", required = false) String keyword){
        String searchWord="";
        if(keyword != null){
            searchWord = keyword;
        }
        return boardService.getBoardsByPage(page, size, sortingType, searchWord);
    }
    //공지 목록 조회
    @GetMapping("/notice")
    public Page<GetBoardListResponse> getNoticeList(@RequestParam(name = "page", defaultValue = "1") int page,
                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                             @RequestParam(name = "keyword", required = false) String keyword){
        String searchWord = "";
        if(keyword != null){
            searchWord = keyword;
        }
        Pageable pageable = PageRequest.of(page-1, size, Sort.by(Sort.Direction.DESC, "created"));
        return boardService.getNoticesByPage(searchWord, pageable);
    }
    //게시글 좋아요
    @PostMapping("/likes")
    public ResponseEntity<?> addBoardLike(@RequestBody AddBoardLikeRequest addBoardLikeReq,
                                          @RequestHeader(value = "Authorization", required = false) String token){
        try{
            Long res = boardLikeService.addLike(addBoardLikeReq, token);
            return ResponseEntity.ok(res);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //게시글 좋아요 취소(삭제)
    @DeleteMapping("/likes/{id}")
    public ResponseEntity<String> removeBoardLike(@PathVariable("id")long boardId,
                                                  @RequestHeader(value = "Authorization", required = false) String token){
        try{
            String res = boardLikeService.removeBoardLike(boardId, token);
            return ResponseEntity.ok(boardId+"글 좋아요 취소 "+res);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
