package com.third.project.service;



import com.third.project.model.TbContent;

import java.util.List;

public interface TbService {
    List<TbContent> get();

    void setJedis(String name, String value);
}
