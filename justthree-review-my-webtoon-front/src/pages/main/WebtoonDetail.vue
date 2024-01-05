<script setup>

import {ref} from 'vue'
import {api, apiToken} from "@/common.js";
import {useRoute} from "vue-router";
import {useAuthStore} from "@/stores/auth.store.js";
import router from "@/router/index.js";

const authStore = useAuthStore()
const route = useRoute();
const color = ref('#BEADFA')
const data = ref([]);
const reviewData = ref([]);
const rating = ref(0);
const reviewContent = ref("")
const links = ref({
  platform: [],
  link: []
});
// 화면 기초 정보
if (authStore.user) {
  apiToken("webtoon/" + route.params.masterId,
      "GET",
      {}
      ,
      JSON.parse(authStore.user.token).accessToken
  ).then((response) => {
        if (response.ageCheck) {
          alert("성인 웹툰입니다. 메인으로 이동합니다.")
          router.push("/")
        }
        data.value = response;
        rating.value = response.userStar / 2
    // 한 웹툰에 중복 웹툰인 경우 문자열 파싱 후 배열로 만듬
    if (response.links) {
          if (response.links.indexOf("*") > 0) {
            const linkSplit = response.links.split("*")
            for (const linkSplitIdx in linkSplit) {
              links.value.platform[linkSplitIdx] = linkSplit[linkSplitIdx].split("$")[0]
              links.value.link[linkSplitIdx] = linkSplit[linkSplitIdx].split("$")[1]
            }

          } else {
            if (response.links[0].indexOf("$")) {
              links.value.platform[0] = response.links.split("$")[0]
              links.value.link[0] = response.links.split("$")[1]
            }
          }
        }
      }
  );
} else {
  api("webtoon/" + route.params.masterId,
      "GET",
  ).then((response) => {
        data.value = response;
        rating.value = response.userStar / 2
        if (response.links) {
          if (response.links.indexOf("*") > 0) {
            const linkSplit = response.links.split("*")
            for (const linkSplitIdx in linkSplit) {
              links.value.platform[linkSplitIdx] = linkSplit[linkSplitIdx].split("$")[0]
              links.value.link[linkSplitIdx] = linkSplit[linkSplitIdx].split("$")[1]
            }

          } else {
            if (response.links[0].indexOf("$")) {
              links.value.platform[0] = response.links.split("$")[0]
              links.value.link[0] = response.links.split("$")[1]
            }
          }
        }
      }
  );
}
api("webtoon/reviews/" + route.params.masterId,
    "GET"
).then((response) => {
      reviewData.value = response.content
    }
)

// 리뷰 리스트 가져오기


// 별점 남기기 api
function ratingSend() {
  if (authStore.user) {
    apiToken("webtoon/rating?" +
        "masterId=" + route.params.masterId +
        "&star=" + rating.value * 2,
        "PUT",
        {},
        JSON.parse(authStore.user.token).accessToken
    ).then(() => {
          alert("별점 등록!")
          router.go(0)
        }
    )
  } else {
    alert("로그인을 먼저 해 주세요!")
  }
}

// 관심 등록 api
function interestAdd() {
  if (authStore.user && authStore.user.token !== null) {
    apiToken(
        "webtoon/interest/" +
        route.params.masterId,
        "PUT",
        {},
        JSON.parse(authStore.user.token).accessToken
    ).then(
        (response) => {
          alert(response)
          router.go(0);
        }
    )
  } else {
    alert("로그인을 먼저 해 주세요!")
  }
}

// 리뷰 api => 모달
function submitReview() {
  if (authStore.user) {
    apiToken(
        "webtoon/review/" +
        route.params.masterId,
        "POST",
        {
          content: reviewContent.value
        },
        JSON.parse(authStore.user.token).accessToken
    ).then(
        (response) => {
          // 프론트 유효성 검사
          if (5 <= reviewContent.value.length && reviewContent.value.length <= 200) {
            alert(response)
            router.go(0)
          } else {
            alert("5~ 200자 값을 입력해 주세요")
          }
        })
  } else {
    alert("로그인을 먼저 해 주세요!")
  }
}


</script>
<template>
  <v-card
      class="v-col-md-12"
  >
    <v-btn
        style="margin-left: 2%"
        icon="mdi-arrow-left"
        @click="router.go(-1)"
    >
    </v-btn>
  </v-card>
  <v-container
      class="main-container"
  >

    <v-row align="center" justify="center"
           class="top-row m-lg-2"
    >
      <v-card
          class="mx-auto card-all image-header"
          height="100%"
          width="90%"
          :style="
            {
              borderRadius: '10px',
              backgroundImage: 'linear-gradient(rgba(0, 0, 0, 0.6),rgba(0, 0, 0, 1.0)),' +'url(' + data.imgUrl +')'
              }
            ">
        <v-card-item>
          <div>
            <div class="top-row-title text-h2 mb-1"
                 v-text="data.title"
            >
            </div>
            <div class="top-row-writer text-h4"
                 v-text="data.writer"
            ></div>
          </div>
        </v-card-item>
      </v-card>
    </v-row>
    <v-row align="center" justify="center"
           class="flex mid-row m-lg-2"
    >
      <v-card
          class="flex align-items-center mx-auto"
          width="100%"
          min-height="600px"
          color=#F8F8F8
          style="display:flex">
        <div
            style="width:30%"
        >
          <v-img
              style="border-radius: 25px;"
              :src="data.imgUrl"
              height="300px"
              :cover=true

          >
          </v-img>
        </div>
        <v-card
            width="70%"
            height="100%"
        >
          <v-card
              height="30%"
              style="display: flex"
          >
            <v-rating
                :half-increments=true
                :length="5"
                size="x-large"
                :hover=true
                active-color="red"
                class="rating"
                v-model="rating"
                @click="ratingSend"
            >
            </v-rating>
            <v-card
                class="mid-info-card"
            >
              <v-container
                  class="align-items-center justify-content-center"
              >
                <v-row
                    class="align-items-center justify-content-center">
                  <v-col>
                    <div>
                      <div
                          class="mid-row-rating-num"
                          v-text="(data.avgRating/2).toFixed(1)"
                      >
                      </div>
                    </div>
                  </v-col>
                  <v-col class="flex-column" style="text-align: center">
                    <v-icon
                        size="64"
                        icon="mdi-plus-box"
                        @click="interestAdd"
                        :color="data.isAddInterest==false ? 'gray' : 'red'"
                    ></v-icon>
                  </v-col>
                  <v-col class="flex-column" style="text-align: center">
                    <v-dialog width="1000" height="800px"
                              scrollable
                    >
                      <template v-slot:activator="{ props }">
                        <v-icon
                            v-bind="props"
                            color="gray "
                            size="64"
                            icon="mdi-grease-pencil"
                        ></v-icon>
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
                                @keyup.enter="submitReview"
                            ></v-btn>
                            <v-btn
                                text="Close"
                                @click="isActive.value = false"
                            ></v-btn>
                          </v-card-actions>
                        </v-card>
                      </template>
                    </v-dialog>
                  </v-col>
                  <v-col class="flex-column" style="text-align: center">
                    <router-link :to="'/chat/'+route.params.masterId"
                                 class="no-color-line"
                    >
                      <v-icon
                          color="gray"
                          size="64"
                          icon="mdi-wechat"
                          style="justify-content: center"
                      ></v-icon>
                    </router-link>
                  </v-col>
                </v-row>
                <v-row
                    class="align-items-center justify-content-center">
                  <v-col class="v-col-3" style="text-align: center"><span
                      style="color:red"
                      v-text="data.countStar"></span>명투표
                  </v-col>
                  <v-col class="v-col-3" style="text-align: center">관심등록</v-col>
                  <v-col class="v-col-3" style="text-align: center">리뷰쓰기</v-col>
                  <v-col class="v-col-3 " style="text-align: center">채팅방</v-col>
                </v-row>
              </v-container>


            </v-card>
          </v-card>
          <v-card
              height="75%"
              style="
            min-height: 500px;"
          >
            <div id="outline"
                 class="outline"
                 v-text="data.outline"
            >
            </div>
            <div
                class="flex-row-reverse"
            >
              <div
                  v-for="(linkEle,linkIdx) in links.platform" :key="linkIdx"
              >


                <v-btn
                    style="float: right;
                 margin-right: 10px"
                    color="#5302FE"
                    elevation="4"
                    rounded="sm"
                    :href="links.link[linkIdx]"
                >
              <span
                  style="color: #FFFFFF"
                  v-text="links.platform[linkIdx]"
              >
              </span>
                </v-btn>
              </div>
            </div>
          </v-card>
        </v-card>

      </v-card>
    </v-row>
    <v-row align="center" justify="center"
           style="
           width: 90%;"
           class="m-lg-2"
    >
      <v-card
          class="mx-auto card-all"

          width="100%"
          :color="color"
          style="display: flex; flex-direction: column;"
      >
        <v-card-item
            style="height: 60%;"
        >
          <div class="text-h2 mb-1 flex-column"
               style="margin: 0 0 10px 20px;"
          >
            리뷰
          </div>

        </v-card-item>
        <v-card-item>
          <router-link :to="'/reviewlist/' + route.params.masterId">
            <div class="text-h4 flex-row-reverse" style="margin: 20px;
          float: right"
            >
              더 보기
            </div>
          </router-link>
        </v-card-item>
      </v-card>
    </v-row>
    <v-row align="center" justify="center"
           style="
           width: 90%;"
           class="m-lg-2"
    >
      <v-card
          class="mx-auto card-all"

          width="100%"
          :color="color"
          style="display: flex;
          flex-direction: column;"
      >
        <v-container class="bg-blue-grey-lighten-0"
                     style="background: #FFFFFF"
        >

          <v-row>
            <v-col v-if="reviewData.length===0">
              <v-sheet class="pa-2 ma-6 "
              >
                리뷰가 없습니다. 리뷰를 등록 해 보세요!
              </v-sheet>
            </v-col>
            <v-col v-for="(itemCol,idxCol) in reviewData"
                   cols="4"
                   style="min-width:300px;border-radius: 15px;"
            >

              <v-sheet
              >
                <v-card style="
                   background: #F2F2F2;"
                >
                  <v-container>
                    <v-row class="align-items-center">
                      <v-col class="v-col-12">
                        <v-avatar color="surface-variant"
                        >
                          <v-img
                              :src="itemCol.imgUrl"
                              width="32px"
                              height="42px"
                          >
                          </v-img>
                        </v-avatar>

                        <router-link :to="'/mypage/userinfo/' + itemCol.replyUserId"
                                     class="no-color-line"
                        >
                        <span style="margin-left:15px"
                              v-text="itemCol.userNickName"></span>
                        </router-link>
                        <v-divider></v-divider>
                        <router-link :to="'/review/' +itemCol.reviewId"
                                     class="no-color-line"
                        >
                          <v-col class="v-col-12"
                                 v-text="itemCol.content.length > 100 ?itemCol.content.substring(0,100)+ '...' : itemCol.content"
                                 style="font-size: 14px;
                                     height:150px">
                          </v-col>
                        </router-link>
                      </v-col>

                    </v-row>
                    <v-row
                    >
                      <v-col
                          style="margin: 0 0 0 10px"
                      >
                        <v-icon
                            color="red "
                            icon="mdi-star"
                        ></v-icon>
                        <span
                            class="review-rating"
                            v-text="itemCol.rating ? (itemCol.rating/2).toFixed(1) : '평가 안함'"
                        ></span>
                      </v-col>
                    </v-row>
                    <v-row class="ma-lg-0"
                           style="max-width: 300px"
                    >
                    </v-row>
                    <v-divider></v-divider>
                    <v-row
                        style="max-width: 300px"
                    >
                      <v-col align-self="end"
                             style="margin-left: 5px">
                        <v-icon
                            class="v-col-md-2"
                            style="margin-right: 5px"
                            color="gray "
                            icon="mdi-thumb-up"
                        ></v-icon>
                        <span v-text="itemCol.heartCount"></span>
                        <v-icon
                            class="v-col-md-2"
                            style="margin-right: 5px"
                            color="gray "
                            icon="mdi-wechat"
                        ></v-icon>
                        <span v-text="itemCol.replyCount"></span>
                      </v-col>
                    </v-row>
                    <v-divider></v-divider>
                  </v-container>
                </v-card>
              </v-sheet>

            </v-col>
          </v-row>
        </v-container>

      </v-card>
    </v-row>
  </v-container>

</template>

<script>

</script>

<style scoped>
@import "@/assets/css/webtoonDetail.css";


</style>