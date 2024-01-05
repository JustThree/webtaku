<script setup>
import {api} from '@/common.js'
import {ref, watch} from "vue";
import {useRoute} from "vue-router";

const route = useRoute();
let type = 1;
let content = ref([[],[],[],[],[]]);
let pages = ref([0,0,0,0,0]);
let size = 24;

// 검색 방식
const typeJson = {
  1:"title",
  2:"outline",
  3:"writer",
  4:"user"
}

// 버튼을 누를 경우 타입 변경
const changeType = function (newType) {
  pages = ref([0,0,0,0,0]);
  type = newType;
}

// 데이터 갱신
const fetchData = async (idx) => {
  try {
    const response = await api(`webtoon/search?type=${typeJson[idx]}&word=${route.query.searchword}&page=${pages.value[idx]}&size=${size}`, "GET");
    if (!response.size){
      return response.size;
    }
    if (response.totalPages < pages.value[idx])
    {
      return "end"
    }
    if (response instanceof Error) {
      // 에러 처리
    } else {
      for (const contentElement of response.content) {
        content.value[idx].push({ ...contentElement, idx });
      }
      return response.content.size
    }
  } catch (error) {
    console.error(error);
  }
};
// 갱신
const load = (
    { done }
) => {
  setTimeout(async () => {
      //페이지배열의 값 1증가
      pages.value[type]++;
      // 데이터 받기
      const returnVal = await fetchData(type);
      // 값이 비어있거나 끝났으면 끝...
      if (!returnVal || returnVal==="end"){
        done("empty")
      } else {
        done('ok')
      }
    }, 2000)
}

//검색어가 변경시 수행
watch(
    () =>route.query.searchword,
    () => {
      // 초기화 후 데이터를 다시 받음
      content = ref([[],[],[],[],[]]);
      pages = ref([0,0,0,0,0]);
      // type = 1;
      size = 24
      fetchData(1)
      fetchData(2)
      fetchData(3)
      fetchData(4)
    }
)
// 데이터 초기화
fetchData(1);
fetchData(2);
fetchData(3);
fetchData(4);
type = 1
</script>

<template>
  <v-infinite-scroll height=100% :items="content[type]"
                     :onLoad="load"
                     empty-text="검색하신 결과가 더 없어요"
                      aria-hidden="true">
  <div>
  <v-card
      class="main-card">
    <v-tabs
        v-model="tab"
        color="deep-purple-accent-4"
        align-tabs="center"
    >
      <v-tab :value="1"
      @click="changeType(1)"
      >제목</v-tab>
      <v-tab :value="2"
      @click="changeType(2)"
      >내용</v-tab>
      <v-tab :value="3"
     @click="changeType(3)"
      >작가</v-tab>
      <v-tab :value="4"
             @click="changeType(4)"
      >유저</v-tab>
    </v-tabs>
    <v-window v-model="tab"
    >
      <v-window-item
          v-for="n in 4"
          :key="n"
          :value="n"
      >
        <v-container >
          <v-row>
            <v-col
                v-for="(item,idx) in content[n]"
                :key="idx"
                cols="12"
                md="4"
            ><router-link :to="type===4 ? 'mypage/userinfo/' + item.masterId : 'webtoon/' + item.masterId"
                          class="no-color-line"
            >
              <v-img
                  :src="item.imgUrl"
                  aspect-ratio="1"
                  cover="true"
              ></v-img>
              <div
              v-text="item.title + '/' + item.writer">
              </div>
            </router-link>
            </v-col>
          </v-row>
        </v-container>
      </v-window-item>
    </v-window>
  </v-card>
  </div>

</v-infinite-scroll>
</template>

<style scoped>
@import "@/assets/css/search.css";


</style>
<script>
export default {
  data: () => ({
    tab: null,
  }),
}

</script>