<template>
    <v-container>
        <div class="tab-menu-frame">
            <v-row>
                <v-tabs v-model="activeTab" active-class="custom-active-tab">
                    <v-tab to="/boardslist/comm" @click="showCommunityTab"><span class="tab-menu">커뮤니티</span></v-tab>
                    <v-tab to="/boardslist/notice"><span class="tab-menu">공지사항</span></v-tab>
                </v-tabs>
            </v-row>
        </div>
            <v-tabs-items v-model="activeTab">
                <v-tab-item>
                    <router-view></router-view>
                </v-tab-item>
            </v-tabs-items>
    </v-container>
</template>

<script setup>
import {ref} from "vue";
import router from "@/router/index.js";

const activeTab = ref('/boardslist/comm');
const showCommunityTab = () =>{
    //router.push('/boardslist/comm');
}
</script>

<style scoped>
.tab-menu-frame{
    margin: 30px;
}
.v-tab--selected {
    background-color: #8F7CEE;
    color: #ffffff;
}
.tab-menu{
    font-size: 20px;
    font-weight: bold;
}
</style>