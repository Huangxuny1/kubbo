package com.huangxunyi.rpc;


import com.huangxunyi.filter.Filter;
import com.huangxunyi.filter.FilterChain;
import com.huangxunyi.remoting.message.Request;
import com.huangxunyi.remoting.message.Response;

import java.util.List;

/**
 * 卡尔
 */
public interface Invoker<T> {

    Class<T> getInterface();

    Response invoke(Request request) throws Exception;

}
