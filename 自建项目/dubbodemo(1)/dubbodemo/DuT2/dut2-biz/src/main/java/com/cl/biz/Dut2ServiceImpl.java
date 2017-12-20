package com.cl.biz;

import com.cl.api.service.Dut2Service;

/**
 * Created by TF on 2016/8/4.
 */
public class Dut2ServiceImpl implements Dut2Service {

    @Override
    public String test() {
        return "dubbo";
    }
}
