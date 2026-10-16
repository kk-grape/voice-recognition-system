import request from "./request";

/**
 * 文件格式转换管理相关 API
 */

/**
 * 获取转换文件目录信息
 */
export const getDirInfo = () => {
  return request.get("/admin/fileformat/dir-info");
};

/**
 * 获取转换文件目录大小
 */
export const getDirSize = () => {
  return request.get("/admin/fileformat/dir-size");
};

/**
 * 刷新目录信息
 */
export const refreshDirInfo = () => {
  return request.post("/admin/fileformat/refresh-dir-info");
};
