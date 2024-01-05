<script setup>
import {ref, defineProps, onBeforeMount} from 'vue';
import { api } from '@/common.js';

const props = defineProps(['masterId']);
const masterId = props.masterId;

const title = ref("");
const genre = ref("");
const writer = ref("");
const painter = ref("");
onBeforeMount(() => {

    api(`chats/info/${masterId}`, "GET", {})
        .then((resp) => {
            title.value = resp.title;
            genre.value = resp.genre;
            writer.value = resp.writer;
            painter.value = resp.value != null ? ", " + resp.value : "";
        })
})


</script>

<template>
<body class="mx-8">
        <h5 @click="$router.push(`/webtoon/${masterId}`)">{{title}}</h5>
        <h6>{{ genre }} / {{ writer }} {{ painter }}</h6>

</body>
</template>

<style scoped>
*{
    text-align: left;
}
h5{
    font-weight: 700;
}
h6{
    color: gray;
}
</style>