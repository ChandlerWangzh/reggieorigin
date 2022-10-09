package com.wzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzh.reggie.entity.Orders;


public interface OrderService extends IService<Orders> {

    void submit(Orders orders);
}
