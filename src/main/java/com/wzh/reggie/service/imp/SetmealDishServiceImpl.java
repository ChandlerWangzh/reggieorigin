package com.wzh.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzh.reggie.entity.SetmealDish;
import com.wzh.reggie.mapper.SetmealDishMapper;
import com.wzh.reggie.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl  extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
