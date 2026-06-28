import {
    reactive,
    computed,
    toRefs
} from 'vue';
import { storeToRefs } from 'pinia';
import axios from 'axios';

const apiBase = () => import.meta.env.PROD
    ? window.location.origin
    : `http://${window.location.hostname}:8089`;

const wsBase = () => {
    if (import.meta.env.PROD) {
        const proto = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        return `${proto}//${window.location.host}`;
    }
    return `ws://${window.location.hostname}:8089`;
};

const apiToken = async (urn, method, data, token) => {
    const url = `${apiBase()}/api/${urn}`;
    return (await axios({
        url,
        method,
        data,
        headers: {
            Authorization: token,
        }
    }).catch(e => {
        console.log(e);
        return { data: e};
    })).data
}
const api = async (urn, method, data) => {
    const url = `${apiBase()}/api/${urn}`;
    return (await axios({
        method: method,
        url,
        data,
    }).catch(e => {
        return { data: e};
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

const defaultProfile = new URL('@/assets/images/blackDUK-removebg-preview.png', import.meta.url).href

const profileSrc = (url) => url || defaultProfile

const onProfileError = (e) => {
    if (e.target && e.target.src !== defaultProfile) e.target.src = defaultProfile
}

export {
    api, apiToken, createdDiff, apiBase, wsBase, profileSrc, onProfileError, defaultProfile
};