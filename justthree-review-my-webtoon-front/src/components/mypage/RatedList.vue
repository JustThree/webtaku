<template>
<section>
  <div class="text-center">
    <hr class="line">
    <div id="btn_back_line">
      <v-btn class="ma-2" color="#5041BC" @click="goBack">
        <v-icon start icon="mdi-arrow-left"></v-icon>뒤로가기</v-btn>
      <div id="pagetitle">평가한 웹툰</div>
    </div>
    <hr class="line">
  </div>


  <v-card>
    <v-tabs v-model="tab" color="deep-purple-accent-4" align-tabs="center">
      <v-tab :value="1">기본 순</v-tab>
      <v-tab :value="2">별점 높은 순</v-tab>
      <v-tab :value="3">별점 낮은 순</v-tab>
      <v-tab :value="4">5점</v-tab>
      <v-tab :value="5">별점 순</v-tab>
    </v-tabs>
  </v-card>


  <div id="listsection" class="responsive-list"><!--반응형 하려고뷰티빠이 바꿈 -->
    <v-container>
      <v-row>
        <v-col v-for="(item, index) in rated.values" cols="1" class="overflow-x-hidden">
          <div>
            <img :src="item.imageUrl" id="thumbnail">
          </div>
          <router-link :to="{path:`/`}" id="titletext">{{item.title.length > 10 ? item.title.substring(0,10) + '...' : item.title }}</router-link><!--a태그 아래까지 감싸야 클릭 전체로 되는데 그러면 다른정보들이 안 불러와짐 왜이러노 -->
          <div id="tooninfo">
            {{ item.pictrWritrNm }}<v-icon style="margin-bottom:2px " icon="mdi-star" size="small"></v-icon>{{item.starVal/2}}
          </div>
        </v-col>
      </v-row>
    </v-container>
  </div>
</section>
</template>

<script setup>
import {api} from '@/common.js'
import {defineProps, onBeforeMount, reactive, ref, watch} from "vue";

const props = defineProps(['usersId']);
let usersId=props.usersId;
const tab = ref(1);
let rated = reactive([]);

watch(tab,()=>{
  sortBtn();
})

onBeforeMount( () => {
  sortBtn();
});

//10개씩 나오게
const getSlice = (index) => {
  const start = index * 13;
  const end = start + 13;
  return rated.values.slice(start, end);
};
//뒤로가기 버튼
const goBack = () => {
  window.history.back();
};
//정렬기준
const sortBtn = () => {
  try {
    api(`mypage/rated/${usersId}?sortNum=${tab.value}`, "GET", {})
        .then((resp) => {
          console.log(resp)
          rated.values=resp;
        })
    console.log("API Response:", rated);
  } catch (error) {
    console.error("API Error:", error);
  }
}
</script>



<style scoped>
@import "@/assets/css/ratedlist.css";

@media screen and (max-width: 600px) {
  .responsive-list{
    flex-direction: column;
  }

  #webtoonlist{
    margin-right: 0;
    margin-bottom: 20px; /* Adjust as needed */
  }
}
</style>