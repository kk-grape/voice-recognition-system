<template>
  <a-layout>
    <the-header></the-header>
    <a-layout-content
      :style="{
        background: '#fff',
        padding: '24px',
        margin: 0,
        minHeight: '280px',
      }"
    >
      <router-view />
    </a-layout-content>
  </a-layout>
</template>

<script setup>
import TheHeader from "../components/the-header.vue";
</script>

<style scoped>
.site-layout-background {
  background: #fff;
}
</style>
