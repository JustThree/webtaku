<script setup>
  import {api} from "@/common.js"
  import {onMounted, ref} from "vue";
  import { useAuthStore } from '@/stores/auth.store';
  import { storeToRefs } from 'pinia';

  const authStore = useAuthStore()
  const { user } = storeToRefs(authStore);

  // onMounted(() => {
  //   getUserInfo();
  // })

  const getUserInfo = async () => {
    await api("api/tui",'GET').then(result => {
      userEmail.value = result.usersEmail;

    })
  }

  const userEmail = ref("");
  const token = JSON.parse(user.value.token);
  const accessToken = ref(token.accessToken);
  console.log(accessToken);
  console.log(token.key);
</script>

<template>
    <div>
        <h1>유저 이메일 : {{userEmail}}</h1>
        <h1>토큰 : {{accessToken}}</h1>
    </div>
  <button @click="getUserInfo">유저정보 얻기</button>
</template>

<style scoped>

</style>