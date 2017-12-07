package com.second.project.impl;

import com.second.project.dao.TbContentMapper;
import com.second.project.model.TbContent;
import com.second.project.service.TbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TbServiceImpl implements TbService {
    @Autowired
    private TbContentMapper tbContentMapper;
    @Override
    public List<TbContent> get() {
        return tbContentMapper.get();
    }
}
