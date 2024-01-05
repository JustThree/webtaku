<script setup>
import { ref, defineEmits } from 'vue';
import {api} from "@/common.js";
const props = defineProps({
  board : {
    type: Object
  },
  buttonText: {
    type:String,
    required: true
  },
})
// [이미지 수정] 삭제 기능 추가
const removeImage = (imgId) => {
    console.log("imgId", imgId);
    const idx = props.board.boardImgMapList.findIndex(imgMap => imgMap.imgId === imgId); // imgId를 사용하여 인덱스 찾기
    if (idx !== -1) {
        props.board.boardImgMapList.splice(idx, 1); // 해당 인덱스의 이미지 데이터를 배열에서 제거
    }
    console.log(props.board.boardImgMapList);
};
const emit = defineEmits(['submit']);
const submitBoard = () => {
  //emit('submit', board.value); //등록
  emit('submit', props.board);
};
//삭제(수정일 때만)
const deleteBoard = () => {
  emit('deleteBoard', props.board); // delete 이벤트 emit
}
const goBack = () => {
    emit('goBack');
}
</script>
<template>
    <v-container>
        <!--  글 제목 -->
        <v-row class="title-frame">
            <v-col cols="12">
                <div class="title-input-frame">
                    <v-text-field
                        counter
                        class="font-weight-bold"
                        variant="standard"
                        v-model="board.title"
                        maxlength="30"
                        bg-color="#EDE7F6"
                        placeholder="글 제목을 입력해주세요(30자 이내)">
                    </v-text-field>
                </div>
            </v-col>
        </v-row>
        <!--  글 내용 -->
        <v-row class="content-frame">
            <v-col cols="12">
                <div class="content-input-frame">
                    <v-textarea
                        counter
                        clearable
                        no-resize
                        variant="outlined"
                        clear-icon="mdi-close-circle"
                        bg-color="white"
                        v-model="board.content"
                        placeholder="작성 규칙 &#13;&#10; - 존댓말(높임말) 사용 &#13;&#10; - 광고 및 홍보성 게시글은 사전고지 없이 삭제 처리되며 내용에 따라 강퇴 조치">
                    </v-textarea>
                </div>
            </v-col>
            <!--  첨부파일 -->
            <v-file-input
                clearable
                multiple
                v-model="board.boardFiles"
                label="이미지 파일 첨부">
            </v-file-input>
        </v-row>
        <!-- [게시글 수정 시]이미지 첨부파일   -->
        <div v-if="board.boardImgMapList.length>0" class="d-flex justify-space-around align-center bg-white">
            <div class="ma-4" v-for="(imgMap, index) in board.boardImgMapList" :key="index" >
                <div class="image-container">
                    <v-img class="bg-white" width="300" :src="imgMap.accessUrl" cover></v-img>
                    <v-btn
                        variant="text"
                        style="position: absolute; z-index: 1; top: 5px; right: 5px;color: red;font-weight:900;"
                        @click="removeImage(imgMap.imgId)">X</v-btn>
                </div>
                <div class="text-subtitle-2">{{imgMap.originName}}</div>
            </div>
        </div>
        <v-row>
            <v-col cols="12">
                <div class="frame-bottom">
                    <v-btn variant="outlined" @click="submitBoard" >{{buttonText}}</v-btn>
                    <v-btn variant="tonal" v-if="buttonText==='수정완료'" @click="deleteBoard" >글 삭제</v-btn>
                    <v-btn variant="text" v-if="buttonText==='수정완료'" @click="goBack">수정 취소</v-btn>
                </div>
            </v-col>
        </v-row>
    </v-container>
</template>
<style>
/* 등록 & 수정 */
.v-textarea .v-field--no-label textarea, .v-textarea .v-field--active textarea {
    opacity: 1;
    height: 400px;
}
.frame-bottom{
    display: flex;
    justify-content: center;
    gap: 30px;
}
/*  수정일 때  */
.image-container {
    position: relative; /* 부모 요소에 대해 상대적인 위치 설정 */
}
</style>