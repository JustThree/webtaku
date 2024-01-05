<template>
  <div class="pa-5" style="background-color:#F5F5F5">
    <v-card class="w-50 my-13 pa-5" rounded="xl" height="600px">

      <v-tabs bg-color="" show-arrows slider-color="purple-lighten-4" align-tabs="center" v-model="page">
        <v-tab :value="1">
          전체
        </v-tab>
        <v-tab :value="2">
          참가자 있는 채팅 👤
        </v-tab>
        <v-tab :value="3">
          인기웹툰 🔥
        </v-tab>
        <v-tab :value="4">
          내가 참여한 채팅 📪
        </v-tab>
      </v-tabs>

      <br>
      <v-autocomplete class="mx-16 my-2" variant="outlined" label="웹툰 제목을 입력해주세요 (2자 이상)"
                    prepend-inner-icon="mdi-magnify" hide-details v-model="searchWord"
                      :items="searchItems"></v-autocomplete>

      <v-infinite-scroll :height="400" :chats="chats" :onLoad="load" v-if="chats.length>0">
        <template v-for="chat in chats" :key="chat.masterId">
          <div class="mx-16">
            <hr>
            <v-row class="pa-6 gc-14">
              <!--      img, title, lastMsg(sender, content), lastMsgTime, ChatURL -->
              <v-avatar calss="v-col" size="80" variant="outlined">
                <img :src="chat.imageUrl" alt="sender" />
              </v-avatar>
              <v-col class="w-50 mt-auto mb-auto">
                <v-row justify="space-between">
                  <div class="text-h6 font-weight-bold mb-1"> {{ chat.title }} </div>
                  <div class="text-medium-emphasis"> {{ createdDiff(chat.created)  }}</div>
                </v-row>
                <v-row class="text-medium-emphasis" justify="start"> {{ chat.usersNickname + ": " + chat.contents }} </v-row>
              </v-col>
              <v-btn v-if="token" :href="'/chat/' + chat.masterId" calss="v-col" variant="tonal" color="#924AFE" style="align-self: center;">
                참여하기
              </v-btn>
            </v-row>
          </div>
        </template>
        <template
               v-slot:empty>
          <v-alert
              class="mx-16"
              width="10"
              color="purple"
              variant="outlined"
              closable="true"
          >No more chat!</v-alert>
        </template>
      </v-infinite-scroll>

      <div v-else>
        <v-alert class="ma-16" type="warning">
          {{ alertMsg}}
        </v-alert>
      </div>
    </v-card>

  </div>
</template>

<script setup>
import { onBeforeMount, onUnmounted, ref, watch} from 'vue';
import { api, createdDiff } from '@/common.js';
import {useAuthStore} from "@/stores/auth.store.js";

const page = ref(1);
const chats = ref([]);
const alertMsg = ref("")
let token = null;

if(useAuthStore().user != undefined){
  token = JSON.parse(useAuthStore().user.token).accessToken;
}
const ws = new WebSocket(`ws://${window.location.hostname}:8089/chat?${token}`);
ws.onmessage = () => {
  loadChats();
}
const load = async ({ done }) => {
  done('empty')
}

watch(page, () => {
    loadChats();
})

const searchWord = ref("");
const searchItems = ref([]);
watch(searchWord, () => {
  console.log(searchWord)
  if(searchWord.value.length >= 2){
    api(`api/webtoon/search?type=1&word=${searchWord.value}&size=5`, "GET")
        .then(resp => {
          searchItems.value = resp;
        })
  }
})
onUnmounted(() => {
  if (ws) {
    ws.close();
  }
});

const loadChats = ()=> {
  chats.value = [];

  if(page.value===4 && token ===null){
    alertMsg.value ="로그인 후 이용가능한 서비스입니다. ";
  }else{
    alertMsg.value = "잠시만 기다려 주세요...";

    api(`chats/type/${page.value}`, "GET", {})
        .then((resp) => {
          // 응답이 늦을 시 page == 4에서 실행됨
          console.log(resp)
          if(page.value !== 4 || token !==null){
            alertMsg.value = "채팅이 존재하지 않습니다. "
            chats.value = resp;
          }
        })
  }
}
</script>

<style scoped>
@font-face {
  font-family: 'Pretendard-Regular';
    src: url('https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Regular.woff') format('woff');
    font-weight: 400;
  font-style: normal;
}

* {
  font-family: 'Pretendard-Regular';
}
</style>