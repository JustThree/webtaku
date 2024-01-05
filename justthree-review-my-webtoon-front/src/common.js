import {
    reactive,
    computed,
    toRefs
} from 'vue';
import { storeToRefs } from 'pinia';
import axios from 'axios';

const apiToken = async (urn, method, data, token) => {
    const url = "http://" + window.location.hostname + ":8089/" + urn
    console.log(url)
    return (await axios({
        url,
        method,
        data,
        headers: {
            Authorization: token,
        }
    }).catch(e => {
        console.log("http://" + window.location.hostname + ":8089/" + urn)
        console.log(e);
        return { data: e}; //error 발생 시 e 반환
    })).data
}
const api = async (urn, method, data) => {
    const url = "http://" + window.location.hostname + ":8089/" + urn;
    return (await axios({
        method: method,
        url,
        data,
    }).catch(e => {
        return { data: e}; //error 발생 시 e 반환
    })).data;
}

const createdDiff = (created) => {
    const createdDateTime = new Date(created)
    const diff = Date.now() - createdDateTime;

    const millisecondsPerMinute = 1000 * 60;
    const millisecondsPerHour = millisecondsPerMinute * 60;
    const millisecondsPerDay = millisecondsPerHour * 24;

    if (diff < millisecondsPerMinute) {
        return "방금 전";
    } else if (diff < millisecondsPerHour) {
        const minutes = Math.floor(diff / millisecondsPerMinute);
        return `${minutes}분 전`;
    } else if (diff < millisecondsPerDay) {
        const hours = Math.floor(diff / millisecondsPerHour);
        return `${hours}시간 전`;
    } else{
        return `${createdDateTime.getFullYear()}년 ${createdDateTime.getMonth()}월 ${createdDateTime.getDate()}일` ;
    }
}

export {
    api, apiToken, createdDiff
};