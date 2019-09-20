package com.sziti.locationkit2.treeRecycleView.base;

import android.util.SparseArray;

/**
 * 为了进行多个布局的适配
 */
public class ItemConfig {

    private static SparseArray<Class<? extends TreeItem>> treeViewHolderTypes;

    static {
        treeViewHolderTypes = new SparseArray<>();
    }

    public static Class<? extends TreeItem> getTreeViewHolderType(int type) {
        return treeViewHolderTypes.get(type);
    }

    public static void addTreeHolderType(int type, Class<? extends TreeItem> clazz) {
        if (null == clazz) {
            return;
        }
        treeViewHolderTypes.put(type, clazz);
    }
}
