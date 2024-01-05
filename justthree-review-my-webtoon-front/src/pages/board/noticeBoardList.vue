<template>
    <v-container>
        <!-- 검색 Frame -->
        <v-row class="d-flex justify-center align-center">
            <div class="search-frame">
                <v-text-field
                    class="input-keyword"
                    variant="standard"
                    maxlength="20"
                    bg-color="#EDE7F6"
                    :style="{ 'font-weight': 700  }"
                    v-model="searchKeyword"
                    placeholder="검색 키워드를 작성해주세요">
                </v-text-field>
            </div>
        </v-row>
        <!-- 글 목록   Frame-->
        <v-row>
            <div class="board-count-frame">
                <p class="text-h6 font-weight-medium"><span  class="font-weight-bold" style="color:#FB8C00">{{noticeCount}}</span>건</p>
            </div>
        </v-row>
        <v-row>
            <!-- 글 목록     -->
            <v-col cols="12">
                <div class="notice-table-container">
                    <v-simple-table>
                        <template v-slot:default>
                            <table class="notice-table">
                                <thead>
                                <tr>
                                    <th>제목</th>
                                    <th>작성자 </th>
                                    <th>작성일 </th>
                                    <th>조회수 </th>
                                </tr>
                            </thead>
                            <tbody v-if="noticeCount>0">
                            <tr v-for="(data, idx) in noticeList" :key="idx"
                                @click="$router.push({path: `/boards/${data.boardId}`, query:{noticeYn: data.noticeYn}})">
                                <td>{{ data.title }}</td>
                                <td>{{ data.userNickname }}</td>
                                <td>{{ data.created}}</td>
                                <td>{{ data.viewCount}}</td>
                            </tr>
                            </tbody>
                                <tbody v-else-if="noticeCount===0">
                                <tr>
                                    <td colspan="4">조회된 결과가 없습니다.</td>
                                </tr>
                                </tbody>
                            </table>
                        </template>
                    </v-simple-table>
                </div>
            </v-col>
            <v-col cols="12">
                <!-- 페이지네이션 -->
                <div class="pagination-frame"  v-if="noticeList.length>0">
                    <ul class="pagination-list" >
                        <li>
                            <button :disabled="currentPage === 1" @click="goToPage(1)">
                                <v-icon>mdi-chevron-double-left</v-icon>
                            </button>
                        </li>
                        <li>
                            <button :disabled="currentPage === 1" @click="goToPage(currentPage - 1)">
                                <v-icon>mdi-chevron-left</v-icon>
                            </button>
                        </li>
                        <li v-for="pageNumber in visiblePageNumbers" :key="pageNumber">
                            <button :class="{ active: pageNumber === currentPage }" @click="goToPage(pageNumber)">
                                {{ pageNumber }}
                            </button>
                        </li>
                        <li>
                            <button :disabled="currentPage === totalPages" @click="goToPage(currentPage + 1)">
                                <v-icon>mdi-chevron-right</v-icon>
                            </button>
                        </li>
                        <li>
                            <button :disabled="currentPage === totalPages" @click="goToPage(totalPages)">
                                <v-icon>mdi-chevron-double-right</v-icon>
                            </button>
                        </li>
                    </ul>
                </div>
            </v-col>
        </v-row>
    </v-container>
</template>
<script setup>
import {api} from "@/common.js";
import {computed, onMounted, ref, watch} from "vue";
import NoticeBoard from "@/components/board/noticeBoard.vue";

//검색
const searchKeyword = ref("");

// 페이징 관련
const currentPage = ref(1); // 현재 페이지
const noticePerPage = 10; //페이지당  글 수
const noticeList = ref([]); // 공지사항 목록
const totalPages = ref(0);//전체 페이지 수
const noticeCount = ref(0); //전체 글 수
//페이지 5개만 보이게
const visiblePageNumbers = computed(() => {
    const currentPageNumber = currentPage.value;
    const totalPageNumber = totalPages.value;
    const visiblePages = [];
    let startPage = currentPageNumber - 2;
    let endPage = currentPageNumber + 2;

    if (startPage <= 0) {
        startPage = 1;
        endPage = Math.min(5, totalPageNumber);
    } else if (endPage > totalPageNumber) {
        startPage = Math.max(totalPageNumber - 4, 1);
        endPage = totalPageNumber;
    }
    for (let i = startPage; i <= endPage; i++) {
        visiblePages.push(i);
    }
    return visiblePages;
});

// 페이지 번호 클릭 시 해당 페이지로 이동
async function goToPage(pageNumber) {
    console.log(pageNumber);
    currentPage.value = pageNumber;
    await getData();
}
// currentPage, noticePerPage, searchKeyword 값이 변경될 때마다 getData 함수 호출
watch([currentPage, noticePerPage, searchKeyword], async () => {
    await getData();
});
//데이터 조회
const getData = async () => {
    console.log(currentPage.value);
    api("board/notice?page=" +   currentPage.value+"&size=" + noticePerPage +
        "&keyword=" + searchKeyword.value, "GET")
        .then((response) => {
                if (response instanceof Error) {
                    let errorRes = response;
                    console.log(errorRes.response);
                    noticeList.value = [];
                } else {
                    console.log("response", response);
                    noticeCount.value = response.totalElements;
                    noticeList.value = response.content;
                    totalPages.value = response.totalPages;
                    //console.log("notice.value", noticeList.value);
                }
            }
        );
};
onMounted(async () =>{
    await  getData();
})
</script>

<style scoped>
.search-frame{
    width:  50%;
    display: flex;
    text-align: center;
}
.board-count-frame{
    margin: 5px;
    padding-left: 20px;
}
.notice-table-container{
    position: relative;
    display: block;
    overflow: auto;
}
.notice-table {
    width: 100%;
    height: 500px;
    border-collapse: collapse;
    text-align: center;
}
.notice-table thead{
    height: 25px;
    position: sticky;
    top: 0;
}
.notice-table th, .notice-table td {
    height: 20px;
    padding: 8px;
}
.notice-table th {
    border-bottom: 1px solid black;
    font-weight: bold;
}
 .notice-table td {
     height: 25px;
     padding: 10px;
 }
.notice-table tbody :hover {
    background-color: lightgray;
    cursor: pointer;
}
.notice-table tbody tr:first-child {
    position: sticky;
    top: 0;
}
/* 페이징 */
.pagination-list {
    list-style-type: none;
    display: flex;
    justify-content: center;
    align-items: center;
}
.pagination-list li {
    margin: 0 5px;
}
.pagination-list button {
    padding: 5px 10px;
    background-color: #ffffff;
    border: none;
    cursor: pointer;
}
.pagination-list button:hover {
    background-color: #bdbdbd;
}
.pagination-list button.active {
    background-color: #BEADFA;
    color: #ffffff;
}
</style>