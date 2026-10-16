import {createRouter, createWebHistory} from 'vue-router'
import Home from "../view/home.vue"
import Login from "../view/login.vue";
import Welcome from "../view/home/welcome.vue"
import Help from "../view/home/help.vue";
import Filetrans from "../view/home/filetrans.vue";
import Member from "../view/home/member.vue";
import Order from "../view/home/order.vue";
import Fileformat from "../view/home/fileformat.vue";
import AiChat from "../view/home/ai-chat.vue";
import Realtime from "../view/home/realtime.vue";

const routes = [{
  path: "/",
  redirect: "/login"
}, {
  path: "/login",
  component: Login
}, {
  path: "/home",
  component: Home,
  children: [{
    path: "welcome",
    component: Welcome,
  }, {
    path: "filetrans",
    component: Filetrans,
  }, {
    path: "member",
    component: Member,
  }, {
    path: "order",
    component: Order,
  }, {
    path: "fileformat",
    component: Fileformat,
  }, {
    path: "ai-chat",
    component: AiChat,
  }, {
    path: "realtime",
    component: Realtime,
  }, {
    path: "help",
    component: Help,
  }]
}]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
