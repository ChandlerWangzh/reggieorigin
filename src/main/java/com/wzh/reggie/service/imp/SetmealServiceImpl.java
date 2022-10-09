package com.wzh.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.common.CustomException;
import com.wzh.reggie.dto.SetmealDto;
import com.wzh.reggie.entity.Setmeal;
import com.wzh.reggie.entity.SetmealDish;
import com.wzh.reggie.mapper.SetmealMapper;
import com.wzh.reggie.service.SetmealDishService;
import com.wzh.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐同时保存套餐和和菜品的基本关系
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息 操作 setmeal表 执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联信息 setmealdish 执行insert
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐的状态 确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if (count>0) {
            //如果不能删除  抛出业务异常 当前套餐正在售卖中
            throw new CustomException("套餐正在售卖中,不能删除");
        }
        //如果可以删除 先删除套餐表中的数据 setmeal
        this.removeByIds(ids);
        //删除关系表中的数据 setmealdish
//        delete from setmealdish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Transactional
    public void updateSetmealStatusById(Integer status, List<Long> ids) {
        //构造一个套餐的条件查询器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
//        条件查询到具体的套餐
        queryWrapper.in(ids !=null,Setmeal::getId,ids);
//        菜品根据套餐的条件查询
        List<Setmeal> list = this.list(queryWrapper);
        for(Setmeal setmeal: list)
        {
            if(setmeal !=null)
            {
                setmeal.setStatus(status);
                this.updateById(setmeal);
            }

        }
    }
}

