<script setup>
import {ref, toRef, toRefs, watchEffect} from "vue";
import {useRouter} from "vue-router";

const router = useRouter();
const props = defineProps({
    boardreply: Object,
    writerUsersId: Number,
    loginUsersId: Number,
    reReplyList: Object,
});
const {boardReplyId, boardId, boardReplyContent, replyCreated,
    replyUpdated, parentReplyId, replyUsersId, replyUserEmail,
    replyUserNickname} = toRefs(props.boardreply);
const pprops = toRef(props, "boardreply");
const writerId = props.writerUsersId;

watchEffect(()=>{
    boardReplyId.value = pprops.value.boardReplyId;
    boardId.value = pprops.value.boardId;
    //writerUsersId.value = pprops.value.writerUsersId;
    boardReplyContent.value = pprops.value.boardReplyContent;
    replyCreated.value = pprops.value.replyCreated;
    replyUpdated.value = pprops.value.replyUpdated;
    parentReplyId.value = pprops.value.parentReplyId;
    replyUsersId.value = pprops.value.replyUsersId;
    replyUserEmail.value = pprops.value.replyUserEmail;
    replyUserNickname.value = pprops.value.replyUserNickname;
});
const emit = defineEmits([]);

//댓글 수정
const isUpdateModalOpen = ref(false);
const updatedReplyContent = ref('');

//수정 버튼 클릭 시 modal 열기
const openUpdateModal = () =>{
    updatedReplyContent.value = boardReplyContent.value;
    isUpdateModalOpen.value = true;
}
// 댓글 수정 modal 닫기
const closeUpdateModal = () =>{
    isUpdateModalOpen.value = false;
}
// (modal)댓글 수정 → 부모 컴포넌트로 전달
const saveUpdatedReply = () =>{
    // 수정된 댓글 내용을 저장하고 부모 컴포넌트로 전달
    const editedReply = {
        boardReplyId: boardReplyId.value,
        replyUsersId: replyUsersId.value,
        boardId: boardId.value,
        updatedReplyContent: updatedReplyContent.value
    };
    emit('saveUpdatedReply', editedReply);
    isUpdateModalOpen.value = false;
}
//대댓글 등록
const isReReplyModalOpen = ref(false);
const createdReReplyContent = ref('');

//대댓 등록 버튼 클릭 시 modal 열기
const openReReplyModal = () =>{
    isReReplyModalOpen.value = true;
}
//대댓글 modal 취소
const closeReReplyModal = () =>{
    createdReReplyContent.value = '';
    isReReplyModalOpen.value = false;
}
//대댓글 modal에서 등록(저장)
const createReReply = () =>{
    // 대댓글 내용을 저장하고 부모 컴포넌트로 전달
    const newReReply = {
        boardId: boardId.value,
        boardReplyContent: createdReReplyContent.value,
        parentReplyId: boardReplyId.value
    };
    emit('createReReply', newReReply);
    isReReplyModalOpen.value = false;
    createdReReplyContent.value = '';

}
//댓글 삭제 -> 부모 컴포넌트로 전달
const delBoardReply=()=>{
    const deletedReply = {"boardReplyId": boardReplyId.value };
    emit('delBoardReply', deletedReply);
}
</script>
<template>
    <div class="d-flex align-center flex-column">
        <v-card class="card-reply" v-if="parentReplyId === 0" variant="outlined">
            <div class="card-header">
                <div class="title-wrapper">
                    <span class="text-overline" v-if="replyUsersId === writerId">  글 작성자 | </span>
                    <span class="text-subtitle-1">{{ replyUserNickname }}</span>
                </div>
                <div v-if="parentReplyId===0">
                    <v-btn variant="text" @click="openReReplyModal"> 대댓글 등록 </v-btn>
                </div>
                <div class="reply-edit-btns" v-if="loginUsersId === replyUsersId">
                    <v-btn variant="outlined" @click="openUpdateModal">  수정  </v-btn>
                    <v-btn variant="tonal" @click="delBoardReply">  삭제  </v-btn>
                </div>
            </div>
            <div class="card-desc">
                <p class="text-caption">등록일자 {{ replyCreated }}</p>
            </div>
            <div class="card-body">
                <p>{{ boardReplyContent }}</p>
            </div>
        </v-card>
        <!-- 대댓글 목록 -->
        <div  class="rereply-list-frame" v-for="reReply in reReplyList" :key="reReply.boardReplyId">
            <template v-if="reReply.parentReplyId === boardReplyId">
                <v-card class="card-re-reply" variant="text" >
                    <div class="card-re-reply-arrow"> →  </div>
                    <div class="card-re-reply-content">
                        <div class="re-card-header">
                            <div class="re-card-hearder-wrapper">
                                <span
                                    class="text-overline"
                                      v-if="reReply.replyUsersId === writerUsersId">  글 작성자 | </span>
                                <span class="text-subtitle-1">{{ reReply.replyUserNickname }}</span>
                            </div>
                            <p class="text-caption"  style="padding-left:5px">등록일자 {{ reReply.replyCreated }}</p>
                        </div>
                        <div class="re-reply-card-body">
                            <p>{{ reReply.boardReplyContent }}</p>
                        </div>
                    </div>
                </v-card>
            </template>
        </div>
        <!-- 대댓글 등록 Modal   -->
        <v-dialog v-model="isReReplyModalOpen" max-width="500px">
            <v-card>
                <v-card-title>대댓글 등록</v-card-title>
                <v-card-text>
                    <v-textarea
                        no-resize
                        clearable
                        clear-icon="mdi-close-circle"
                        v-model="createdReReplyContent"
                        label="댓글 내용"
                        rows="3">
                    </v-textarea>
                </v-card-text>
                <v-card-actions>
                    <v-btn color="primary" @click="createReReply">저장</v-btn>
                    <v-btn @click="closeReReplyModal">취소</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <!-- 댓글 수정 Modal   -->
        <v-dialog v-model="isUpdateModalOpen" max-width="500px">
            <v-card>
                <v-card-title>댓글 수정</v-card-title>
                <v-card-text>
                    <v-textarea
                        no-resize
                        clearable
                        clear-icon="mdi-close-circle"
                        v-model="updatedReplyContent"
                        label="댓글 내용"
                        rows="3">
                    </v-textarea>
                </v-card-text>
                <v-card-actions>
                    <v-btn color="primary" @click="saveUpdatedReply">저장</v-btn>
                    <v-btn @click="closeUpdateModal">취소</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>
<style scoped>
/* 댓글 */
.card-reply{
    width: 95%;
    margin:  5px;
    padding: 5px;
}
/* 작성자 & 댓글 관련 버튼 */
.card-header{
    width: 100%;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    align-content: center;
    margin: 3px;
    padding: 2px;
    gap: 3px;
}
.reply-edit-btns{
    display: flex;
    gap: 3px;
}
.card-desc{ /*  댓글 등록일자 */
    margin: 3px;
    padding: 2px;
}
.card-body, .reply-card-body{ /*  댓글 내용 */
    margin: 3px;
    padding: 2px;
}
/* 대댓글 */
.rereply-list-frame{
    width: 95%;
}
.card-re-reply{
    width: 100%;
    display:flex;
    margin-top:  5px;
    padding: 5px;
}
.card-re-reply-arrow{
    display: flex;
    align-items: center;
}
.card-re-reply-content{
    width: 100%;
    margin-left: 10px;
    padding: 3px;
    background-color: #D5C2EE;
    border-radius: 5px;
}
.re-card-header {
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    margin: 3px;
    padding: 5px;
    gap: 3px;
}
.re-card-hearder-wrapper{
    padding-left:  5px;
}
.re-reply-card-body{
    padding-left: 15px;
}
</style>