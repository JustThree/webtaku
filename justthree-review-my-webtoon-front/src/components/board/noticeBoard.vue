<template>
<div class="d-flex align-center flex-column">

</div>
</template>
<script setup>
import {toRef, toRefs, watchEffect} from "vue";

const props = defineProps({
  noticeone: Object
});

const {boardId, title, userNickname, created, noticeYn, updated, viewCount} = toRefs(props.noticeone);

const pprops = toRef(props, "noticeone");

watchEffect(()=>{
  boardId.value = pprops.value.boardId;
  title.value = pprops.value.title;
  created.value = pprops.value.created;
  updated.value = pprops.value.updated;
  viewCount.value = pprops.value.viewCount;
});
</script>

<style scoped>

</style>