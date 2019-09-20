package com.sziti.locationkit2.treeRecycleView.base;

import java.util.List;

/**
 * @param <T>
 * recycler中item的封装管理类
 */
public abstract class ItemManager<T> {

    private BaseRecyclerAdapter<T> mAdapter;

    public ItemManager(BaseRecyclerAdapter<T> adapter) {
        mAdapter = adapter;
    }

    public BaseRecyclerAdapter<T> getAdapter() {
        return mAdapter;
    }

    public void setAdapter(BaseRecyclerAdapter<T> adapter) {
        mAdapter = adapter;
    }

    //增
    public abstract void addItem(T item);

    public abstract void addItem(int position, T item);

    public abstract void addItems(List<T> items);

    public abstract void addItems(int position, List<T> items);

    public void addItems(BaseRecyclerAdapter<T> mAdapter, int position, List<T> items){}

    //删
    public abstract void removeItem(T item);

    public abstract void removeItem(int position);

    public abstract void removeItems(List<T> items);

    public void removeItems(BaseRecyclerAdapter<T> mAdapter, int position, List<T> items){}
    //改
    public abstract void replaceItem(int position, T item);

    public abstract void replaceAllItem(List<T> items);

    //查
    public abstract T getItem(int position);

    public abstract int getItemPosition(T item);

    //刷新
    public void notifyDataChanged() {
        mAdapter.notifyDataSetChanged();
    }

}
