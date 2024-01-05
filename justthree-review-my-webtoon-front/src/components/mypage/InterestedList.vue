<template>
  <section>
    <div class="text-center">
      <hr class="line">
      <div id="btn_back_line">
        <v-btn class="ma-2" color="#5041BC" @click="goBack">
          <v-icon start icon="mdi-arrow-left"></v-icon>뒤로가기</v-btn>
        <div id="pagetitle">관심 웹툰</div>
      </div>
      <hr class="line">
    </div>

    <v-card>
      <v-tabs v-model="tab" color="deep-purple-accent-4" align-tabs="center" >
        <v-tab :value="1">기본 순</v-tab>
      </v-tabs>
    </v-card>

    <div id="listsection" class="responsive-list">
      <ul v-for="(item,index) in interested.values" :key="index" id="webtoonlist" >
        <li v-for="item in getSlice(index)" :key="item.mastrId" id="ratedtoon">
          <div>
            <img :src="item.imageUrl" id="thumbnail">
          </div>
          <router-link :to="{path:`/`}" id="titletext" >{{item.title}}</router-link><!--a태그 아래까지 감싸야 클릭 전체로 되는데 그러면 다른정보들이 안 불러와짐 왜이러노 -->
          <div id="tooninfo">
             {{item.pictrWritrNm}} {{item.sntncWritrNm}}
          </div>
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup>
import {api} from '@/common.js'
import {defineProps, onBeforeMount, reactive, ref} from "vue";

const props = defineProps(['usersId']);
let usersId=props.usersId;

let interested = reactive([]);
const tab = ref(null);

onBeforeMount( () => {
  console.log("start")
  try {
    api(`mypage/interested/${usersId}`, "GET", {})
        .then((resp) => {
          console.log(resp)
          interested.values=resp;
        })
    console.log("API Response:", interested);
  } catch (error) {
    console.error("API Error:", error);
  }
});

//13개씩 나오게
const getSlice = (index) => {
  const start = index * 13;
  const end = start + 13;
  return interested.values.slice(start, end);
};
const goBack = () => {
  window.history.back();
};
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