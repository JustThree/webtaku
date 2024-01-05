<template>
  <v-container>
      <div class="text-h6 text-md-h5 text-lg-h4 font-weight-black" style="margin: 15px;"> 글 수정하기</div>
    <board-form :board="board" :buttonText="'수정완료'" @submit="updateBoard" @deleteBoard="deleteBoard" @goBack="cancelUpdate"></board-form>
  </v-container>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import {api} from "@/common.js";
import BoardForm from "@/components/board/boardForm.vue";
import {useRoute, useRouter} from "vue-router";
import {useAuthStore} from "@/stores/auth.store.js";
import {storeToRefs} from "pinia";

const router = useRouter();

//로그인한 유저 확인
let loginUsersId = ref();

const board = ref({
    title: '',
    content: '',
    boardFiles: [],
    noticeYn: 0,
    users: '',
    //기존 데이터 조회
    boardImgMapList: [new Map([[], []])],
    boardId: 0,
});
const route = useRoute();

//데이터 조회
onMounted(async () =>{
    const authStore = useAuthStore()
    const { user } = storeToRefs(authStore);
    loginUsersId.value = user.value.usersId;
    console.log("로그인한 usersId", loginUsersId.value );

  const response = await api("board/"+route.params.boardId, "GET");
  if (response instanceof Error) {
    console.log(response.response.data);
  } else {
    board.value = response;
    if(board.value.boardImgMapList.length<=0){
        board.value.boardFiles = []; //boardFiles는 []로 초기화되어 있으므로, board.boardFiles는 빈 배열. 값을 할당해줘야함
    }
  }
});
//글 수정 처리[Component(BoardForm) 관련]
const updateBoard = async (board) => {
    console.log(board);
    if(board.title.trim() === '' || board.content.trim() === '') {
        alert("제목과 내용을 입력해주세요");
    }else {
        const formData = new FormData();
        formData.append('title', board.title);
        formData.append('content', board.content);
        if(board.boardFiles) {
            for (let i = 0; i < board.boardFiles.length; i++) {
                formData.append('imageFiles', board.boardFiles[i]);
            }
        }
        for (let i = 0; i < board.boardImgMapList.length; i++) {
            formData.append('imageIdList', board.boardImgMapList[i].imgId);
        }
        formData.append("noticeYn", 0);// 0: 자유 1: 공지

        const response = await api("board/"+route.params.boardId, "PUT", formData);
        if (response instanceof Error) {
            console.log(response.response.data);
        } else {
            if (response) {
                alert("글이 성공적으로 수정되었습니다.");
                router.go(-1);
            } else {
                alert("수정 실패..");
            }
        }
    }
};
//글 삭제 처리[Component(BoardForm) 관련]
const deleteBoard = async (board) => {
  console.log(board);
  if(confirm("정말 삭제하시겠습니까?")){
      const response = await api("board/"+board.boardId, "DELETE");
      if (response instanceof Error) {
          console.log(response.response.data);
      } else {
          if (response) {
              alert("글이 삭제되었습니다.");
              router.replace("/boardslist/comm");
               } else {
              alert("삭제 실패..");
          }
      }
  }else{
      return;
  }
}
const cancelUpdate = () =>{
    router.go(-1);
}
</script>

<style>

</style>