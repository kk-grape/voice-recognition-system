<template>
  <a-layout class="main-layout">
    <the-header></the-header>
    <a-layout class="content-layout">
      <the-sider></the-sider>
      <a-layout-content class="main-content">
        <router-view/>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import TheHeader from "../components/the-header.vue";
import TheSider from "../components/the-sider.vue";
</script>

<style scoped>
.main-layout {
  min-height: 100vh;
  background: transparent;
}

.content-layout {
  background: transparent;
  padding: 20px;
}

.main-content {
  background: var(--bg-card);
  border-radius: var(--radius-large);
  box-shadow: var(--shadow-card);
  padding: 24px;
  min-height: calc(100vh - 110px);
  margin-left: 20px;
  overflow: auto;
}
</style>
