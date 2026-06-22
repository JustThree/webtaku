<script setup>
import {toRef, toRefs, watchEffect} from "vue";

const props = defineProps({
  boardone: Object
});

const {boardId, title, content, created, noticeYn, updated, userEmail, userNickname, viewCount, boardReplyCount} = toRefs(props.boardone);

const pprops = toRef(props, "boardone");

watchEffect(()=>{
    boardId.value = pprops.value.boardId;
    title.value = pprops.value.title;
    created.value = pprops.value.created;
    updated.value = pprops.value.updated;
    userEmail.value = pprops.value.userEmail;
    userNickname.value = pprops.value.userNickname;
    viewCount.value = pprops.value.viewCount;
    boardReplyCount.value = pprops.value.boardReplyCount;
});
</script>
<template>
    <div>
        <v-card
            width="100%"
            @click="$router.push({path: `/boards/${boardId}`, query:{noticeYn: noticeYn}})"
            class="card-frame"
        >
            <div class="card-center-frame">
                <div class="card-left-frame">
                    <div class="card-title-frame">
                        <span class="text-h6 font-weight-black">{{title}}</span>
                    </div>
                    <div class="card-content-frame">
                        <span class="font-weight-regular text-overflow">{{content}}</span>
                    </div>
                    <div class="card-bottom-frame">
                        <span class="font-weight-light">{{userNickname}} | </span>
                        <span v-if="created === updated" class="font-weight-light">등록일자 {{ created}}</span>
                        <span v-else-if="created !== updated" class="font-weight-light">수정일자 {{ updated}}</span>
                    </div>
                </div>
                <div class="card-right-frame">
                    <div><v-icon>mdi-comment</v-icon> {{boardReplyCount}}</div>
                    <div><v-icon>mdi-eye</v-icon> {{viewCount}}</div>
                </div>
            </div>
        </v-card>
    </div>
</template>

<style scoped>
.card-frame {
    border-top: 1px solid;
    border-radius: 0;
    min-height: 160px;
}
.card-center-frame {
    width: 100%;
    min-height: 150px;
    margin: 5px 0;
    padding: 5px;
    border-left: none;
    border-right: none;
    border-bottom: 1px;
    border-radius: 0;
    display: flex;
    justify-content: space-between;
}

@media (max-width: 600px) {
    .card-frame {
        min-height: 110px;
    }
    .card-center-frame {
        min-height: 100px;
        gap: 8px;
    }
    .card-left-frame {
        gap: 4px;
    }
    .card-left-frame :deep(.text-h6) {
        font-size: 1rem !important;
        line-height: 1.25;
    }
    .card-bottom-frame {
        font-size: 0.78em;
        flex-wrap: wrap;
    }
    .card-right-frame {
        width: auto;
        min-width: 50px;
        font-size: 0.85em;
        gap: 4px;
    }
}
/*카드 본문 내용(왼쪽)*/
.card-left-frame {
    margin-right: auto;
    width: 90%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
    gap: 10px;
}

.card-content-frame {
    width: 100%;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.card-bottom-frame {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 4px;
}
/*댓글수 & 조회수*/
.card-right-frame {
    margin-left: auto;
    width: 10%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 7px;
}
</style>
