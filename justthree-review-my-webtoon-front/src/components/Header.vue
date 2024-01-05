<template>
  <v-card>
    <v-toolbar class="w-75 mx-auto" color="white" height="80">

      <img alt="logo" src="@/assets/images/blackDUK.png" @click="$router.push('/')">

      <v-toolbar-title class="ml-2">
        <v-btn @click="moveWebtoon"> 웹툰 </v-btn>
        <v-btn href="/boardslist/comm"> 커뮤니티 </v-btn>
        <v-btn href="/chatlist"> 채팅 </v-btn>
        <v-btn v-if="user && user.usersRole === 'USER,ADMIN'" href="/admin"> 관리자 </v-btn>
      </v-toolbar-title >

      <v-toolbar-items class="w-50">
        <v-card-text class="mt-1">
          <v-text-field
              v-model="searchText"
              :loading = "loading"
              density="compact"
              variant="outlined"
              label="Search"
              append-inner-icon="mdi-magnify"
              single-line
              hide-details
              @keyup.enter="onClick"
              @click:append-inner="onClick"
          ></v-text-field>
        </v-card-text>
        <v-btn v-if="!user" href="/user/login">
          로그인
        </v-btn>


        <v-btn v-if="user" @click="goToMymage">
          마이페이지
        </v-btn>

        <v-btn v-if="user" @click="authStore.logout()">
          로그아웃
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>
  </v-card>

</template>
<script setup>
import {ref} from "vue";
import {api} from "@/common.js";
import {useAuthStore} from "@/stores/auth.store.js";
import {storeToRefs} from "pinia";
import router from "@/router/index.js";

const authStore = useAuthStore()
const { user } = storeToRefs(authStore);
const loading = ref(false);
const searchText = ref("");
const goToMymage = async () =>{
  await api('api/getUserId','get').then(res => {
    router.push(`/mypage/userinfo/${res}`);
  });
}
const onClick = () => {
  // 검색 중 로딩 표시
  loading.value = true;
  router.push(`/search?searchword=`+searchText.value);
  loading.value = false;
}
function moveWebtoon(){
  sessionStorage.removeItem("page")
  sessionStorage.removeItem("order")
  sessionStorage.removeItem("genre")
  router.push("/webtoon");
}

</script>
<style scoped>
@font-face {
  font-family: 'Pretendard-Regular';
  src: url('https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Regular.woff') format('woff');
  font-weight: 400;
  font-style: normal;
}

*{
  font-family: 'Pretendard-Regular';
}
</style>