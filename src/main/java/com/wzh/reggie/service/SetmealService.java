package com.wzh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzh.reggie.dto.SetmealDto;
import com.wzh.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐同时保存套餐和和菜品的基本关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除~
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
    void updateSetmealStatusById(Integer status, List<Long> ids);
}
