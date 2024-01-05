<template>
  <div class="d-flex">
    <v-select
        v-model="queryString.type"
        :items="items"
        :rules="[v => !!v || 'Item is required']"
        class="itemBox"
        required
    ></v-select>
    <v-card-text class="mt-1 pb-6">
      <v-text-field
          v-model="searchVal"
          density="compact"
          variant="outlined"
          label="Search"
          append-inner-icon="mdi-magnify"
          single-line
          hide-details
          class="searchContainer"
          @click:append-inner="onClick"
      ></v-text-field>
    </v-card-text>
  </div>


  <v-table>
    <thead>
    <tr>
      <th class="text-left">
        유저 번호
      </th>
      <th class="text-left">
        이메일
      </th>
      <th class="text-left">
        닉네임
      </th>
      <th class="text-left">
        가입일
      </th>
      <th class="text-left">
        상태코드
      </th>
      <th class="text-left">
        관리
      </th>
    </tr>
    </thead>
    <tbody>
    <tr
        v-for="item in userList"
        :key="item.usersId"
    >
      <td class="text-left">{{ item.usersId }}</td>
      <td class="text-left">{{ item.usersEmail }}</td>
      <td class="text-left">{{ item.usersNickname }}</td>
      <td class="text-left">{{ item.created}}</td>
      <td class="text-left">{{ item.statusCode }}</td>
      <td class="text-left" @click="deleteUser(item.usersId)">Delete</td>
    </tr>
    </tbody>

  </v-table>
  <v-pagination
      class="v-row v-md-12"
      v-model="page"
      :length="totalPages"
      total-visible="8"
      @click="onClick"
      active-color=#5302FE
  >
    <!-- totalPages 0부터 시작-->
  </v-pagination>
</template>

<script setup>

import {api} from "@/common.js";
import {onMounted, ref, watch} from "vue";

const userList = ref([]);
const page = ref(1);
const totalPages = ref();
const searchVal = ref();
const queryString =  ref({
      page:0,
      type:"전체",
    }
)

const typeObj = {
  "전체" : "All",
  "이메일" : "Email",
  "닉네임" : "Nickname"
}

const items = [
  '전체',
  '이메일',
  '닉네임',
]
const select = ref(null);


onMounted(() => {
  fetchData();
  console.log(userList);
});

async function getUserList(){
  await api(`api/getUserList?page=${page.value -1}`, "GET").then(r => {
    console.log(`api/getUserList?page=${page.value -1}`);
    console.log(r);
    userList.value = r.content;
    totalPages.value = r.totalPages;
  }).catch(e => {
    alert(e);
  });
}


watch(
    () => queryString.value.type,
    (nowPage, lastPage) => {
      if (queryString.value.type==="전체") {
        fetchData()
      }

    }
)


function deleteUser(userId){
  if(confirm("회원을 비활성화 하시겠습니까?")){
    api('api/deleteUser','POST',userId).then(() => {
      alert("비활성화 되었습니다.");
      getUserList();
    }).catch(() =>{
      alert("비활성화에 실패하였습니다.");
    });
  }
}

// 페이지네이션
const fetchData = async () => {
  try {
    const response = await api(`api/getUserList?page=${page.value-1}&type=${typeObj[queryString.value.type]}&search=${searchVal.value}`
        , "GET").then(r => {
          console.log(r);
      userList.value = r.content;
      totalPages.value = r.totalPages;
    });
    console.log(`api/getUserList?page=${page.value-1}&type=${typeObj[queryString.value.type]}&search=${searchVal.value}`);

  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

const onClick = () => {
  fetchData();
}

</script>


<style scoped>
  tr:nth-child(1){

  }
  tr:nth-child(2){
    max-width : max-content;
  }
  tr:nth-child(3){
    min-width : max-content;
  }
  tr:nth-child(4){

  }
  tr:nth-child(5){

  }
  tr:nth-child(6){

  }

  .searchContainer{
    max-width: 400px;
  }
  .itemBox{
    max-width: 200px;
  }

</style>