package com.lht.lhtsharding.demo.mapper;

import com.lht.lhtsharding.demo.model.Order;
import com.lht.lhtsharding.demo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * @author Leo
 * @date 2024/10/24
 */
@Mapper
public interface OrderMapper {

    @Insert("insert into t_order (id,uid,price) values(#{id},#{uid},#{price})")
    int insert(Order user);


    @Select("select * from t_order where id=#{id} and uid=#{uid}")
    Order findById(int id, int uid);

    @Update("update t_order set price=#{price} where id=#{id} and uid=#{uid}")
    int update(Order user);


    @Delete("delete from t_order where id=#{id} and uid=#{uid}")
    int deleteById(int id, int uid);


}
