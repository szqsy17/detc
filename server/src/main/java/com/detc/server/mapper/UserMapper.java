package com.detc.server.mapper;

import com.detc.server.model.UserModel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author szqsy17
 */
@Repository
public interface UserMapper {

    @Insert("INSERT INTO user (username, password, role, department) VALUES (#{username}, #{password}, #{role}, #{department})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addOneUser(UserModel user);

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteOneUser(String id);

    @UpdateProvider(type = UserSqlProvider.class, method = "updateOneUser")
    int updateOnePerson(UserModel user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    List<UserModel> getUserById(String id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    UserModel getUserByUsername(String username);

    @Select("SELECT * FROM user WHERE id <> 1 ORDER BY id DESC")
    List<UserModel> getAllUser();

}
