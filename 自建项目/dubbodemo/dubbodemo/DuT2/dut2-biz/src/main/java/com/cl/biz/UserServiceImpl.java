package com.cl.biz;

import com.cl.api.service.UserService;

/**
 * Created by TF on 2016/8/9.
 */
public class UserServiceImpl implements UserService {

    @Override
    public String user() {
        return "user";
    }
}
