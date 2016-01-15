package com.ennew.callback;

/**
 * 多布局时的回调函数
 */
public interface MultiItemTypeSupport<T> {
	int getLayoutItemRes(int position, T t);

	int getItemViewTypeCount();

	int getItemViewType(int position, T t);

}
