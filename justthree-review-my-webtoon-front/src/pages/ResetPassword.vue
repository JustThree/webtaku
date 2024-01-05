<template>
  <div id="registerContainer">
    <v-card
        class="mx-auto pa-12 pb-8"
        elevation="8"
        width="37%"
        rounded="lg"
    >
      <h1 id="LoginTitle">비밀번호 변경</h1>

      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">
        비밀번호
      </div>
      <v-text-field
          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
          :type="visible ? 'text' : 'password'"
          v-model="usersPw"
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
          v-model="checkPw"
          variant="outlined"
          @click:append-inner="visible = !visible"
          :rules="[required]"
      ></v-text-field>

      <v-btn
          block
          class="mb-8 basicBtnColor"
          variant="tonal"
          @click="changePassword"
      >
        변경하기
      </v-btn>
    </v-card>
  </div>
</template>

<script setup>
import { ref, onBeforeMount } from 'vue'
import { useRoute } from 'vue-router';
import axios from 'axios';

const route = useRoute();

onBeforeMount(() => {
  axios.get('http://192.168.3.112:8089/api/verify-code?email=' + route.query.email + '&code=' + route.query.code)
      .catch((err) => {
        alert(err.response.data);
        location.href="/";
      })
});

const usersPw = ref('');
const checkPw = ref('');

function changePassword() {
  if (usersPw.value === checkPw.value) {
    var data = new FormData();
    data.append("email", route.query.email);
    data.append("password", usersPw.value);
    data.append("correctPassword", usersPw.value);

    axios.put('http://192.168.3.112:8089/api/reset-password', data, {headers:
          {'secretCode': import.meta.env.VITE_SECRET_CODE}})
        .then((res) => {
          alert(res.data);
          location.href="/";
        })
        .catch((err) => {
          alert(err.response.data);
        })
  } else {
    alert("비밀번호가 일치하지 않습니다");
  }
}
function required (v) {
  return !!v || 'Field is required'
}

</script>

<style scoped>
@import '@/assets/css/login.css';

.vaildateBtn{
  hight: 100%;
  margin-left : 10px;
}


</style>