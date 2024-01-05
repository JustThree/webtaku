<template>
  <v-container>
      <div class="text-h6 text-md-h5 text-lg-h4 font-weight-black" style="margin: 15px;"> 글 작성하기</div>
      <board-form :board="board" :buttonText="'등록하기'" @submit="createBoard"></board-form>
  </v-container>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import {api} from "@/common.js";
import BoardForm from "@/components/board/boardForm.vue";
import {useRouter} from "vue-router";
import {useAuthStore} from "@/stores/auth.store.js";
import {storeToRefs} from "pinia";

const router = useRouter();
let loginUsersId = ref();
const board = ref({
  title: '',
  content: '',
  boardFiles: [],
  noticeYn: 0,
  users: '',
  //기존 데이터 조회
  boardImgMapList: [],
});

//글 등록 처리[Component(BoardForm) 관련]
const createBoard = async (board) => {
  if(board.title.trim() === '' || board.content.trim() === '') {
    alert("제목과 내용을 입력해주세요");
  }else {
    const formData = new FormData();
    formData.append('title', board.title);
    formData.append('content', board.content);
    for (let i = 0; i < board.boardFiles.length; i++) {
      formData.append('imageFiles', board.boardFiles[i]);
    }
    formData.append("noticeYn", useAuthStore().user.nickname === "관리자" || useAuthStore().user.nickname === "admin" ? 1 : 0);// 0: 자유 1: 공지
    const response = await api("board", "POST", formData);
    if (response instanceof Error) {
      console.log(response.response.data);
    } else {
      if (response) {
          alert("글이 성공적으로 등록되었습니다.");
          router.push("/boardslist/comm");
      } else {
        alert("등록 실패..");
      }
    }
  }
};
onMounted(async =>{
    const authStore = useAuthStore()
    const { user } = storeToRefs(authStore);
    loginUsersId.value = user.value.usersId;
})
</script>

<style>

</style>