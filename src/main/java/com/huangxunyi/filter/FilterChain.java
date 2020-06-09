package com.huangxunyi.filter;

import com.huangxunyi.rpc.Invoker;

import java.util.List;

public class FilterChain<T> {
    private List<Filter> filters;

    private Invoker<T> invoker;

    public FilterChain(List<Filter> filters, Invoker<T> invoker) {
        this.filters = filters;
        this.invoker = invoker;
    }

    public FilterChain(Invoker<T> invoker) {
        this(LoadFilters.create().getFilters(), invoker);
    }

    public Invoker<T> buildFilterChain() {
        // 最后一个
        Invoker<T> last = invoker;

        for (int i = filters.size() - 1; i >= 0; i--) {
            last = new FilterWrapper(filters.get(i), last);
        }
        // 第一个
        return last;

    }
}