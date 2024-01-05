<template>
<!--    <form method="post" action="http://localhost:8089/api/join" >
        <div class="formContainer">
            <input type="email" name="usersEmail" width="100px" placeholder="이메일" required>
            <input type="text" name="usersNickname" width="100px" placeholder="닉네임" required>
            <input type="password" name="usersPw" width="100px" placeholder="비밀번호" required>
            <input type="password" name="checkPw" width="100px" placeholder="재확인" required>
            <button class="btn btn-primary" type="submit">가입하기</button>
        </div>
    </form>-->
  <div id="registerContainer">
    <v-card
        class="mx-auto pa-12 pb-8"
        elevation="8"
        width="37%"
        rounded="lg"
    >
      <h1 id="LoginTitle">회원가입</h1>

      <div class="text-subtitle-1 text-medium-emphasis">이메일</div>
      <div class="flex">
        <v-text-field
            :rules="emailRules"
            v-model="vars.usersEmail"
            variant="outlined"
            autofocus="autofocus"
        >
        </v-text-field>
        <v-btn
            class="vaildateBtn basicBtnColor mt-2"
            @click="emailButton"
        >
          인증
        </v-btn>
      </div>
      <div v-show="showLoading" class="text-center">
        <img src="@/assets/images/loading.gif" class="loadingImg" alt=""/>
      </div>

<!--  인증번호 확인   -->
      <div v-show="showToken">
        <div class="text-subtitle-1 text-medium-emphasis">인증번호 확인</div>
        <div class="flex">
          <v-text-field
              :rules="[required]"
              v-model="emailCode"
              variant="outlined"
          >
          </v-text-field>
          <v-btn
              class="vaildateBtn basicBtnColor mt-2"
              @click="emailCodeButton"
          >
            인증
          </v-btn>
        </div>
      </div>

      <div class="text-subtitle-1 text-medium-emphasis" >닉네임</div>
      <div>
        <div class="flex">
          <v-text-field
              class="textFieldh"
              :rules="[required]"
              v-model="vars.usersNickname"
              variant="outlined"
          >
          </v-text-field>
          <v-btn
              class="vaildateBtn basicBtnColor mt-2"
              @click="nicknameButton"
          >
            중복확인
          </v-btn>

        </div>
      </div>

      <span id="nicknameMsg" class=""></span>

      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">
        비밀번호
      </div>
      <v-text-field
          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
          :type="visible ? 'text' : 'password'"
          v-model="vars.usersPw"
          variant="outlined"
          @click:append-inner="visible = !visible"
          :rules="[required]"
      ></v-text-field>

      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">
        재확인
      </div>
      <v-text-field
          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
          :type="visible ? 'text' : 'password'"
          v-model="vars.checkPw"
          variant="outlined"
          @click:append-inner="visible = !visible"
          :rules="[required]"
      ></v-text-field>

      <v-btn
          block
          class="mb-8 basicBtnColor"
          variant="tonal"
          @click="join"
      >
        회원가입
      </v-btn>
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
const joinUrl = import.meta.env.VITE_SERVER_URL + import.meta.env.VITE_JOIN_API_PATH;
const checkEmailUrl = import.meta.env.VITE_SERVER_URL + import.meta.env.VITE_EMAIL_VERIFICATION_API_PATH;
const checkEmailCodeUrl = import.meta.env.VITE_SERVER_URL + import.meta.env.VITE_VERIFY_CODE_API_PATH;
const checkNickNameUrl = import.meta.env.VITE_SERVER_URL + import.meta.env.VITE_CHECK_NICKNAME_API_PATH;

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
      bjCheck: false,
      nickCheck: false,
    }
);

// message DOM
onMounted(() => {
  let emailMsg = document.getElementById("emailMsg");
  let idMsg = document.getElementById("idMsg");
  let nicknameMsg = document.getElementById("nicknameMsg");
  let pwMsg = document.getElementById("pwMsg");
});

const emailRules = [
  value => !!value || 'Required.',
  value => (value || '').length <= 100 || 'Max 100 characters',
  value => {
    const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    return pattern.test(value) || 'Invalid e-mail.'
  },
]

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
    let reg = /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,25}$/;
    if(reg.test(password)){
      return true;
    }else {
      alert("비밀번호는 영문, 숫자, 특수문자 조합으로 이루어진 8~15자로 구성해주세요");
    }

  }
}

async function join(){
  // if (!checkAlg(pwMsg, vars.value.usersPw)){
  if (!(check.value.emailCheck && check.value.nickCheck)) {
    alert("모든 인증을 완료해주세요!");
  } else {
    if (checkPassword(vars.value.usersPw, vars.value.checkPw)){
      console.log(vars.value);
      await axios.post(joinUrl, vars.value)
          .then((res) => {
            alert("환영합니다! 회원가입이 완료되었습니다!")
            router.push('/user/login');
          })
          .catch((err) => {
            alert(err.data);
          });
    }
  }
}

// 이메일 인증 메일 전송
const emailButton = async () => {

  if (!vars.value.usersEmail.includes('@')) {
    emailMsg.innerText = "이메일을 입력하세요";
  } else {
    var data = new FormData();
    data.append("email", vars.value.usersEmail);
    data.append("type", "join");
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

// 인증번호 확인
const emailCodeButton = async () => {

  var data = new FormData();
  data.append("email", vars.value.usersEmail);
  data.append("code", emailCode.value);

  await axios.post(checkEmailCodeUrl, data)
      .then((res) => {
        alert("인증이 완료되었습니다")
        showToken.value = false;
        check.value.emailCheck = true;
      })
      .catch((err) => {
        check.value.emailCheck = false;
        alert(err.response.data);
      });
}


// 닉네임 확인
const nicknameButton = async () => {

  if (!checkAlg(nicknameMsg, vars.value.usersNickname)) {

    let nicknameCheck = checkNickNameUrl + "?nickname=" + vars.value.usersNickname

    await axios.get(nicknameCheck)
        .then((res) => {
          nicknameMsg.innerText = "사용 가능한 닉네임입니다"
          check.value.nickCheck = true;
        })
        .catch((err) => {
          check.value.nickCheck = false;
          nicknameMsg.innerText = err.response.data;
        });
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

    .formContainer{
        width: 60%;
        margin: auto;
        display: flex;
        flex-direction: column;
    }
    #EmailContainer{
      display: flex;


    }
    .vaildateBtn{
      hight: 100%;
      margin-left : 10px;
    }
</style>