package com.cl.api.util;

import com.alibaba.dubbo.common.serialize.support.SerializationOptimizer;

import java.util.Collection;

/**
 * Created by TF on 2016/8/8.
 */
public class SerializationOptimizerImpl implements SerializationOptimizer {

    @Override
    public Collection<Class> getSerializableClasses() {
        return null;
    }
}
