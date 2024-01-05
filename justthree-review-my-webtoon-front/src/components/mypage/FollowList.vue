<template>
  <div class="text-center">
    <hr class="line">
    <div id="btn_back_line">
      <v-btn class="ma-2" color="#5041BC" @click="goBack">
        <v-icon start icon="mdi-arrow-left"></v-icon>뒤로가기</v-btn>
      <div id="pagetitle">유저이름</div>
    </div>
    <hr class="line">
  </div>

  <v-card>
    <v-tabs v-model="tab" color="deep-purple-accent-4" align-tabs="center">
      <v-tab :value="1"><span>팔로우</span></v-tab>
      <v-tab :value="2"><span>팔로잉</span></v-tab>
    </v-tabs>
  </v-card>

  <v-card max-width="800" class="mx-auto" id="followListLayout">
    <v-container>
        <v-col v-for="item in follow.values" cols="12">
          <v-card color="#F5F3FF" theme="dark">
            <div class="d-flex flex-no-wrap justify-space-between">
              <div>
                <v-card-title v-if="item.followingNickname" class="text-h5">{{ item.followingNickname }}</v-card-title>
                <v-card-title v-else class="text-h5">{{ item.followerNickname }}</v-card-title>
                <v-card-subtitle>{{ item.usersEmail }}</v-card-subtitle>
                <div @click="handleFollowButtonClick(item.usersId)" >
                  <v-btn variant="text"
                         :class="item.following ? 'text-red' : ''"
                         icon="mdi-heart"
                         @click="toggleFav(item)"></v-btn>
                </div>
              </div>
              <v-avatar class="ma-6" size="125" rounded="10">
                <img :src="item.profileUrl" alt="" style="width: 100%">
              </v-avatar>
            </div>
          </v-card>
        </v-col>
    </v-container>
  </v-card>
</template>





<script setup>
import {api} from '@/common.js'
import {defineProps, onBeforeMount, reactive, ref, watch} from "vue";
const props = defineProps(['usersId']);
let usersId=props.usersId;
const tab = ref(1);
let follow = reactive([]);
let resp;

watch(tab,()=>{
  sortBtn();
})

onBeforeMount( () => {
  sortBtn();
});
const sortBtn=()=>{
  try {
    api(`mypage/follow/${usersId}?sortNum=${tab.value}`, "GET", {})
        .then((resp) => {
          console.log(resp)
          follow.values=resp;
        })
    console.log("API Response:", follow);
  } catch (error) {
    console.error("API Error:", error);
  }
}
const toggleFav = (item) => {
  item.following = !item.following;
};
const goBack = () => {
  window.history.back();
};

// /////////////////////////////////////////////////////로그인 한 사람 아이디 = followerId
api(`api/getUserId`, "GET", {
}).then((response) => {
      resp = response;
      console.log(resp);
    }
);

/////////////////////////////////////////////////////////팔로우 버튼////////////////////////
const handleFollowButtonClick = (followingId) => {
  try {
    api(`mypage/follow?followingId=${followingId}&followerId=${resp}`, "POST", {
    }).then(() => {
      console.log("Follow button clicked!");
    });
  } catch (error) {
    console.error("API Error:", error);
  }
};
</script>

<style scoped>
@import "@/assets/css/followList.css";

@media screen and (max-width: 600px) {
  .responsive-list {
    flex-direction: column;
  }
}

</style>