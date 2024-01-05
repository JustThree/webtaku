<template>
  <div class="text-center">
    <hr class="line">
    <div id="btn_back_line">
      <v-btn class="ma-2" color="#5041BC" @click="goBack">
        <v-icon start icon="mdi-arrow-left"></v-icon>뒤로가기</v-btn>
      <div id="pagetitle">내 정보 수정</div>
    </div>
    <hr class="line">
  </div>

  <div>
    <v-card id="updateUserInfoForm_layout" class="mx-auto pa-12 pb-8" elevation="8" max-width="560" rounded="lg" style="text-align: center">
      <div id="profileImgLayOut">
      <v-avatar size="110" id="profileimg"><img :src=profileImg alt="profileimg" style="width: 100%"></v-avatar>
      </div>
<!--      파일 인풋 -->
      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">프로필 사진 변경</div>
      <v-file-input label="File input" v-model="selectedFile" @change="handleFileChange" icon="none" variant="solo-filled"></v-file-input>

<!--      닉네임 변경 -->
      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">닉네임 변경</div>
    <div style="display: flex" class="form-group">
      <div class="input-group">
        <v-text-field v-model="newNickname" density="compact"
                      placeholder="변경할 닉네임을 입력하세요" prepend-inner-icon="mdi-account-outline"
                      variant="outlined"></v-text-field>
        <div class="input-group-append">
        <v-btn class="vaildateBtn basicBtnColor" @click="checkNickname">중복확인</v-btn>
        </div>
      </div>
    </div>
      <small id="nicknameMsg" class="form-text text-info">{{ nicknameAvailabilityMsg }}</small>



      <v-btn block class="mb-8" color="#5041BC" size="large" variant="elevated" @click="updateUserInfo" >회원정보 수정</v-btn>
    </v-card>
  </div>
</template>



<script setup>
import axios from "axios";
import {onMounted, reactive, ref, watch} from "vue";
import {api} from "@/common.js";
import router from "@/router/index.js";
import {useAuthStore} from "@/stores/auth.store.js";
const { user } = useAuthStore()

const checkNickNameUrl = import.meta.env.VITE_SERVER_URL + import.meta.env.VITE_CHECK_NICKNAME_API_PATH;


const userProfileImageUrl = "@/assets/images/blackDUK.png";
const selectedFile = ref([]);
const newNickname = ref("");
const nicknameAvailabilityMsg = ref("");
const currentUser = ref(user.usersId)
const profileImg = ref(user.profile)

const check = ref(
  {
    nickCheck:false,
  }
)

watch(selectedFile,()=> {
  profileImg.value = URL.createObjectURL(selectedFile.value[0]);
})

const goBack = () => {
  window.history.back();
};
function checkAlg(msg, value){
  if (isEmptyString(value)){
    msg.innerText = "값을 입력해주세요";
    return true;
  } else if (isSpaceCharacter(value)){
    msg.innerText = "공백은 입력할 수 없습니다";
    return true;
  } else {
    return false;
  }
}

function isEmptyString(value){
  if (value.trim() == "")
    return true;
  else
    return false;
}
function isSpaceCharacter(value){
  if (value.includes(' '))
    return true;
  else
    return false;
}


const checkNickname = async () => {
  if (!checkAlg(nicknameMsg, newNickname.value)) {

    await axios.get(`http://localhost:8089/api/check-nickname?nickname=${newNickname.value}`)
        .then((res) => {
          console.log(res)
          nicknameAvailabilityMsg.value = "사용 가능한 닉네임입니다"
          check.value.nickCheck = true;
        })
        .catch((err) => {
          console.log(err)
          nicknameAvailabilityMsg.value = err.response.data;
          check.value.nickCheck = false;
        });
  }
}
const updateUserInfo = () => {
  const formData = new FormData();
  formData.append("file", selectedFile.value[0]);
  formData.append("newNickname", newNickname.value);

  if(newNickname.value != "" && !(check.value.nickCheck)){
    alert("닉네임 중복체크를 완료해주세요")
  }else {
    axios.put("http://localhost:8089/mypage/update", formData,{
      headers:{
        'Content-Type': 'multipart/form-data',
      },
    })
        .then((response) => {
          console.log(response.data);
          router.push(`/mypage/userinfo/${ useAuthStore().user.usersId}`)//////////////////////////////
        })
        .catch((error) => {
          if (error.response) {
            console.error(error.response.data);
            console.error("Status Code:", error.response.status);
          } else if (error.request) {
            console.error("The request was made but no response was received");
          } else {
            console.error("An unexpected error occurred:", error.message);
          }
        });
  }
};

</script>
<style scoped>
@import "@/assets/css/updateuserinfo.css";
</style>