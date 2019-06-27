package com.jt.service;

import java.util.List;

import com.jt.pojo.Cart;

//在jt-common中添加接口
public interface DubboCartService {

	List<Cart> findCartListByUserId(Long userId);

	void updateCartNum(Cart cart);

	void deleteCart(Cart cart);

	void insertCart(Cart cart);
	
}
