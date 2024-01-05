<script setup>


import {api} from "@/common.js";
import {ref, watch} from "vue";

const pageContents = ref();
const totalPages = ref();

// api query String 정의
const queryString =  ref({
  page:0,
  order:"등록순",
  genre:"전체"
  }
)
const orderObj = {
  "등록순" : "latest",
  "인기순" : "like",
  "평점순" : "rate"
}
const genreObj = {
 "전체" : "all",
  "판타지" : "fantasy",
  "로맨스" : "romance",
  "학원" : "school",
  "일상" : "daily",
  "코믹" : "comic",
  "무협" : "martialarts"
}
// 페이지네이션 딜레이
const loadContent = ref(false);

function sleep(ms){
  const wakeUpTime = Date.now() + ms;
  while (Date.now() < wakeUpTime) {}
}

// 뒤로가기 위한 세션값 저장 세션 스토리지에 있으면 가져오기
if (sessionStorage.getItem("page")){
  queryString.value.page = sessionStorage.getItem("page");
}
if (sessionStorage.getItem("order")){
  queryString.value.order = sessionStorage.getItem("order");
}
if (sessionStorage.getItem("genre")){
  queryString.value.genre = sessionStorage.getItem("genre");
}

watch(
    // page가 바끼면 데이터 갱신 후 세션 값 갱신
    () => queryString.value.page,
    () => {
      fetchData()
      sessionStorage.setItem("page",queryString.value.page);
    }
)
watch(
    // order가 바끼면 데이터 갱신 후 세션 값 갱신
    () => queryString.value.order,
    (nowOrder, lastOrder) => {
      queryString.value.page = 0
      fetchData()
      sessionStorage.setItem("order",queryString.value.order);
    }
)
watch(
    // genre가 바끼면 데이터 갱신 후 세션 값 갱신
    () => queryString.value.genre,
    (nowGenre, lastGenree) => {
      queryString.value.page = 0
      fetchData()
      sessionStorage.setItem("genre",queryString.value.genre);
    }
)



// 페이지네이션 데이터 갱신 api
const fetchData = async () => {
  loadContent.value = true
  try {
    const response = await api("webtoon?page="
    + (queryString.value.page -1)
    + "&genre=" + genreObj[queryString.value.genre]
    + "&order=" + orderObj[queryString.value.order]
    , "GET");
    pageContents.value = response.content;
    totalPages.value = response.totalPages;
  } catch (error) {
    console.error("Error fetching data:", error);
  }
  sleep(500)
  loadContent.value = false
};
// 버튼 누를 경우 쿼리스트링 변경
const changeGenre = function (a){
  queryString.value.genre = a
}
const changeOrder = function (a){
  queryString.value.order = a
}
// 데이터 초기 갱신
fetchData()

</script>
<template>
  <v-card
      class="v-col-md-12">
    <router-link :to="'/'">
      <v-btn
          style="margin-left: 2%"
          icon="mdi-arrow-left">

      </v-btn>
    </router-link>
    <span
        class="top-header-text"
    >
    웹툰
  </span>
  </v-card>
  <v-container
      class="main-container"
  >
    <v-container
    >
      <v-row
      >
        <v-col
            class="v-col-3"
        >
      <v-select
          v-model="queryString.order"
          label="정렬"
          :items="['등록순', '인기순', '평점순']"
          @update:modelValue="changeOrder"
      >
      </v-select>
        </v-col>
        <v-col
            class="v-col-3"
        >
          <v-select
              v-model="queryString.genre"
              label="장르"
              :items="['전체','판타지', '로맨스', '학원', '일상', '코믹', '무협']"
              @update:modelValue="changeGenre"
          >
          </v-select>
        </v-col>
      </v-row>
        <v-row class="justify-center">
        <v-col v-for=" (item, idx) in pageContents"
               cols="2"
               style="min-height:150px
                      ;min-width:150px"
        >
          <router-link :to="'/webtoon/' + item.masterId"
            class="no-color-line"
          >
            <div>
<!--              이미지 텍스트 위치 맞추기-->
              <v-img
                  :src="item.imgUrl"
                  width="150px"
                  height="150px"
                  alt="https://vuetifyjs.com/en/"
                  cover="true"
              >
              </v-img>
              <div
                  style="font-weight:1000"
                  v-text="(item.title.length > 15)? item.title.substring(0,15) + '...' : item.title">
              </div>
              <div
                style="color:#555765"
              >
                <span>평균</span>
                <v-icon
                    size="15"
                    icon="mdi-star"
                  color=#555765
                ></v-icon>
                <span
                    v-text="item.starAvg ? (item.starAvg/2).toFixed(1) : '0.0'"
                ></span>
              </div>
            </div>
          </router-link>
        </v-col>

      </v-row>
      <v-container>
      <v-row>
      <v-col>
          <v-pagination
              class="v-row v-md-12"
              v-model="queryString.page"
              :length="totalPages-1"
              :disabled="loadContent"
              total-visible="8"
              active-color=#5302FE>
            <!-- totalPages 0부터 시작-->
          </v-pagination>
      </v-col>
      </v-row>
    </v-container>
  </v-container>
  </v-container>
</template>

<style scoped>
@import "@/assets/css/webtoonList.css";

</style>