<template>
  <div id="LoginContainer">
    <v-card
        class="mx-auto pa-16 pb-8 rounded-5"
        elevation="8"
        max-width="650"
    >
      <h1 id="LoginTitle">웹타쿠 로그인</h1>
      <div class="text-subtitle-1 text-medium-emphasis">이메일</div>

      <v-text-field
          density="compact"
          placeholder="Email address"
          prepend-inner-icon="mdi-email-outline"
          variant="outlined"
          :rules="emailRules"
          v-model="email"
      ></v-text-field>

      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">
        비밀번호
        <a
            class="text-caption text-decoration-none text-blue"
            href="/forgot-password"
            rel="noopener noreferrer"
            target="_blank"
        >
          Forgot login password?</a>
      </div>

      <v-text-field
          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
          :type="visible ? 'text' : 'password'"
          v-model="password"
          density="compact"
          placeholder="Enter your password"
          prepend-inner-icon="mdi-lock-outline"
          variant="outlined"
          @click:append-inner="visible = !visible"
          :rules="[required]"
          @keyup.enter="submit"
      ></v-text-field>

      <v-btn
          block
          class="mb-8 basicBtnColor"
          color="black"
          size="large"
          variant="tonal"
          @click="submit"
      >
        로그인
      </v-btn>
<!--      <v-btn
          block
          class="mb-8 basicBtnColor"
          color="black"
          size="large"
          variant="tonal"
          @click="submit"
      >
        회원가입
      </v-btn>-->
      <div class="registerBtnContainer">
        <v-btn
            class="mb-8 basicBtnColor registerBtn"
            color="black"
            size="large"
            variant="tonal"
            href="/user/register"
        >
          회원가입
        </v-btn>
      </div>
    </v-card>


  </div>

</template>
<script setup>
import {useAuthStore} from "@/stores/auth.store.js";
import {ref} from "vue";

const form = ref()
const email = ref('')
const password = ref('')
const visible = ref(false)
const authStore = useAuthStore()
const submit = () => {
  if(!email.value || !password.value){
    alert("아이디와 비밀번호를 입력해주세요");
  }else {
    authStore.login(email.value, password.value);
  }
}
function required (v) {
  return !!v || 'Field is required'
}

const emailRules = [
  value => !!value || 'Required.',
  value => (value || '').length <= 100 || 'Max 100 characters',
  value => {
    const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    return pattern.test(value) || 'Invalid e-mail.'
  },
]

async function validate () {
  const { valid } = await form.value.validate()

  if (valid) alert('Form is valid')
}
function reset () {
  form.value.reset()
}
function resetValidation () {
  form.value.resetValidation()
}


</script>
<style scoped>
@import '@/assets/css/login.css';

</style>