<template>
  <v-app>
    <Header></Header>
    <router-view />
  </v-app>
</template>



<script setup>
import Header from '@/components/Header.vue';
</script>


<style scoped>
</style>
<style>
@import "assets/css/body.css";
*{
  font-family: Pretendard-Regular
}
</style>