import request from "./request";

/**
 * 订单管理相关 API
 */

/**
 * 查询订单列表（管理员版本，分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.orderNo] - 订单号（可选，模糊查询）
 * @param {string} [params.memberId] - 会员ID（可选）
 * @param {string} [params.status] - 订单状态（可选）
 */
export const getOrderList = (params) => {
  return request.get("/admin/order/list", { params });
};

/**
 * 查看订单详情
 * @param {number} id - 订单ID
 */
export const getOrderDetail = (id) => {
  return request.get(`/admin/order/detail/${id}`);
};

/**
 * 根据订单号查看订单详情
 * @param {string} orderNo - 订单号
 */
export const getOrderDetailByOrderNo = (orderNo) => {
  return request.get(`/admin/order/detail-by-orderno/${orderNo}`);
};

/**
 * 统计订单总数
 */
export const getOrderCount = () => {
  return request.get("/admin/order/count");
};

/**
 * 按状态统计订单数量
 */
export const getOrderCountByStatus = () => {
  return request.get("/admin/order/count-by-status");
};

/**
 * 统计订单总金额
 */
export const getOrderSumAmount = () => {
  return request.get("/admin/order/sum-amount");
};
