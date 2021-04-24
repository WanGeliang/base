package com.wgl.dao;

import com.wgl.dataobject.ItemStockDO;
import org.apache.ibatis.annotations.Param;

public interface ItemStockDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Sat Oct 03 16:51:58 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Sat Oct 03 16:51:58 CST 2020
     */
    int insert(ItemStockDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Sat Oct 03 16:51:58 CST 2020
     */
    int insertSelective(ItemStockDO record);


    int decreaseStock(@Param("itemId") Integer itemId,@Param("amount") Integer amount);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Sat Oct 03 16:51:58 CST 2020
     */
    ItemStockDO selectByPrimaryKey(Integer id);


    ItemStockDO selectByItemId(Integer id);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Sat Oct 03 16:51:58 CST 2020
     */
    int updateByPrimaryKeySelective(ItemStockDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item_stock
     *
     * @mbg.generated Sat Oct 03 16:51:58 CST 2020
     */
    int updateByPrimaryKey(ItemStockDO record);
}