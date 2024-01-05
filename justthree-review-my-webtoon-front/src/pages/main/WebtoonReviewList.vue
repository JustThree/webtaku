<script setup>


import {useRoute} from "vue-router";
import {api, apiToken} from "@/common.js";
import {handleError, ref, watch} from "vue";
import router from "@/router/index.js";
import {useAuthStore} from "@/stores/auth.store.js";

const authStore = useAuthStore()
const route = useRoute();
// 페이징 관련
const pageContents = ref();
const totalPages = ref(1);
const reviewContent = ref("");
const totalCount = ref();
const queryString = ref({
      page: 0
    }
)
// 페이지네이션
const fetchData = async () => {
  try {
    const response = await api("webtoon/reviews/"
        + route.params.masterId
        + "?page=" + (queryString.value.page - 1)
        , "GET");
    pageContents.value = response.content;
    totalPages.value = response.totalPages;
    totalCount.value = response.totalElements;

  } catch (error) {
    console.error("Error fetching data:", error);
  }
};
// 댓글 페이징을 위한
watch(
    () => queryString.value.page,
    () => {
      fetchData()
    }
)
// 리뷰 제출
function submitReview() {
  if (authStore.user) {
    apiToken(
        "webtoon/review/" + route.params.masterId,
        "POST",
        {
          "content": reviewContent.value
        },
        JSON.parse(authStore.user.token).accessToken
    ).then(
        (response) => {
          if (5 <= reviewContent.value.length && reviewContent.value.length <= 200) {

            alert(response);
            router.go(0);
          } else {
            alert("5 ~ 200자 이내로 작성해 주세요")
          }
        })
  } else {
    alert("로그인을 먼저 해 주세요!");
  }
}
// 데이터 초기화
fetchData()
</script>

<template>
  <v-card
      class="v-col-md-12"
      style="
      position: relative;
      height: 100px;"
  >
    <router-link :to="'/webtoon/'+ route.params.masterId">
      <v-btn
          class="back-move-btn"
          icon="mdi-arrow-left">

      </v-btn>
    </router-link>
    <span
        class="top-header-text"
    >
    리뷰
  </span>
    <v-dialog width="1000" height="800px"
              transition="false"
    >
      <template v-slot:activator="{ props }">
        <v-btn
            class="review-btn"
            color="#5302FE"
            v-bind="props"
            v-text="'리뷰 쓰기'"
        ></v-btn>
      </template>

      <template v-slot:default="{ isActive }">
        <v-card title="웹툰 리뷰를 써 주세요!">
          <v-divider></v-divider>

          <v-textarea
              v-model="reviewContent"
              class="p-5"
              bg-color=#F2F2F2
              placeholder="(글자수 5~200자)"
          >
          </v-textarea>
          <v-divider></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>

            <v-btn
                text="Write"
                @click="submitReview"
            ></v-btn>
            <v-btn
                text="Close"
                @click="isActive.value = false"
            ></v-btn>
          </v-card-actions>
        </v-card>
      </template>
    </v-dialog>
  </v-card>

  <v-container
      class="align-center"
  >
    <v-row>
      <v-col
          class="offset-8 v-col-4 "
      >
      </v-col>
    </v-row>
    <v-row v-if="totalCount === 0">
      리뷰가 없어요!
    </v-row>
    <div
        v-text=""
    ></div>
    <v-row v-for="(item,idx) in pageContents"
           class="justify-center"
    >
      <v-card
          class="ma-10 justify-center"
          style="background: #F7F2FA"
          width="40%"
          min-height="200px"
      >
          <v-row
              class="align-center"
          >

            <v-col
                class="v-col-5 ml-2 mt-2"
            >

              <img
                  style="width:40px;height:40px; border-radius: 15px;"
                  :src="item.imgUrl">
              <router-link :to="'/mypage/userinfo/' + item.replyUserId"
                           class="no-color-line">
                <span
                    class="ml-4 font-weight-bold"
                    style="color:black"
                    v-text="item.userNickName">
                </span>
              </router-link>
            </v-col>

          </v-row>
          <v-divider></v-divider>
          <v-row>
            <v-col
                class="m-2"
            >
              <router-link :to="'/review/'+item.reviewId"
                           class="no-color-line"
              >
              <div v-text="item.content"
                   class="ml-2"
                   style="min-height:150px">

              </div>
              </router-link>
            </v-col>
          </v-row>
          <v-divider></v-divider>
          <v-row
              class="ml-2 mb-2"
          >
            <v-col
                class="v-col-2"
            >
              <v-icon
                  color="gray "
                  size="12"
                  icon="mdi-thumb-up"
              ></v-icon>
              <span
                  class="ml-2"
              v-text="item.heartCount">
              </span>
            </v-col>
            <v-col
                class="v-col-2"
            >
              <v-icon
                  color="gray "
                  size="12"
                  icon="mdi-chat-outline"
              ></v-icon>
              <span
                  class="ml-2"
                  v-text="item.replyCount"
              >
            </span>
            </v-col>
          </v-row>

      </v-card>
    </v-row>
    <v-row>
      <v-pagination
          v-if="totalPages-1"
          class="v-row v-md-12"
          v-model="queryString.page"
          :length="(totalPages-1)"
          total-visible="8"
          active-color=#5302FE
      >
        <!-- totalPages 0부터 시작-->
      </v-pagination>
    </v-row>
  </v-container>
</template>

<style scoped>
@import "@/assets/css/webtoonReviewList.css";
</style>