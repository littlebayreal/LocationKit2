package com.sziti.locationkit2.treeRecycleView;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.sziti.locationkit2.treeRecycleView.base.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baozi on 2017/4/20.
 * 树级结构recycleradapter.
 * item之间有子父级关系,
 */

public class TreeRecyclerAdapter extends BaseRecyclerAdapter<TreeItem> {

    private TreeRecyclerType type;

    private ItemManager<TreeItem> mItemManager;

    public TreeRecyclerAdapter() {
        this(null);
    }

    public TreeRecyclerAdapter(TreeRecyclerType treeRecyclerType) {
        type = treeRecyclerType == null ? TreeRecyclerType.SHOW_DEFUTAL : treeRecyclerType;
    }

    @Override
    public void onBindViewHolderClick(final ViewHolder holder, View view) {
        if (!view.hasOnClickListeners()) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //获取在recyclerview中的真实位置
                    int layoutPosition = holder.getLayoutPosition();
                    //获得处理后的position（剔除掉header footer的情况下得到的真实位置）
                    layoutPosition = checkPosition(layoutPosition);
                    //拿到BaseItem
                    TreeItem item = getDatas().get(layoutPosition);
                    TreeItemGroup itemParentItem = item.getParentItem();
                    //判断上一级是否需要拦截这次事件，只处理当前item的上级，不关心上上级如何处理.
                    if (itemParentItem != null && itemParentItem.onInterceptClick(item)) {
                        return;
                    }
                    //展开,折叠和item点击不应该同时响应事件.
                    //必须是TreeItemGroup才能展开折叠,并且type不能为 TreeRecyclerType.SHOW_ALL
                    if (item instanceof TreeItemGroup && type != TreeRecyclerType.SHOW_ALL) {
                        TreeItemGroup treeItemGroup = (TreeItemGroup) item;
                        //展开或者伸缩（实质上是对recyclerview进行添加和删除的操作）
                        treeItemGroup.setExpand(!treeItemGroup.isExpand());
                    }
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(holder, layoutPosition);
                    } else {
                        //拿到对应item,回调.
                        getDatas().get(layoutPosition).onClick(holder);
                    }
                }
            });
        }
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //获得holder的position
                int layoutPosition = holder.getLayoutPosition();
                //检查position是否可以点击
                //检查并得到真实的position
                int itemPosition = checkPosition(layoutPosition);
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(holder, itemPosition);
                }
                return false;
            }
        });
    }

    @Override
    public void setDatas(List<TreeItem> items) {
        if (null == items) {
            return;
        }
        getDatas().clear();
        assembleItems(items);
    }

    public void setDatas(TreeItemGroup treeItemGroup) {
        if (null == treeItemGroup) {
            return;
        }
        ArrayList<TreeItem> arrayList = new ArrayList<>();
        arrayList.add(treeItemGroup);
        setDatas(arrayList);
    }

    /**
     * 对初始的一级items进行遍历,将每个item的childs拿出来,进行組合。
     *
     * @param items
     */
    private void assembleItems(List<TreeItem> items) {
        //根据需要显示的类型进行datas的数据初始化
        if (type != null) {
            List<TreeItem> datas = getDatas();
            datas.addAll(ItemHelperFactory.getChildItemsWithType(items, type));
        } else {
            super.setDatas(items);
        }
    }

    public ItemManager<TreeItem> getItemManager() {
        if (mItemManager == null) {
            mItemManager = new TreeItemManageImpl(this);
        }
        return mItemManager;
    }

    public void setItemManager(ItemManager<TreeItem> itemManage) {
        this.mItemManager = itemManage;
    }

    @Override
    public int getLayoutId(int position) {
        return getDatas().get(position).getLayoutId();
    }

    private void checkItemManage(TreeItem item) {
        if (item.getItemManager() == null) {
            item.setItemManager(getItemManager());
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, TreeItem item, int position) {
//		TreeItem t = getDatas().get(position);
		//为每个item绑定manager 控制adapter
		checkItemManage(item);
		item.onBindViewHolder(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            //得到布局管理设置的列数
            final int spanCount = gridLayoutManager.getSpanCount();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    //获取数据的个数
                    int i = getItemCount();
                    if (i == 0) {
                        return spanCount;
                    }
                    int checkPosition = checkPosition(position);
                    if (checkPosition < 0 || checkPosition >= i) {
                        return spanCount;
                    }
                    int itemSpanSize = getItemSpanSize(checkPosition);
                    if (itemSpanSize == 0) {
                        return spanCount;
                    }
                    return itemSpanSize;
                }
            });
        }
    }

    public int getItemSpanSize(int position) {
        return getData(position).getSpanSize();
    }

//    /**
//     * 相应RecyclerView的点击事件 展开或关闭某节点
//     */
//    private void expandOrCollapse(TreeItemGroup treeItemGroup) {
//        boolean expand = treeItemGroup.isExpand();
//        treeItemGroup.setExpand(!expand);
//        treeItemGroup.notifyExpand();
//    }

    /**
     * 需要设置在setdata之前,否则type不会生效
     *
     * @param type
     */
    @Deprecated
    public void setType(TreeRecyclerType type) {
        this.type = type;
    }

    //实际上的itemManager实现类
    private class TreeItemManageImpl extends ItemManager<TreeItem> {

        TreeItemManageImpl(BaseRecyclerAdapter<TreeItem> adapter) {
            super(adapter);
        }

        @Override
        public void addItem(TreeItem item) {
            if (null == item) {
                return;
            }
            if (item instanceof TreeItemGroup) {
                getDatas().add(item);
            } else {
                TreeItemGroup itemParentItem = item.getParentItem();
                if (itemParentItem != null) {
                    List child = itemParentItem.getChild();
                    if (child != null) {
                        int i = getDatas().indexOf(itemParentItem);
                        getDatas().add(i + itemParentItem.getChild().size(), item);
                    } else {
                        child = new ArrayList();
                        itemParentItem.setChild(child);
                    }
                    child.add(item);
                }
            }
            notifyDataChanged();
        }

        @Override
        public void addItem(int position, TreeItem item) {
            getDatas().add(position, item);
            if (item != null && item.getParentItem() != null) {
                item.getParentItem().getChild().add(item);
            }
            notifyDataChanged();
        }

        @Override
        public void addItems(List<TreeItem> items) {
            getDatas().addAll(items);
            notifyDataChanged();
        }

        @Override
        public void addItems(int position, List<TreeItem> items) {
            getDatas().addAll(position, items);
            notifyDataChanged();
        }

        //带动画执行的添加
        public void addItems(BaseRecyclerAdapter<TreeItem> mAdapter, int position, List<TreeItem> items) {
            if (items == null || items.size() == 0) return;
            int size = items.size();
            for (int i = 0; i < size; i++) {
                getDatas().add(position + i, items.get(i));
                mAdapter.notifyItemInserted(position + i);
            }
        }

        //带动画执行的删除
        public void removeItems(BaseRecyclerAdapter<TreeItem> mAdapter, int position, List<TreeItem> items) {
            if (items == null || items.size() == 0) return;
            int size = items.size();
            for (int i = size + position - 1; i > position - 1; i--) {
                getDatas().remove(i);
                mAdapter.notifyItemRemoved(i);
            }
        }

        @Override
        public void removeItem(TreeItem item) {
            if (null == item) {
                return;
            }
            //将原始数据中的item删除
            getDatas().remove(item);
            TreeItemGroup itemParentItem = item.getParentItem();
            if (itemParentItem != null) {
                List childs = itemParentItem.getChild();
                if (childs != null) {
                    childs.remove(item);
                }
            }
            notifyDataChanged();
        }

        @Override
        public void removeItem(int position) {
            TreeItem t = getDatas().get(position);
            TreeItemGroup parentItem = t.getParentItem();
            if (parentItem != null && parentItem.getChild() != null) {
                parentItem.getChild().remove(t);
            }
            getDatas().remove(position);
            notifyDataChanged();
        }

        @Override
        public void removeItems(List<TreeItem> items) {
            if (items == null) return;
            getDatas().removeAll(items);
            notifyDataChanged();
        }

        @Override
        public void replaceItem(int position, TreeItem item) {
            TreeItem t = getDatas().get(position);
            if (t instanceof TreeItemGroup) {
                getDatas().set(position, item);
            } else {
                TreeItemGroup parentItem = t.getParentItem();
                if (parentItem != null && parentItem.getChild() != null) {
                    List childs = parentItem.getChild();
                    int i = childs.indexOf(t);
                    childs.set(i, item);
                }
                getDatas().set(position, item);
            }
            notifyDataChanged();
        }

        @Override
        public void replaceAllItem(List<TreeItem> items) {
            if (items != null) {
                setDatas(items);
                notifyDataChanged();
            }
        }

        @Override
        public TreeItem getItem(int position) {
            return getDatas().get(position);
        }

        @Override
        public int getItemPosition(TreeItem item) {
            return getDatas().indexOf(item);
        }

    }

}
