<template>
  <div id="bodywapper" class="d-flex">
    <aside class="aside1">
      <div id="adminPic"><img src="@/assets/images/blackDUK-removebg-preview.png" alt=""></div>
      <h1>관리자</h1>
        <v-list
            density="compact"
            nav
        >
          <router-link to="/admin"><v-list-item prepend-icon="mdi-account-group" title="회원관리" value="User"></v-list-item></router-link>
          <router-link to="/noticeAdmin"><v-list-item prepend-icon="mdi-cube" title="공지글 관리" value="Notice"></v-list-item></router-link>
          <router-link to="/boardAdmin"><v-list-item prepend-icon="mdi-widgets" title="게시글 관리" value="Board"></v-list-item></router-link>
          <router-link to="/static"><v-list-item prepend-icon="mdi-chart-pie" title="통계" value="static"></v-list-item></router-link>

        </v-list>
    </aside>
    <section class="section1">
      <router-view></router-view>
    </section>
  </div>

</template>

<script setup>
  import { mdiAccount, mdiDelete, mdiPencil, mdiShareVariant } from '@mdi/js'
</script>
<style scoped>
@import "@/assets/css/mypage.css";
#bodywapper{
  width:100%;
}
.aside1 {
  float: left;
  width: 350px;
  height: 100vh;
  margin-left:10px;
  margin-right:10px;
  margin-top: 30px;
  text-align: center;
  display : flex;
  flex-direction : column;
  border-radius : 30px;
  padding-top: 30px;
  box-shadow: 0px 0px 13px 1px #888888;
}

.text2 {
  position: relative;

}

.section1 {
  width:100%;
  height: 100vh;
  text-align: center;
  margin-top: 30px;
  margin-left: 10px;
  margin-right: 10px;
  padding : 30px;
  border-radius: 30px;
  box-shadow: 0px 0px 13px 1px #888888;
}

.text3 {
  position: relative;

}
a{
  text-decoration:none;
  color: inherit;
}

@media (max-width: 960px) {
  #bodywapper {
    flex-direction: column;
  }
  .aside1 {
    float: none;
    width: auto;
    height: auto;
    margin: 12px;
    padding-top: 16px;
    border-radius: 16px;
  }
  .aside1 :deep(h1) {
    font-size: 1.4rem;
    margin: 8px 0;
  }
  .aside1 :deep(.v-list) {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
  }
  .aside1 :deep(.v-list-item) {
    flex: 0 0 auto;
  }
  .section1 {
    width: auto;
    height: auto;
    min-height: 50vh;
    margin: 12px;
    padding: 16px;
    border-radius: 16px;
  }
}
</style>