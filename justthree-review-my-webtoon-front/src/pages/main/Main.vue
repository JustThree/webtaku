<script setup>


import MainSideGroups from "@/components/MainSideGroups.vue";
import {api} from "@/common.js";
import {ref} from "vue";


const data = ref([]);
const apiList = ["recent","famous","recentend","fantasy","love"]

// for 문으로 api 전달 후 데이터에 넣음
for (const idx in apiList) {
  api("webtoon/webtoonlist?keyword=" + apiList[idx],
      "GET",
  ).then((response) =>{
        data.value[idx] = response;
      }
  );
}



</script>

<template>
  <div>
    <main-side-groups :title="'최근 연재 시작한 웹툰'" :webtoons="data[0]" :color="'white'"></main-side-groups>
  </div>
  <div>
    <main-side-groups :title="'인기있는'" :webtoons="data[1]" :color="'white'"></main-side-groups>
  </div>
  <div>
    <main-side-groups :title="'최근 완결 웹툰'" :webtoons="data[2]" :color="'white'"></main-side-groups>
  </div>
  <div>
    <main-side-groups :title="'#판타지'" :webtoons="data[3]" :color="'white'"></main-side-groups>
  </div>
  <div>
    <main-side-groups :title="'#로맨스'" :webtoons="data[4]" :color="'white'"></main-side-groups>
  </div>

</template>
<script>

</script>

<style scoped >
div {
  margin: 20px 0 20px 0;
}

/* 스타일 작성 */
</style>
