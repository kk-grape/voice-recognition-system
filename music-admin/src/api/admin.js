import request from "./request";

/**
 * 管理员登录相关 API
 */

/**
 * 管理员登录
 * @param {Object} data - 登录参数
 * @param {string} data.loginName - 登录名
 * @param {string} data.password - 密码（MD5加密后的）
 * @param {string} data.imageCode - 图形验证码
 * @param {string} data.imageCodeToken - 图形验证码Token
 */
export const adminLogin = (data) => {
  return request.post("/admin/login", data);
};

/**
 * 获取管理员列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.loginName] - 登录名（可选，模糊查询）
 */
export const getAdminList = (params) => {
  return request.get("/admin/list", { params });
};

/**
 * 查看管理员详情
 * @param {number} id - 管理员ID
 */
export const getAdminDetail = (id) => {
  return request.get(`/admin/detail/${id}`);
};

/**
 * 新增管理员
 * @param {Object} data - 管理员信息
 * @param {string} data.loginName - 登录名
 * @param {string} data.password - 密码（MD5加密后的）
 * @param {string} [data.nickname] - 昵称（可选）
 */
export const createAdmin = (data) => {
  return request.post("/admin/create", data);
};

/**
 * 修改管理员
 * @param {number} id - 管理员ID
 * @param {Object} data - 修改的管理员信息
 */
export const updateAdmin = (id, data) => {
  return request.put(`/admin/update/${id}`, data);
};

/**
 * 删除管理员
 * @param {number} id - 管理员ID
 */
export const deleteAdmin = (id) => {
  return request.delete(`/admin/delete/${id}`);
};

/**
 * 统计管理员总数
 */
export const getAdminCount = () => {
  return request.get("/admin/count");
};
