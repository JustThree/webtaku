<script setup>
import ChatList from '@/components/chat/ChatList.vue';
</script>

<template>
  <body style="text-align: -webkit-center; ">
    <ChatList :page="page" @page-change="handlePageChange"/>
    </body>
</template>


<style scoped>
@font-face {
    font-family: 'Pretendard-Regular';
    src: url('https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Regular.woff') format('woff');
    font-weight: 400;
    font-style: normal;
}

* {
    font-family: 'Pretendard-Regular';
}
</style>