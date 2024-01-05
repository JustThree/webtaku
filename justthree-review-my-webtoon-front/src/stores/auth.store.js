import { ref } from 'vue'
import { defineStore } from 'pinia'
import axios from 'axios'
import router from '@/router/index.js'
import {api} from "@/common.js";

const loginUrl = "http://" + window.location.hostname + ":8089" + import.meta.env.VITE_LOGIN_API_PATH;

async function setUser(user, response){
    user.value = {
        nickname: response.data.nickname,
        profile: response.data.profileImg,
        usersId: response.data.usersId,
        usersRole: response.data.usersRole,
        token: response.headers.authorization,
        accessToken: JSON.parse(response.headers.authorization).accessToken,
        refreshToken: JSON.parse(response.headers.authorization).refreshToken,

    };
    localStorage.setItem('user', JSON.stringify(user.value));
}

function handleLoginError(error) {
    if (error.response) {
        const status = error.response.status;
        if (status >= 400 && status < 500)
            alert("아이디 또는 비밀번호가 일치하지 않습니다.");
        else if (status >= 500)
            alert("문제가 발생했습니다. 잠시 후 다시 시도해주세요.");
    } else {
        // 네트워크 오류 또는 알 수 없는 오류 처리
        // alert(status);
    }
}

export const useAuthStore = defineStore('auth', () => {

    // 로그인한 유저 정보 (토큰, 닉네임, 프로필) : 없으면 null
    const user = ref(JSON.parse(localStorage.getItem('user')));
    // 로그인 후 이동할 페이지 경로 (없으면 홈) -> 로그인 전에 접근했던 페이지로 이동하기 위함
    const returnUrl = ref(null);

    async function login(email, password) {
        try {
            const response = await axios.post(loginUrl, {"usersEmail" : email, "usersPw" : password}, {
                headers: {'X-Requested-With': 'XMLHttpRequest'}});
            // const response = await api(import.meta.env.VITE_LOGIN_API_PATH,"post",{"usersEmail" : email, "usersPw" : password});
            await setUser(user, response);

            router.push(returnUrl.value != null ? returnUrl.value : '/');

        } catch (error) { handleLoginError(error); }
    }

    async function getTokenkey(){
        console.log(user);
        let token = ref(JSON.parse(user.value.token));
        let userEmail = ref(token.value.key);
        return userEmail;
    }
    async function logout(){
        let userEmail = await getTokenkey(user);
        await api(`api/logout`,'POST',{"usersEmail" : `${userEmail.value}`});
        user.value = null;
        localStorage.removeItem('user');
        router.push('/');
    }

    return { user, returnUrl, login, logout }
})