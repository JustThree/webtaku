<script setup>
import {onMounted, ref} from "vue";
import {useRoute} from "vue-router";
import {api, apiToken} from "@/common.js";
import router from "@/router/index.js";
import BoardReply from "@/components/board/boardReply.vue";
import {useAuthStore} from "@/stores/auth.store.js";
import {storeToRefs} from "pinia";


const authStore = useAuthStore();

const board = ref({
    title: '',
    content: '',
    boardFiles: [],
    noticeYn: 0,
    writerUsersId: 0,
    //기존 데이터 조회
    boardImgMapList: [new Map([[], []])],
    boardId: 0,
    boardReplyListObj:{},
    boardReplyList:[],
    replyList: [],
    reReplyList:[],
    boardLikeYn: false,
    boardLikeCount: 0,
});

const route = useRoute();

//로그인한 유저 확인
let loginUsersId = ref();

//rereplylist
let boardreReplyList = ref([]);

//게시글 좋아요 등록 및 취소
async function toggleLike() {
    if(!board.value.boardLikeYn) {  //좋아요 등록
        const response = await api("board/likes", "POST", {
            "boardId": board.value.boardId
        });
        if (response instanceof Error) {
            console.log(response.response.data);
        } else {
            if (response) {
                board.value.boardLikeYn= true;
                board.value.boardLikeCount++;
            } else {
                alert("등록 실패..");
            }
        }
    }else{ //좋아요 취소
        const response = await api("board/likes/"+board.value.boardId, "DELETE");
        if (response instanceof Error) {
            console.log(response.response.data);
        } else {
            if (response) {
                board.value.boardLikeYn= false;
                board.value.boardLikeCount--;
            } else {
                alert("삭제 실패..");
            }
        }
    }
}

//게시글 수정
function gotoUpdateBoard(){
    //console.log(board.value.boardId);
    let bId = board.value.boardId;
    router.push({ name: 'updatedBoard', params: { bId}});
}

//게시글 삭제
async function delBoard(){
    if(confirm("정말 삭제하시겠습니까?")){
        //let bId = board.value.boardId;
        const response = await api("board/"+board.value.boardId, "DELETE");
        if (response instanceof Error) {
            console.log(response.response.data);
        } else {
            if (response) {
                console.log("삭제");
                alert("글이 삭제되었습니다.");
                //router.replace("/boardlist"); //글 목록으로 이동
            } else {
                alert("삭제 실패..");
            }
        }
    }
}

//댓글 등록
const txtReply = ref('');
const submitReply = async () => {
    console.log(loginUsersId.value);
    if(!loginUsersId.value){
            alert("로그인해야 댓글 등록 가능합니다.");
            router.replace("/user/login");
            return;
    }
    if(!txtReply.value.trim()){
            alert('댓글을 입력해주세요');
            return;
    }
   const response = await api("boardreply", "POST", {
            "boardId": board.value.boardId,
            "boardReplyContent": txtReply.value,
            "parentReplyId" : 0
   });
   if (response instanceof Error) {
            console.log(response.response.data); //서버에서 예외처리 필요
   } else {
        if (response) {
                console.log("성공");
                alert("댓글이 성공적으로 등록되었습니다.");
                txtReply.value="";
                router.go();
        } else {
              alert("등록 실패..");
        }
   }
}
//댓글 삭제 처리[Component(BoardReply) 관련]
const handleDeleteReply = async (delBoardReply) =>{
    console.log("삭제 예정", delBoardReply);
    console.log("삭제 예정", delBoardReply.boardReplyId);
    if(confirm("정말 삭제하시겠습니까?")){
        const response = await api("boardreply/"+delBoardReply.boardReplyId, "DELETE");
        if (response instanceof Error) {
            console.log(response.response);
        } else {
            if (response) {
                console.log("삭제");
                alert("댓글이 삭제되었습니다.");
                /*await getData();*/
                router.go(0);
            } else {
                alert("삭제 실패..");
            }
        }
    }
};
//댓글 수정 처리[Component(BoardReply) 관련]
const handleUpdateReply = async (editedReply)=>{
    //console.log("수정 요청 예정", editedReply);
    if(!editedReply.updatedReplyContent.trim()){
        alert('내용을 입력해주세요');
        return;
    }
    const response = await api("boardreply/"+editedReply.boardReplyId, "PUT", {
        "boardId": editedReply.boardId,
        "boardReplyContent": editedReply.updatedReplyContent
    });
    if (response instanceof Error) {
        console.log(response.response);
    } else {
        if (response) {
           // console.log("성공");
            alert("댓글이 성공적으로 수정되었습니다.");
            await getData();
        } else {
            alert("수정 실패..");
        }
    }
}
//대댓글 등록 처리[Component(BoardReply) 관련]
const handleCreateReReply = async (newReReply)=>{
    console.log("newReReply", newReReply);
    if(!loginUsersId.value){
        alert("로그인해야 댓글 등록 가능합니다.");
        router.replace("/user/login");
        return;
    }
    if(!newReReply.boardReplyContent.trim()){
        alert('내용을 입력해주세요');
        return;
    }
    if(confirm("대댓글은 수정 및 삭제가 안됩니다. 등록하시겠습니까?")){
        const response = await api("boardreply", "POST", {
        "boardId": newReReply.boardId,
        "boardReplyContent": newReReply.boardReplyContent,
        "parentReplyId" : newReReply.parentReplyId
        });
        if (response instanceof Error) {
            console.log(response.response.data);
             } else {
            if (response) {
                alert("댓글이 성공적으로 등록되었습니다.");
                await getData();
            } else {
                alert("등록 실패..");
            }
        }
    }
    else{
        return;
    }
}
const addLineBreaks = (text) => {
    return text.replace(/\n/g, "<br>");
};
//데이터 조회
const getData = async () =>{
    //console.log(JSON.parse(authStore.user.token).accessToken );
    console.log(route.params.boardId);
    api("board/"+route.params.boardId, "GET")
        .then((response)=>{
            if(response instanceof Error){
                let errorRes = response;
                console.log(errorRes.response);
                //not found 글 목록으로 이동
            }else{
                console.log(response);
                board.value = response;
                board.value.content = board.value.content.split('\n').join('<br>');
                // 댓글, 대댓글
                board.value.replyList = board.value.boardReplyList.filter(reply => reply.parentReplyId === 0);
                board.value.reReplyList = board.value.boardReplyList.filter(reply => reply.parentReplyId !== 0);
                boardreReplyList = board.value.reReplyList;
                if(board.value.boardImgMapList.length===0) {
                    board.value.boardFiles = [];
                }
            }
        })
};
onMounted(async () =>{
    const authStore = useAuthStore()
    const { user } = storeToRefs(authStore);
    console.log("user", user);
    if(user.value!==null){
        loginUsersId.value = user.value.usersId;
    }else {
        loginUsersId.value =-1;
    }
    await getData();
});
</script>

<template>
    <v-container>
        <v-row>
            <div class="top-frame">
                <div class="top-title-frame" >
                    <span class="top-title-text">{{board.title}}</span>
                </div>
                <div class="top-desc-frame" >
                    <span>{{board.userNickname}}</span>
                    <span v-if="board.created === board.updated"  class="font-weight-light"> 등록일자 {{board.created}}</span>
                    <span v-else-if="board.created !== board.updated" class="font-weight-light">수정일자 {{ board.updated}}</span>
                    <span><v-icon>mdi-eye</v-icon>{{board.viewCount}}</span>
                </div>
                <div class="top-bottom-frame">
                    <v-btn
                        v-if="loginUsersId !== -1"
                        :variant="board.boardLikeYn ? 'plain' : 'text'"
                        @click="toggleLike">
                        <v-icon left >{{ board.boardLikeYn ? 'mdi-heart' : 'mdi-heart-outline' }}</v-icon>
                        <span>{{ board.boardLikeYn ? '좋아요 취소' : '좋아요' }}</span>
                    </v-btn>
                    <span><v-icon left style="color:red">mdi-heart</v-icon> {{  board.boardLikeCount  }} </span>
                    <div class="edit-btns" v-if="loginUsersId === board.writerUsersId">
                        <v-btn variant="flat" @click="gotoUpdateBoard">  수정  </v-btn>
<!--                        <v-btn variant="tonal" @click="delBoard">  삭제  </v-btn>-->
                    </div>
                </div>
            </div>
        </v-row>
        <!-- 이미지 첨부파일   -->
        <v-row v-if="board.boardImgMapList.length > 0">
            <v-carousel show-arrows="hover">
                <v-carousel-item v-for="(imgMap, index) in board.boardImgMapList" :key="index" >
                    <div>
                        <div class="images-frame">
                            <div class="text-overline">{{imgMap.originName}}</div>
                            <v-img class="bg-white" width="300" :src="imgMap.accessUrl" cover></v-img>
                        </div>
                    </div>
                </v-carousel-item>
            </v-carousel>
        </v-row>
        <!-- 게시글 내용 -->
        <v-row>
            <div class="content-frame" >
                <div v-html="board.content"></div>
            </div>
        </v-row>
        <!--  댓글 Div     -->
        <v-row v-if="$route.query.noticeYn !== '1'">
            <div class="reply-title-frame">
                <span class="text-h5">
                    댓글  <span class="font-weight-bold">{{board.boardReplyList.length}}</span>개 </span>
            </div>
        </v-row>
        <v-row no-gutters v-if="$route.query.noticeYn !== '1'">
            <div class="reply-input-frame">
                <v-text-field
                    v-model="txtReply"
                    placeholder="댓글을 입력해주세요"
                    variant="outlined"
                @keyup.enter="submitReply">
                </v-text-field>
                <v-btn class="reply-submit-btn" variant="tonal" @click="submitReply" >등록</v-btn>
            </div>
        </v-row>
        <!--  댓글 목록   -->
        <v-row v-if="board.boardReplyList.length>0">
            <div class="reply-list-frame">
                <div v-for="(data, idx) in board.boardReplyList" :key="idx">
                    <BoardReply
                        :boardreply="data"
                        :writer-users-id="board.writerUsersId"
                        :loginUsersId="loginUsersId"
                        :reReplyList = "board.reReplyList"
                        @delBoardReply="handleDeleteReply"
                        @saveUpdatedReply="handleUpdateReply"
                        @createReReply="handleCreateReReply">
                    </BoardReply>
                </div>
            </div>
        </v-row>
    </v-container>
</template>
<style scoped>
.custom-menu-link{
    color: inherit;
    text-decoration: none;
    font-size: 30px;
    font-weight: 700;
}
/*게시글 제목 */
.top-frame{
    width: 100%;
    height: 150px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin-top: 20px;
    padding: 10px;
    background-color: #EDE7F6;
    border-top: 1px;
    border-bottom: 1px;
}
.top-title-frame{
    padding: 5px;
    margin-bottom: 3px;
}
.top-title-text{
    padding-left: 15px;
    font-size: 22px;
    font-weight: 600;
}
.top-desc-frame{
    width: 90%;
    display: flex;
    justify-content: flex-start;
    flex-direction: row;
    align-items: center;
    margin: 5px;
    padding-left: 15px;
    gap: 20px;
}
.top-bottom-frame{
    width: 90%;
    display: flex;
    justify-content: flex-start;
    flex-direction: row;
    align-items: center;
    margin: 5px;
    gap: 20px;
}
.edit-btns{
    display: flex;
    gap: 5px;
    margin-left: 10px;
}
/* 게시글 본문 이미지 */
.images-frame {
    margin: 10px;
    padding: 5px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    align-content: center;
    background-color:  #ffffff;
}
::v-deep .v-carousel__controls {
    background: #ffffff;
}
::v-deep .v-carousel__controls .v-carousel__control-dots{
    background: #7B68EE;
}
/* 게시글 본문 내용 */
.content-frame{
    width: 100%;
    min-height: 200px;
    margin: 10px;
}
/* 댓글 */
.reply-title-frame{
    margin: 10px;
}
.reply-input-frame{
    width: 95%;
    display: flex;
    flex-direction: row;
    gap: 10px;
    margin: 5px;
    padding: 10px;
}
.reply-input-frame .v-btn--size-default {
    --v-btn-size: 0.875rem;
    --v-btn-height: 57px;
    font-size: var(--v-btn-size);
    min-width: 65px;
    padding: 0 16px;
}
.reply-submit-btn{
    height: 30px;
    color:white;
    background-color: #7B68EE;
}
.reply-list-frame{
    width: 95%;
}
</style>