<script setup>
import Board from "@/components/board/board.vue";
import {onMounted, ref, nextTick, watch} from "vue";
import {api} from "@/common.js";
import {useRoute} from "vue-router";
import {useAuthStore} from "@/stores/auth.store.js";
import {storeToRefs} from "pinia";
import router from "@/router/index.js";
const route = useRoute();
const commBoardList = ref([]);
const pagingMsg = ref("시간이 조금 걸립니다:)");
let loginUsersId = ref();
let page = 1;
const itemPerPage = 10;
let isLoading = false; // 데이터 로딩 중인지 여부를 나타내는 변수
let commCount = ref(0);
const selectedSort = ref('최신순');
const sortOptions = ['최신순', '오래된순', '조회수순'];
const sortType = ref('sortDesc');
const sortBoards = async () => {
    if(selectedSort.value === '최신순'){
        sortType.value = 'sortDesc';
    }else if (selectedSort.value === '오래된순'){
        sortType.value = 'sortAsc';
    }else if(selectedSort.value === '조회수순'){
        sortType.value = 'sortViewCntDesc';
    }
    page = 1;
    commBoardList.value = [];
    pagingMsg.value = "시간이 조금 걸립니다:)";
    await loadMoreBoards();
};
watch(selectedSort, () => {
    sortBoards();
});
function gotoCreateBoard(){
    if(loginUsersId.value===-1){
        alert("로그인해야 가능한 서비스입니다.");
        router.replace("/user/login");
        return;
    }
    router.replace('/comm/new');
}
//검색
const searchKeyword = ref(""); // 검색어
const searchBoard = async () => {
    //if (!searchKeyword.value.trim()) {return;}
    console.log(searchKeyword.value);
    page = 1;
    commBoardList.value = [];
    pagingMsg.value = "시간이 조금 걸립니다:)";
    await loadMoreBoards();
};
async function loadMoreBoards(){
    console.log(page);
    if (isLoading) {
        return; // 이미 로딩 중인 경우 중복 호출을 방지
    }
    isLoading = true; // 로딩 상태로 변경
    try {
        const response = await api(
            `board?page=${page}&size=${itemPerPage}&sortingType=${sortType.value}&keyword=${searchKeyword.value}`, "GET");
        if (response instanceof Error) {
            console.log(response);
        } else {
            commCount.value = response.totalElements;
            const { content } = response;
            if (content.length > 0) {
                commBoardList.value = [...commBoardList.value, ...content];
                page++;
            }
        }
    } catch (error) {
        console.log(error);
    } finally {
        isLoading = false;
    }
}
const load =({ done }) => {
    setTimeout(async ()=>{
        await  loadMoreBoards();
        if(!isLoading){done("empty")}
        else{done("ok")}
    }, 2000)
}
watch(page, () => {loadMoreBoards();})
onMounted(async  ()=>{
    const authStore = useAuthStore()
    const { user } = storeToRefs(authStore);
    if(user.value!==null){loginUsersId.value = user.value.usersId;}
    else {loginUsersId.value =-1;}
    await loadMoreBoards();
});
</script>
<template>
    <v-container>
        <v-row>
            <div class="menu-frame">
                <div class="sort-menu-frame">
                    <v-select
                        v-model="selectedSort"
                        :items="sortOptions"
                        class="sort-menu"
                        variant="outlined"
                        @change="sortBoards">
                    </v-select>
                </div>
                <div class="search-create-frame">
                    <div class="search-input-frame">
                        <div class="search-input">
                            <v-text-field
                                variant="standard" maxlength="20"
                                bg-color="#EDE7F6" v-model="searchKeyword"
                                placeholder="검색 키워드를 작성해주세요(20자 이내)"
                                @keyup.enter="searchBoard">
                            </v-text-field>
                        </div>
                        <v-btn class="search-btn"  variant="text" @click="searchBoard">검색</v-btn>
                    </div>
                    <div class="create-btn-frame">
                        <v-btn class="create-btn" variant="tonal" @click="gotoCreateBoard" >
                            <v-icon left>mdi-pencil</v-icon> 글쓰기
                        </v-btn>
                    </div>
                </div>
            </div>
        </v-row>
        <v-row>
            <div class="board-count-frame">
                <p class="text-h6 font-weight-medium"><span class="font-weight-bold" style="color:#FB8C00">{{commCount}}</span>건</p>
            </div>
        </v-row>
        <v-infinite-scroll  class="infinte-frame" :onLoad="load">
            <template v-for="(data, idx) in commBoardList" :key="idx">
                <Board :boardone="data"></Board>
            </template>
            <template v-slot:loading>
                <div v-if="isLoading">로딩 중...</div>
                <div v-else>시간이 조금 걸립니다:)</div>
            </template>
        </v-infinite-scroll>
    </v-container>
</template>
<style scoped>
.menu-frame{
    width: 100%;
    height: 100px;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    gap: 10px;
    line-height: 100px;
    margin: 5px;
    padding: 15px;
}
/*  정렬 */
.sort-menu-frame{
    width: 25%;
    margin-right: 130px;
    padding-top: 25px;
    display: flex;
    align-content: center;
    align-items: center;
}
.sort-menu{
    width: 100%;
    padding-top: 5px;
    text-decoration: none;
}
.search-create-frame{
    width: 100%;
    display: flex;
    justify-content: flex-end;
    align-content: center;
    align-items: center;
}
/*  검색 */
.search-input-frame{
    min-width: 60%;
    height: 55px;
    line-height: 100px;
    display: flex;
    gap: 3px;
    margin-left: 130px;
}
.search-input{
    width: 90%;
    height: 50px;
}
.search-btn {
    color: #8F7CEE;
    font-weight: bold;
}
.v-btn--size-default {
    --v-btn-size: 0.875rem;
    --v-btn-height: 50px;
}
/*  작성 버튼 */
.create-btn-frame{
    width: 10%;
}
.create-btn {
    width: 100%;
    background-color: #8F7CEE;
    color: white;
}
.board-count-frame{
    margin: 5px;
    padding-left: 20px;
}
/*스크롤 CSS*/
.infinte-frame {
    min-height: 700px;
    margin: 5px;
}
::-webkit-scrollbar {
    display: none;
}
</style>