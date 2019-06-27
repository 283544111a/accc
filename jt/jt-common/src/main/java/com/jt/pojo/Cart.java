package com.jt.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;
@Data
@Accessors(chain = true)
@TableName("tb_cart")
public class Cart extends BasePojo{
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long userId;  //userId和itemId表示用户信息
	private Long itemId;
	private String itemTitle;
	private String itemImage; //保存商品的第一张图片信息
	private Long itemPrice;
	private Integer num;
	
}
