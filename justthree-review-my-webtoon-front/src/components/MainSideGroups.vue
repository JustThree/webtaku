<script setup>
const props = defineProps(['title','webtoons','color'])
const title = props.title;

</script>

<template>
  <v-sheet
      class="mx-auto"
      max-width="1285"
      :color="color"
  >
    <h2 class="ml-5 pl-15"
        v-text="title"></h2>
    <v-slide-group
        class="pl-5 slide-fade"
        selected-class="bg-success"
        show-arrows
    >
      <v-slide-group-item
          v-for="(itemWebtoon,indexWebtoon) in webtoons"
          :key="indexWebtoon"
          v-slot="{ isSelected, toggle, selectedClass }"

      >
        <router-link
            class="no-underline"
            :to="'/webtoon/' + itemWebtoon.masterId">
        <v-card
          class="elevation-0 webtoon-card ma-4"
            :class="[selectedClass]"
        >
          <div class="webtoon-thumb">
          <v-img
              class="elevation-0"
              height="100%"
              width="100%"
              style="object-fit: cover;
                      border-radius: 25px;"
              :cover=true
              :src="itemWebtoon.imgUrl"
              @click="toggle"
          >

          </v-img>
          </div>
          <div
              class="webtoon-title"
              v-text="itemWebtoon.title"
          ></div>
          <div
              class="webtoon-writer"
              v-text="itemWebtoon.writer"
          ></div>
          <div class="d-flex fill-height align-center justify-center">
            <v-scale-transition

            >
              <v-icon
                  v-if="isSelected"
                  color="white"
                  size="48"
                  icon="mdi-close-circle-outline"
              ></v-icon>
            </v-scale-transition>
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
                v-text="itemWebtoon.starAvg ? (itemWebtoon.starAvg/2).toFixed(1) : '0.0'"
            ></span>
          </div>
        </v-card>

        </router-link>
      </v-slide-group-item>
    </v-slide-group>
  </v-sheet>
  <v-sheet
      class="mx-auto"
      elevation="8"
      max-width="1300"
  >
  </v-sheet>
</template>
<style scoped>
h2 {
  padding: 4px 0 0 10px;
  font-size: 1.3rem;
}
.no-underline{
  text-decoration: none;
}
.slide-fade-enter-active {
  transition: all 2s ease;
}

.webtoon-card {
  width: 170px;
}
.webtoon-thumb {
  height: 255px;
}
.webtoon-title {
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.webtoon-writer {
  font-size: 0.8em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 960px) {
  .webtoon-card {
    width: 160px;
  }
  .webtoon-thumb {
    height: 240px;
  }
}

@media (max-width: 600px) {
  .webtoon-card.ma-4 {
    width: 100px;
    margin: 4px !important;
  }
  .webtoon-thumb {
    height: 150px;
  }
  h2 {
    font-size: 1.1rem;
    padding: 6px 0 0 6px;
  }
}

@media (max-width: 400px) {
  .webtoon-card.ma-4 {
    width: 90px;
    margin: 3px !important;
  }
  .webtoon-thumb {
    height: 135px;
  }
}
</style>