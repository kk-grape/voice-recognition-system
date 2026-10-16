<template>
  <a-modal
    :title="payInfo.desc"
    :open="open"
    :confirm-loading="modalLoading"
    :afterClose="afterClose"
    :closable="false"
    style="top: 20px"
    width="1200px"
    height="500px"
  >
    <template #footer>
      <a-button key="back" @click="handleCancel" size="large"
        ><CloseOutlined />取消支付</a-button
      >
      <a-button
        key="submit"
        type="primary"
        :loading="modalLoading"
        @click="handleModalOk"
        size="large"
        ><CheckOutlined />我已支付</a-button
      >
    </template>
    <div class="pay-info">
      <div style="font-size: 25px; margin: 20px">
        <img
          style="width: 35px"
          src="/image/alipay-icon.jpg"
        />&nbsp;支付宝扫码支付
      </div>
      <iframe
        name="zfb-qrcode"
        style="height: 500px; border: none; width: 1100px; text-align: center"
        align="middle"
      ></iframe>
      <div style="font-size: 16px">
        打开手机支付宝，扫码支付<span style="color: red">{{
          payInfo.amount
        }}</span
        >元
      </div>
    </div>
  </a-modal>
</template>

<script>
import { defineComponent, onMounted, ref } from "vue";
import axios from "axios";
import { message, notification } from "ant-design-vue";

export default defineComponent({
  name: "the-alipay",
  emits: ["after-pay", "afterPay"],
  setup(props, context) {
    const payInfo = ref();
    payInfo.value = {};
    const orderNo = ref();
    orderNo.value = "";
    const open = ref(false);
    const modalLoading = ref(false);

    const handleModalOk = () => {
      queryOrder(orderNo.value);
    };

    /**
     * 新增
     */
    const handleOpen = (info) => {
      payInfo.value = info;
      console.log(info);
      open.value = true;

      // 需要放到异步，才能在iframe里显示支付宝二维码，否则浏览器会弹出新tab
      setTimeout(function () {
        // 清理之前可能存在的 form 容器
        let divForm = document.getElementsByTagName("divform");
        if (divForm.length) {
          document.body.removeChild(divForm[0]);
        }
        // 创建新的容器并插入支付宝返回的 form
        const div = document.createElement("divform");
        div.innerHTML = info.qrcode; // 支付宝返回的form
        document.body.appendChild(div);
        // 从刚创建的 div 中获取 form 元素，而不是使用 document.forms[0]
        const form = div.querySelector("form");
        if (form) {
          form.setAttribute("target", "zfb-qrcode");
          form.submit();
        } else {
          console.error("未找到支付宝支付表单");
          notification.error({
            message: "支付错误",
            description: "无法加载支付表单，请重试",
          });
        }
      }, 100);
      queryPayResult(info.orderNo);
    };

    /**
     * 取消
     */
    const handleCancel = () => {
      open.value = false;
      clearInterval(queryPayInterval);
    };

    /**
     * 取消
     */
    const afterClose = () => {
      console.log("afterClose");
      clearInterval(queryPayInterval);
    };

    // 支付成功后查询
    let queryPayInterval;

    // 定时查询支付结果
    const queryPayResult = (_orderNo) => {
      orderNo.value = _orderNo;
      queryPayInterval = setInterval(function () {
        queryOrder(_orderNo);
      }, 2000);
    };

    const queryOrder = (_orderNo) => {
      axios
        .get("/web/order-info/query-order-status/" + _orderNo)
        .then((response) => {
          const data = response.data;
          if (data.success) {
            const status = data.content;
            if (status === "S") {
              clearInterval(queryPayInterval);
              notification["success"]({
                message: "支付宝支付提示",
                description: "支付成功，感谢您的使用！",
              });
              open.value = false;
              context.emit("after-pay", "S");
            } else if (status === "F") {
              clearInterval(queryPayInterval);
              notification["error"]({
                message: "支付宝支付失败",
                description: "支付失败！请重新发起支付！",
              });
              context.emit("after-pay", "F");
            } else {
              // message.warning("支付未成功！状态：" + status);
              console.log("支付未成功！状态：" + status);
            }
          } else {
            message.error(data.message);
          }
        })
        .catch((error) => {
          // 在定时查询中，网络错误不显示通知，只记录日志
          console.error("查询订单状态失败：", error);
          // 如果连续多次失败，可以考虑停止定时查询
        });
    };

    onMounted(() => {
      console.log("the-alipay onMounted");
    });

    return {
      payInfo,
      open,
      modalLoading,
      handleModalOk,
      handleOpen,
      handleCancel,
      afterClose,
    };
  },
});
</script>

<style scoped>
.pay-info {
  text-align: center;
  margin-bottom: 20px;
}
</style>
