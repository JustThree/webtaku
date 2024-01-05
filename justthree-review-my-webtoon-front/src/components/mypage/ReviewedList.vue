<template>
  <div class="text-center">
  <hr class="line">
    <div id="btn_back_line">
      <v-btn class="ma-2" color="#5041BC" @click="goBack">
        <v-icon start icon="mdi-arrow-left"></v-icon>뒤로가기</v-btn>
      <div id="pagetitle">작성 리뷰 목록</div>
    </div>
    <hr class="line">
  </div>

  <v-card id="hder">
    <v-tabs v-model="tab" color="deep-purple-accent-4" align-tabs="center">
      <v-tab :value="1">기본 순</v-tab>
      <v-tab :value="2">좋아요 순</v-tab>
      <v-tab :value="3">댓글 순</v-tab>
    </v-tabs>
  </v-card>

    <section>
      <div id="layout">
          <v-col v-for="review in reviewed.values" id="section">
        <div id="review_uppercontent">
          <v-avatar size="24" id="profileimg">
            <img src="@/assets/images/blackDUK.png" alt="profileimg" style="width: 100%">
          </v-avatar>
          <div id="nickname">{{review.usersNickname}}</div>
        </div>
        <hr>
        <div id="midcontent">
          <div>
        <a id="image_a"><img :src="review.imageUrl" id="webtoonimg"></a>
          </div>
          <div id="midcontent_text">
            <div id="text_title">{{ review.title }}</div>
            <div id="text_aurthor">작가명 :  {{review.sntncWritrNm}}</div>
<!--            <div id="text_aurthor">{{ review.pictrWritrNm }} {{review.sntncWritrNm}}</div>-->
            <div id="text_content">{{review.content}}</div>

          </div>
        </div>
        <hr>
            <div id="botcontetnt">
              <v-btn class="text-red" size="small" color="surface-variant" variant="text" icon="mdi-heart" @click="toggleFavorite"></v-btn>{{review.reviewHeartCount}}
              <v-btn size="small" color="surface-variant" variant="text" icon="mdi-account"></v-btn>{{review.reviewReplyCount}}
            </div>
          </v-col>
      </div>
    </section>
<!--  #####################################################Footers###########################################-->
  <v-layout class="overflow-visible" style="height: 56px;">
    <v-bottom-navigation v-model="value" color="purple" grow>
      <v-btn>
        <v-icon>mdi-history</v-icon>
        Rated
      </v-btn>
      <v-btn>
        <v-icon>mdi-heart</v-icon>
        Liked
      </v-btn>
      <v-btn>
        <v-icon>mdi-map-marker</v-icon>
        Reviewed
      </v-btn>
    </v-bottom-navigation>
  </v-layout>
</template>
<script setup>
import {api} from '@/common.js'
import {ref,defineProps, onBeforeMount, reactive} from "vue";

const props = defineProps(['usersId']);
let usersId=props.usersId;
let reviewed = reactive([]);
const tab = ref(null);
const fav = ref(true);


const toggleFavorite = () => {
  fav.value = !fav.value;
};
onBeforeMount(()=>{
  console.log("start")
  try {
    api(`mypage/reviewed/${usersId}`,"GET",{})
        .then((resp)=>{
          console.log(resp)
          reviewed.values=resp;
        })
    console.log("API Response",reviewed);
  }catch (error){
    console.error("API Error",error);
  }
});


const goBack = () => {
  window.history.back();
};
</script>


<style>
@import "@/assets/css/reviewed.css";
</style>