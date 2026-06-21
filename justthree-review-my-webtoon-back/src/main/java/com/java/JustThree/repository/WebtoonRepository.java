package com.java.JustThree.repository;

import com.java.JustThree.domain.Users;
import com.java.JustThree.domain.Webtoon;
import com.java.JustThree.dto.admin.WebtoonLikeCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebtoonRepository extends JpaRepository<Webtoon,Long> {
    // ageGradCd != "4" 조건으로 성인 웹툰을 제외하고 조회한다. (AgeGrade.ADULT_CODE)
    // 메인 페이지 용 최근 연재 시작된 웹툰
    Page<Webtoon> findByAgeGradCdIsNotOrderByPusryBeginDeDesc(String ageGradCd, Pageable pageable);
    // 메인 페이지 용 최근 연재 끝난 웹툰
    Page<Webtoon> findByAgeGradCdIsNotOrderByPusryEndDeDesc(String ageGradCd, Pageable pageable);
    // 메인 페이지 용 장르이름이 일치하는 웹툰들
    Page<Webtoon> findByAgeGradCdIsNotAndMainGenreCdNmIsOrderByMasterIdDesc(String ageGradCd, String mainGenreCdNm, Pageable pageable);
    // 조회순 웹툰 정렬
    Page<Webtoon> findByAgeGradCdIsNotOrderByViewDesc(String ageGradCd, Pageable pageable);
    // 모든 웹툰 조회
    Page<Webtoon> findByAgeGradCdIsNot(String ageGradCd, Pageable pageable);
    // 제목 검색
    Page<Webtoon> findByAgeGradCdIsNotAndTitleContaining(String ageGradCd, String title, Pageable pageable);
    // 줄거리 검색
    Page<Webtoon> findByAgeGradCdIsNotAndOutlineContaining(String ageGradCd, String outline, Pageable pageable);
    // 작가 이름 검색
    @Query("select w1 " +
            "from Webtoon w1 " +
            "where w1.ageGradCd <> :ageCd and w1 " +
            "in (select w2 from Webtoon w2 where w2.pictrWritrNm like %:writer% or w2.sntncWritrNm like %:writer%)")
    Page<Webtoon> findByAgeGradCdIsNotAndWriterIs(@Param("ageCd") String ageGradCd, @Param("writer") String writer, Pageable pageable);
    //장르 1개 조회
    Page<Webtoon> findByAgeGradCdIsNotAndMainGenreCdNmIs(String ageGradCd, String mainGenreCdNm, Pageable pageable);
    // 장르 2개 조회
    @Query("select w1 " +
            "from Webtoon w1" +
            " where w1.ageGradCd <> :ageCd " +
            "and w1 in " +
            "(select w2 " +
            "from Webtoon w2 " +
            "where w2.mainGenreCdNm = :genre1 or w2.mainGenreCdNm = :genre2)")
    Page<Webtoon> findByAgeGradCdIsNotAndDoubleGenreIs(@Param("ageCd") String ageCd, @Param("genre1") String genre1, @Param("genre2") String genre2, Pageable pageable);
    // 전체 조회
    @Query("SELECT w " +
            " FROM Webtoon w " +
            "LEFT JOIN Star s " +
            "on s.webtoon.masterId = w.masterId " +
            "WHERE w.ageGradCd <> :ageCd " +
            "GROUP BY w.masterId, s.webtoon.masterId" +
            " ORDER BY COALESCE(AVG(s.starVal), 0) DESC")
    Page<Webtoon> findByAgeGradCdIsNotAndOrderByPopularity(@Param("ageCd") String ageCd, Pageable pageable);

    // 장르 1개 별점순 조회
    @Query("SELECT w " +
            "FROM Webtoon w " +
            "LEFT JOIN Star s " +
            "on s.webtoon.masterId = w.masterId " +
            "WHERE w.mainGenreCdNm = :genre1 " +
            "AND w.ageGradCd <> :ageCd " +
            "GROUP BY w.masterId, s.webtoon.masterId " +
            "ORDER BY COALESCE(AVG(s.starVal), 0) DESC")
    Page<Webtoon> findByAgeGradCdIsNotAndGenreIsOrderByPopularity(@Param("ageCd") String ageCd, @Param("genre1") String genre1,Pageable pageable);
    // 장르 2개 별점순 조회
    @Query("SELECT w " +
            "FROM Webtoon w " +
            "LEFT JOIN Star s " +
            "on s.webtoon.masterId = w.masterId " +
            "WHERE (w.mainGenreCdNm = :genre1 OR w.mainGenreCdNm = :genre2) " +
            "AND w.ageGradCd <> :ageCd " +
            "GROUP BY w.masterId, w.title " +
            "ORDER BY COALESCE(AVG(s.starVal), 0) DESC")
    Page<Webtoon> findByAgeGradCdIsNotAndDoubleGenreIsOrderByPopularity(@Param("ageCd") String ageCd, @Param("genre1") String genre1, @Param("genre2") String genre2,Pageable pageable);

    // 장르 3개 별점순 조회
    @Query("SELECT w " +
            "FROM Webtoon w " +
            "LEFT JOIN Star s " +
            "on s.webtoon.masterId = w.masterId " +
            "WHERE (w.mainGenreCdNm = :genre1 OR w.mainGenreCdNm = :genre2 OR w.mainGenreCdNm = :genre3) " +
            "AND w.ageGradCd <> :ageCd " +
            "GROUP BY w.masterId, w.title " +
            "ORDER BY COALESCE(AVG(s.starVal), 0) DESC")
    Page<Webtoon> findByAgeGradCdIsNotAndTripleGenreIsOrderByPopularity(@Param("ageCd") String ageCd, @Param("genre1") String genre1, @Param("genre2") String genre2,@Param("genre3") String genre3,Pageable pageable);
    // 좋아요 많이 된 웹툰 조회
    @Query("select NEW com.java.JustThree.dto.admin.WebtoonLikeCountResponse(w.title, count(w))" +
            "from Webtoon w " +
            "right join Interest i " +
            "on i.webtoon.masterId = w.masterId " +
            "group by i.webtoon.masterId " +
            "order by count(w) desc ")
    Page<WebtoonLikeCountResponse> findTopByCountInterest(Pageable pageable);

}
