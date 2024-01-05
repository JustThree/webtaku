package com.java.JustThree.service.board;

import com.java.JustThree.domain.Board;
import com.java.JustThree.domain.BoardImage;
import com.java.JustThree.domain.Users;
import com.java.JustThree.dto.board.request.AddBoardRequest;
import com.java.JustThree.dto.board.response.GetBoardListResponse;
import com.java.JustThree.dto.board.response.GetBoardOneResponse;
import com.java.JustThree.dto.board.request.UpdateBoardRequest;
import com.java.JustThree.dto.board.response.GetBoardReplyResponse;
import com.java.JustThree.jwt.JwtProvider;
import com.java.JustThree.repository.board.BoardImageRepository;
import com.java.JustThree.repository.board.BoardRepository;
import com.java.JustThree.service.board.BoardImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    //이미지 파일
    private final BoardImageRepository boardImageRepository;
    private final BoardImageService boardImageService;

    //댓글
    private  final BoardReplyService boardReplyService;

    //좋아요
    private final BoardLikeService boardLikeService;

    //토큰관련
    private final JwtProvider jwtProvider;

    //커뮤니티 글 등록
    @Transactional
    public Long addBoard(AddBoardRequest addBoardRequest, String token){
        Long userId = jwtProvider.getUserId(token);
        Board newBoard = Board.builder()
                .title(addBoardRequest.getTitle())
                .content(addBoardRequest.getContent())
                .noticeYn(addBoardRequest.getNoticeYn())
                .users(Users.builder().usersId(userId).build())
                .build();
        boardRepository.save(newBoard);
        //첨부파일 있을 경우
        if(addBoardRequest.getImageFiles() != null && !addBoardRequest.getImageFiles()[0].isEmpty()){
           boardImageService.saveBoardImage(newBoard, addBoardRequest.getImageFiles());
        }
        return newBoard.getBoardId();
    }

    //커뮤니티 글 상세 조회
    @Transactional
    public GetBoardOneResponse getBoardOne(long boardId, String token){
        Optional<Board> optionalBoard = boardRepository.findById(boardId);
        if(optionalBoard.isEmpty()){
            return null;
        }else {
            Board board = optionalBoard.get();
            //해당 글에 대한 이미지  파일
            List<BoardImage> boardImageList = boardImageRepository.findByBoard(board);
            //조회수 증가
            boardRepository.updateViewCount(board.getViewCount()+1, boardId);
            //해당 글에 대한 댓글
            List<GetBoardReplyResponse> boardReplyList = boardReplyService.getBoardReplyList(boardId);
            //해당 글에 대한 좋아요 수
            long boardLikeCount = boardLikeService.getBoardLikeCount(boardId);
            //해당 글에 대한 좋아요 여부(로그인 중)
           if(token==null){
                return GetBoardOneResponse.entityToDTO(board, boardImageList, boardReplyList, false, boardLikeCount);
            }else{
                Long userId = jwtProvider.getUserId(token);
                boolean isBoardLIke = boardLikeService.getBoardLike(boardId, userId);
                return GetBoardOneResponse.entityToDTO(board, boardImageList, boardReplyList, isBoardLIke, boardLikeCount);
            }
        }
    }

    //커뮤니티 글 수정
    @Transactional
    public Long updateBoard(UpdateBoardRequest updateBoardReq){
        Board oldBoard = boardRepository.findById(updateBoardReq.getBoardId())
                .orElseThrow(NoSuchElementException::new);
        oldBoard.updateBoard(updateBoardReq.getTitle(), updateBoardReq.getContent());
        boardRepository.save(oldBoard);

        List<BoardImage> oldBoardImageList = boardImageRepository.findByBoard(oldBoard);
        //기존 첨부파일 없을 경우
        if(oldBoardImageList.isEmpty()){
            //수정요청에 첨부파일 있을 경우
            if(updateBoardReq.getImageFiles() != null && !updateBoardReq.getImageFiles()[0].isEmpty()) {
                 boardImageService.saveBoardImage(oldBoard, updateBoardReq.getImageFiles());
            }
        }else{ //기존 첨부파일 있을 경우
            //수정 케이스1 - 기존 첨부파일 다 삭제한 경우
            if(updateBoardReq.getImageIdList() == null){
                for(BoardImage oldBoardImage : oldBoardImageList){
                    boardImageService.deleteBoardImage(oldBoardImage.getImgId()); //db&s3 모두 삭제
                }
            }else {
                List<Long> updateImageIdList = updateBoardReq.getImageIdList();
                // 삭제 대상 이미지를 확인
                List<BoardImage> imagesToDelete = new ArrayList<>();
                for (BoardImage oldBoardImg : oldBoardImageList) {
                    if (!updateImageIdList.contains(oldBoardImg.getImgId())) {
                        imagesToDelete.add(oldBoardImg);
                    }
                }
                // 삭제 대상 이미지를 실제로 삭제
                for (BoardImage imageToDelete : imagesToDelete) {
                    boardImageService.deleteBoardImage(imageToDelete.getImgId());
                }
            }
            //수정 요청에 첨부파일 있을 경우
            if(updateBoardReq.getImageFiles() != null && !updateBoardReq.getImageFiles()[0].isEmpty()) {
                boardImageService.saveBoardImage(oldBoard, updateBoardReq.getImageFiles());
            }
        }
        return updateBoardReq.getBoardId();
    }

    //커뮤니티 글 삭제
    @Transactional
    public String removeBoard(long boardId){
        try{
            Optional<Board> boardOptional = boardRepository.findById(boardId);
            if(boardOptional.isEmpty()){
                return "NotFoundBoard";
            }else {
                Board board = boardOptional.get();
                //S3에서 삭제
                List<BoardImage> boardImageList = boardImageRepository.findByBoard(board);
                for(BoardImage boardImage: boardImageList){
                    String storedName = boardImage.getStoredName();
                    boardImageService.deleteFile(storedName);
                }
                boardRepository.delete(board);
                return "success";
            }
        }catch (Exception e){
            return e.getMessage();
        }
    }
    //커뮤니티 글 목록 조회
   public Page<GetBoardListResponse> getBoardsByPage(int page, int size, String sortType, String keyword){
        // 정렬  기준(기본 최신순)
        Sort sortByDirection =Sort.by(Sort.Direction.DESC, "created");
        if(sortType.equals("sortDesc")){
            sortByDirection = Sort.by(Sort.Direction.DESC, "created");
        }else if(sortType.equals("sortAsc")) {
            sortByDirection = Sort.by(Sort.Direction.ASC, "created");
        }else if(sortType.equals("sortViewCntDesc")){
            sortByDirection = Sort.by(Sort.Direction.DESC, "viewCount")
                    .and(Sort.by(Sort.Direction.DESC, "created"));//조회수 →최신순
        }
        Pageable pageable = PageRequest.of(page-1, size, sortByDirection);

        // 검색어를 포함하는 게시글만 조회하는 쿼리 작성
        Specification<Board> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("noticeYn"), 0), // noticeYn이 0인 게시글만 조회
                        criteriaBuilder.or(
                                criteriaBuilder.like(root.get("title"), "%" + keyword + "%"), // 제목에 검색어 포함
                                criteriaBuilder.like(root.get("content"), "%" + keyword + "%") // 내용에 검색어 포함
                        )
                );
        Page<Board> boardPage = boardRepository.findAll(specification, pageable);
        List<Board> boardList = boardPage.getContent();

        if(boardList.isEmpty()) {
            System.out.println(boardList);
        }
        List<GetBoardListResponse> responseList = boardList.stream()
                .map(GetBoardListResponse::entityToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, boardPage.getTotalElements());
    }

    //공지 글목록 조회
    public Page<GetBoardListResponse> getNoticesByPage(String keyword, Pageable pageable) {
        Specification<Board> specification = (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("noticeYn"), 1), // noticeYn이 1인 공지 게시글만 조회
                        criteriaBuilder.or(
                                criteriaBuilder.like(root.get("title"), "%" + keyword + "%"), // 제목에 검색어 포함
                                criteriaBuilder.like(root.get("content"), "%" + keyword + "%") // 내용에 검색어 포함
                        )
                );

        Page<Board> noticeBoardPage = boardRepository.findAll(specification, pageable);
        return noticeBoardPage.map(GetBoardListResponse::entityToDTO);
    }



}
