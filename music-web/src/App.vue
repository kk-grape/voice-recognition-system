<template>
  <router-view />
  <AiChatWidget v-if="showAiChat" />
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import AiChatWidget from './components/AiChatWidget.vue';

const route = useRoute();

// 在登录、注册、忘记密码页面隐藏AI聊天小助手
const showAiChat = computed(() => {
  const hideRoutes = ['/login', '/register', '/reset'];
  return !hideRoutes.includes(route.path);
});
</script>
