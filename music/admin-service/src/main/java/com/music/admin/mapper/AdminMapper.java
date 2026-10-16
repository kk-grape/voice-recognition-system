package com.music.admin.mapper;

import com.music.admin.domain.Admin;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("SELECT * FROM admin WHERE login_name = #{loginName} LIMIT 1")
    Admin selectByLoginName(@Param("loginName") String loginName);

    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin selectById(@Param("id") Long id);

    @Select("SELECT * FROM admin ORDER BY id DESC")
    List<Admin> selectAllList();

    @Select("<script>" +
            "SELECT * FROM admin WHERE 1=1 " +
            "<if test='loginName != null and loginName != \"\"'>" +
            " AND login_name LIKE CONCAT('%', #{loginName}, '%')" +
            "</if>" +
            "<if test='role != null and role != \"\"'>" +
            " AND role = #{role}" +
            "</if>" +
            "<if test='status != null'>" +
            " AND status = #{status}" +
            "</if>" +
            " ORDER BY id DESC" +
            "</script>")
    List<Admin> selectListByCondition(@Param("loginName") String loginName,
                                     @Param("role") String role,
                                     @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM admin")
    long countAll();

    @Insert("INSERT INTO admin (id, login_name, password, name, avatar, phone, email, department, role, status, login_count, last_login_at, last_login_ip, created_at, updated_at, remark) " +
            "VALUES (#{id}, #{loginName}, #{password}, #{name}, #{avatar}, #{phone}, #{email}, #{department}, #{role}, #{status}, #{loginCount}, #{lastLoginAt}, #{lastLoginIp}, #{createdAt}, #{updatedAt}, #{remark})")
    int insert(Admin admin);

    @Update("<script>" +
            "UPDATE admin SET " +
            "<if test='password != null and password != \"\"'>password = #{password},</if>" +
            "<if test='name != null'>name = #{name},</if>" +
            "<if test='avatar != null'>avatar = #{avatar},</if>" +
            "<if test='phone != null'>phone = #{phone},</if>" +
            "<if test='email != null'>email = #{email},</if>" +
            "<if test='department != null'>department = #{department},</if>" +
            "<if test='role != null'>role = #{role},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "<if test='loginCount != null'>login_count = #{loginCount},</if>" +
            "<if test='lastLoginAt != null'>last_login_at = #{lastLoginAt},</if>" +
            "<if test='lastLoginIp != null'>last_login_ip = #{lastLoginIp},</if>" +
            "<if test='remark != null'>remark = #{remark},</if>" +
            "updated_at = NOW() " +
            "WHERE id = #{id}" +
            "</script>")
    int updateById(Admin admin);

    @Update("UPDATE admin SET login_count = #{loginCount}, last_login_at = #{lastLoginAt}, last_login_ip = #{lastLoginIp}, updated_at = NOW() WHERE id = #{id}")
    int updateLoginInfo(Admin admin);

    @Delete("DELETE FROM admin WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
