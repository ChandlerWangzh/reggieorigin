package com.wzh.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.common.CustomException;
import com.wzh.reggie.dto.DishDto;
import com.wzh.reggie.entity.Dish;
import com.wzh.reggie.entity.DishFlavor;
import com.wzh.reggie.entity.Setmeal;
import com.wzh.reggie.mapper.DishMapper;
import com.wzh.reggie.service.DishFlavorService;
import com.wzh.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品 同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        this.save(dishDto);

        Long dishId = dishDto.getId();

        //保存菜品口味数据到菜品口味表
//        dishFlavorService.saveBatch(dishDto.getFlavors());
        List<DishFlavor> flavors = dishDto.getFlavors();
       flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

       //保存菜品口味数据到菜品口味表dish——flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id来查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWtihFlavor(Long id) {
        //查询菜品基本信息 从dish表
        Dish dish = this.getById(id);

        DishDto dishDto =new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询菜品对应的口味信息 从dishflavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表的基本信息
        this.updateById(dishDto);
        //先清理当前菜品对应口味数据 --dishflavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据  insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    @Transactional//保证事务的一致性
    public void deleteWithFlavor(List<Long> ids) {
        //查看订单中是否存在在售的订单
        //select count(*) from dish where ids in() and status=1;
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count = this.count(queryWrapper);
        if (count>0) {
            //抛出异常
            throw new CustomException("物品正在售卖，无法删除");
        }
        //删除dish表中的数据
        this.removeByIds(ids);
        //删除dishflvor表中的数据
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getId,ids);
        dishFlavorService.remove(queryWrapper1);
    }

    @Transactional
    public void updateSetmealStatusById(Integer status, List<Long> ids) {
       //        构造一个套餐的条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
      //        条件查询到具体的套餐
        queryWrapper.in(ids !=null,Dish::getId,ids);
      //        菜品根据套餐的条件查询
        List<Dish> list = this.list(queryWrapper);
        for(Dish dish: list)
        {
            if(dish !=null)
            {
                dish.setStatus(status);
                this.updateById(dish);
            }
        }
    }
}
