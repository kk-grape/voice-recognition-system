import { createRouter, createWebHistory } from "vue-router";
import Home from "../view/home.vue";
import Login from "../view/login.vue";
import Register from "../view/register.vue";
import Reset from "../view/reset.vue";
import Welcome from "../view/home/welcome.vue";
import Help from "../view/home/help.vue";
import Fileformat from "../view/home/fileformat.vue";
import Filetrans from "../view/home/filetrans.vue";
import Member from "../view/home/member.vue";
import VoiceAssistant from "../view/home/voice-assistant.vue";

const routes = [
  {
    path: "/",
    redirect: "/login",
  },
  {
    path: "/login",
    component: Login,
  },
  {
    path: "/register",
    component: Register,
  },
  {
    path: "/reset",
    component: Reset,
  },
  {
    path: "/home",
    component: Home,
    children: [
      { path: "", component: Welcome },
      { path: "welcome", component: Welcome },
      { path: "filetrans",component: Filetrans},
      { path: "help", component: Help },
      { path: "fileformat", component: Fileformat },
      { path: "member", component: Member },
      { path: "voice-assistant", component: VoiceAssistant },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
