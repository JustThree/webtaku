<template>
  <v-card>
    <v-toolbar class="header-toolbar mx-auto" height="80">

      <v-app-bar-nav-icon
          class="d-md-none"
          @click="drawer = !drawer"
      ></v-app-bar-nav-icon>

      <img alt="logo" src="@/assets/images/blackDUK-removebg-preview.png" class="header-logo" @click="$router.push('/')">

      <v-toolbar-title class="ml-2 d-none d-md-block">
        <v-btn @click="moveWebtoon"> 웹툰 </v-btn>
        <v-btn href="/boardslist/comm"> 커뮤니티 </v-btn>
        <v-btn href="/chatlist"> 채팅 </v-btn>
        <v-btn v-if="user && user.usersRole === 'USER,ADMIN'" href="/admin"> 관리자 </v-btn>
      </v-toolbar-title >

      <v-spacer class="d-md-none"></v-spacer>

      <v-toolbar-items class="header-actions">
        <div class="header-search d-flex align-center">
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
        </div>
        <v-btn icon variant="text" @click="toggleTheme" :title="isDark ? '라이트 모드' : '다크 모드'">
          <v-icon>{{ isDark ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
        </v-btn>
        <v-btn class="d-none d-md-flex" v-if="!user" href="/user/login">
          로그인
        </v-btn>


        <v-btn class="d-none d-md-flex" v-if="user" @click="goToMymage">
          마이페이지
        </v-btn>

        <v-btn class="d-none d-md-flex" v-if="user" @click="authStore.logout()">
          로그아웃
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>
  </v-card>

  <v-navigation-drawer v-model="drawer" temporary>
    <v-list nav>
      <v-list-item prepend-icon="mdi-book-open-variant" title="웹툰" @click="navigate(moveWebtoon)"></v-list-item>
      <v-list-item prepend-icon="mdi-forum" title="커뮤니티" @click="navigate(() => $router.push('/boardslist/comm'))"></v-list-item>
      <v-list-item prepend-icon="mdi-chat" title="채팅" @click="navigate(() => $router.push('/chatlist'))"></v-list-item>
      <v-list-item
          v-if="user && user.usersRole === 'USER,ADMIN'"
          prepend-icon="mdi-shield-account"
          title="관리자"
          @click="navigate(() => $router.push('/admin'))"
      ></v-list-item>
      <v-divider class="my-2"></v-divider>
      <v-list-item v-if="!user" prepend-icon="mdi-login" title="로그인" @click="navigate(() => $router.push('/user/login'))"></v-list-item>
      <v-list-item v-if="user" prepend-icon="mdi-account" title="마이페이지" @click="navigate(goToMymage)"></v-list-item>
      <v-list-item v-if="user" prepend-icon="mdi-logout" title="로그아웃" @click="navigate(() => authStore.logout())"></v-list-item>
    </v-list>
  </v-navigation-drawer>

</template>
<script setup>
import {computed, ref} from "vue";
import {api} from "@/common.js";
import {useAuthStore} from "@/stores/auth.store.js";
import {storeToRefs} from "pinia";
import router from "@/router/index.js";
import {useTheme} from "vuetify";

const authStore = useAuthStore()
const { user } = storeToRefs(authStore);
const loading = ref(false);
const searchText = ref("");
const drawer = ref(false);

const theme = useTheme();
const isDark = computed(() => theme.global.current.value.dark);
const toggleTheme = () => {
  const next = isDark.value ? 'light' : 'dark';
  theme.global.name.value = next;
  localStorage.setItem('theme', next);
};
const goToMymage = async () =>{
  await api('getUserId','get').then(res => {
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
function navigate(fn) {
  drawer.value = false;
  fn();
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

.header-toolbar {
  width: 75%;
}
.header-logo {
  cursor: pointer;
  height: 48px;
}
.header-actions {
  width: 50%;
}
.header-search {
  flex: 1;
  padding: 0 16px;
}

@media (max-width: 960px) {
  .header-toolbar {
    width: 100%;
  }
  .header-actions {
    width: auto;
    flex: 1;
  }
  .header-logo {
    height: 36px;
  }
  .header-search {
    padding: 0 8px;
  }
}
</style>
