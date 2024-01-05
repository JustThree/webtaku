<template>
  <body >
  <v-layout class="mx-8" style="text-align:left">
    <v-icon icon="mdi-account" color="rgb(128,128,128)" @click.stop="drawer = !drawer"></v-icon>
    {{ getParticipants.length }}
  </v-layout>
  <v-divider></v-divider>


  <div id='chatt'>
    <br/>
    <div id="container" style="height: 380px; overflow-y: scroll;">
      <div v-for="item in talk" :key="item.created" class="d-flex mx-4">
        <div v-if="userNickname === item.senderNickname" class="my-2 w-75 v-row justify-end">
          <div class="d-flex mx-2">
            <div class="text-caption d-flex align-end mr-2" style="color:rgb(128,128,128)">
              {{ createdDiff(item.created) }}
            </div>
            <v-card class="d-flex pa-4" min-height="40" max-width="300" color="#FFF8C9">
              <div class="align-self-center text-body-2 text-left"> {{ item.contents }}</div>
            </v-card>
          </div>
        </div>
        <div v-else class="my-2 w-75 v-row justify-start">
          <div>
            <v-avatar variant="outlined" size="48" class="mx-2" @click="$router.push(`/mypage/userinfo/${item.usersId}`)">
              <v-img
                  :src="item.profileUrl"
                  alt="sender"
              ></v-img>
            </v-avatar>
          </div>
          <div class="mx-2">
            <div class="text-subtitle-2 font-weight-bold text-left mb-1"> {{ item.senderNickname }}</div>
            <div class="d-flex">
              <v-card class="d-flex pa-4 " min-height="40" max-width="300" color="#DFCCFB">
                <div class="align-self-center text-body-2 text-left"> {{ item.contents }}</div>
              </v-card>
              <div class="text-caption d-flex align-end ml-2" style="color:rgb(128,128,128)">
                {{ createdDiff(item.created) }}
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
    <v-btn
        :style="newChatBtn"
        color="#BEADFA"
        @click="checkNewMsg"
        class="rounded-pill"
        variant="tonal"
    >
      ⬇️ NEW CHAT!
    </v-btn>

    <v-divider></v-divider>
    <v-banner>
      <v-sheet width="500" class="mx-auto">
        <v-form @submit.prevent>
          <v-text-field
              v-model="contents"
              label="메시지를 입력하세요. "
              variant="outlined"
          >
            <template v-slot:prepend>
              <v-avatar variant="outlined">
                <v-img :src="authStore.profile" alt="userProfile"></v-img>
              </v-avatar>
            </template>
            <template v-slot:append>
              <v-btn type="submit"
                     @click="send"
                     class="mx-2"
                     block="true"
                     variant="tonal"
                     color="gray">전송
              </v-btn>
            </template>
          </v-text-field>
        </v-form>
      </v-sheet>

    </v-banner>
  </div>
    <v-layout>
      <v-navigation-drawer
          v-model="drawer"
          class="pa-2"
          temporary
      >
        <v-list-item v-for="p in getParticipants" :key="p.usersId"
            :prepend-avatar="p.profileUrl"
            :title="p.nickname"
                     @click="$router.push(`/mypage/userinfo/${p.usersId}`)"
            class="ma-2"
        ></v-list-item>


</v-navigation-drawer>
    </v-layout>
  </body>
</template>
<script setup>
import {ref, onUnmounted, defineProps, onMounted, watch} from 'vue';
import {api, createdDiff} from '@/common.js'
import {useAuthStore} from "@/stores/auth.store.js";
import router from "@/router/index.js";

const props = defineProps(['masterId']);
const masterId = props.masterId;

let talk = ref([]);
const contents = ref("");
const getParticipants = ref([]);
const authStore = useAuthStore().user;
const userNickname = ref(authStore.nickname);
const drawer = ref(null);
// const newChatBtn = ref({});
const newChatBtn = ref({
  display: "none"
})
const checkNewMsg = () => {
  container.scrollTop = container.scrollHeight;
  newChatBtn.value = {
    display : "none"
  }
}
let ws;

let container;

onMounted(() => {
  // 기존 DB 채팅 내용 load
  container = document.querySelector("#container");

  api(`chats/list/${masterId}`, "GET", {})
      .then((resp) => {
        console.log(resp)
        talk.value = resp;
      })
      .then(() => {
        container.scrollTop = container.scrollHeight;
      })
  const token = JSON.parse(authStore.token).accessToken;

  // Connect WebSocket
  ws = new WebSocket(`ws://${window.location.hostname}:8089/chat?${token}&${masterId}`/*, [token, masterId]*/);
  // sender -> sessionStorage(token) 변경 예정
  ws.onmessage = function (onmessage) {
    console.log(onmessage)
    let data = JSON.parse(onmessage.data);
    if (data.currentParticipants === undefined) {
      talk.value = [...talk.value, data];
      if (container.scrollTop >= container.scrollHeight - container.clientHeight) {
        setTimeout(() => {
          const scrollInterval = setInterval(() => {
            container.scrollTop++;
            if (container.scrollTop >= container.scrollHeight - container.clientHeight) {
              clearInterval(scrollInterval);
            }
          }, 1)
        }, 100)
      } else {
        newChatBtn.value = {
          display: "inline"
        }
      }
    } else {
      console.log(data)
      getParticipants.value = data.currentParticipants;
    }
  }

})


const send = () => {

  if (contents.value.trim() != '') {
    ws.send(contents.value);
    contents.value = '';
    checkNewMsg();
  }
}

onUnmounted(() => {
  if (ws) {
    ws.close();
  }
});

</script>