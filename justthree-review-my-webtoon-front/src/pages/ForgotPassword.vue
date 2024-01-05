<script setup>

</script>

<template>
  <div id="registerContainer">
    <v-card
        class="mx-auto pa-12 pb-8"
        elevation="8"
        width="37%"
        rounded="lg"
    >
      <h1 id="LoginTitle">비밀번호 찾기</h1>

      <div class="text-subtitle-1 text-medium-emphasis">이메일</div>
      <div class="flex">
        <v-text-field
            :rules="emailRules"
            v-model="vars.usersEmail"
            variant="outlined"
            autofocus="autofocus"
            placeholder="가입한 이메일을 입력해주세요"

        >
        </v-text-field>
        <v-btn
            class="vaildateBtn basicBtnColor mt-2"
            @click="emailButton"
        >
          인증 받기
        </v-btn>
      </div>
      <div v-show="showLoading" class="text-center">
        <img src="@/assets/images/loading.gif" class="loadingImg" alt=""/>
      </div>

      <!--  인증번호 확인   -->
      <div v-show="showToken">
        <h3>메일이 발송되었습니다.</h3>
      </div>
    </v-card>
  </div>
</template>

<script setup>
import {useAuthStore} from "@/stores/auth.store.js";
import {ref, onMounted} from "vue";
import axios from 'axios';
import router from "@/router/index.js";
const authStore = useAuthStore()

// API URL
const checkEmailUrl = import.meta.env.VITE_SERVER_URL + import.meta.env.VITE_EMAIL_VERIFICATION_API_PATH;

//반응형 변수
const showToken = ref(false);
const showLoading = ref(false);
const emailRequired = ref(false);
const emailCodeRequired = ref(false);
const emailCode = ref();
const vars = ref(
    {
      usersEmail: '',
      usersNickname: '',
      usersPw: '',
      checkPw: '',
    }
);

const check = ref(
    {
      emailCheck: false,
    }
);

// message DOM
onMounted(() => {
  let emailMsg = document.getElementById("emailMsg");
});

const emailRules = [
  value => !!value || 'Required.',
  value => (value || '').length <= 20 || 'Max 20 characters',
  value => {
    const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    return pattern.test(value) || 'Invalid e-mail.'
  },
]
function required (v) {
  return !!v || 'Field is required'
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

function checkPassword(password, confirmPassword){
  if (password != confirmPassword){
    alert("비밀번호가 일치하지 않습니다");
    return false;
  } else {
    return true;
  }
}
// 이메일 인증 메일 전송
const emailButton = async () => {

  if (!vars.value.usersEmail.includes('@')) {
    emailMsg.innerText = "이메일을 입력하세요";
  } else {
    var data = new FormData();
    data.append("email", vars.value.usersEmail);
    data.append("type", "reset-password");
    showLoading.value = true;

    await axios.post(checkEmailUrl, data)
        .then((res) => {
          showLoading.value = false;
          // isDisabled.value = true;
          showToken.value = true;
          emailMsg.innerText = "5분 이내로 입력하세요";
        })
        .catch((err) => {
          alert(err.response.data);
        })
  }

}
</script>
<style scoped>
@import '@/assets/css/login.css';
#registerContainer{
  width : 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}
.vaildateBtn{
  hight: 100%;
  margin-left : 10px;
}


</style>