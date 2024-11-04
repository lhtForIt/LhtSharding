package com.lht.lhtsharding.demo.mapper;

import com.lht.lhtsharding.demo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Mapper
public interface UserMapper {

    @Insert("insert into user(id,name,age) values(#{id},#{name},#{age})")
    int insert(User user);


    @Select("select * from user where id=#{id}")
    User findById(int id);

    @Update("update user set name=#{name},age=#{age} where id=#{id}")
    int update(User user);


    @Delete("delete from user where id=#{id}")
    int deleteById(int id);


}
