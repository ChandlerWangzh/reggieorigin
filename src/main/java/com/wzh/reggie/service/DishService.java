package com.wzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzh.reggie.dto.DishDto;
import com.wzh.reggie.entity.Dish;

import java.util.List;

public interface DishService  extends IService<Dish> {

    //新增菜品同时插入菜品对应的口味数据，需要操作两张表：dish，dish——flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id来查询菜品信息和对应的口味信息
    public DishDto getByIdWtihFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void deleteWithFlavor(List<Long> ids);

    public void updateSetmealStatusById(Integer status,List<Long> ids);
}
