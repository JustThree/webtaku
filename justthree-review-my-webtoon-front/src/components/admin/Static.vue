<script setup>
import BarChart from "@/components/admin/Barchart.vue";
</script>

<template>
  <BarChart></BarChart>
<!--  <div id="statistics" class="d-flex">-->
<!--    <div id="followStat">-->

<!--    </div>-->
<!--    <div id="genreStat">-->

<!--    </div>-->
<!--  </div>-->
<!--  <div id="userList">-->
<!--  </div>-->
</template>

<style scoped>
  #statistics{
    display: flex;
    width: 100%;
    height: 50vh;
  }
  #followStat{
    width: 50%;
  }
  #genreStat{
    width: 50%;
  }
  #userList{
    width: 100%;
    height: 50vh;
  }

</style>