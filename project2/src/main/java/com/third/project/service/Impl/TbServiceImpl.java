package com.third.project.service.Impl;

import com.third.project.dao.TbContentMapper;
import com.third.project.model.TbContent;
import com.third.project.redis.JedisClient;
import com.third.project.service.TbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbServiceImpl implements TbService {
    @Autowired
    private TbContentMapper tbContentMapper;
    @Autowired
    private JedisClient jedisClient;
    @Override
    public List<TbContent> get() {
        return tbContentMapper.get();
    }

    @Override
    public void setJedis(String name, String value) {
        jedisClient.set(name,value);
    }
}
