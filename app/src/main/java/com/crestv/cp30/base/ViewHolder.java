package com.crestv.cp30.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhy.autolayout.utils.AutoUtils;


public class ViewHolder
{
	private final SparseArray<View> mViews;
	private int mPosition;
	private View mConvertView;
	private LayoutInflater layoutInflater;

	private ViewHolder(Context context, ViewGroup parent, int layoutId,
					   int position)
	{
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		layoutInflater = LayoutInflater.from(context);
		mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
				false);
		// setTag
		mConvertView.setTag(this);
		//对于listview，注意添加这一行，即可在item上使用高度
		AutoUtils.autoSize(mConvertView);
	}

	/**
	 * 拿到一个ViewHolder对象
	 * 
	 * @param context
	 * @param convertView
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @return
	 */
	public static ViewHolder get(Context context, View convertView,
								 ViewGroup parent, int layoutId, int position)
	{
		ViewHolder holder = null;
		if (convertView == null)
		{
			holder = new ViewHolder(context, parent, layoutId, position);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
			holder.mPosition = position;
		}
		return holder;
	}

	public View getConvertView()
	{
		return mConvertView;
	}

	/**
	 * 通过控件的Id获取对于的控件，如果没有则加入views
	 * 
	 * @param viewId
	 * @return
	 */
	public <T extends View> T getView(int viewId)
	{
		View view = mViews.get(viewId);
		if (view == null)
		{
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * 为TextView设置字符串
	 * 
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ViewHolder setText(int viewId, String text)
	{
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param drawableId
	 * @return
	 */
	public ViewHolder setImageResource(int viewId, int drawableId)
	{
		ImageView view = getView(viewId);
		view.setImageResource(drawableId);

		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param
	 * @return
	 */
	public ViewHolder setImageBitmap(int viewId, Bitmap bm)
	{
		ImageView view = getView(viewId);
		view.setImageBitmap(bm);
		return this;
	}

	/**
	 * 为ImageView设置图片
	 * 
	 * @param viewId
	 * @param
	 * @return
	 */
	public ViewHolder setImageByUrl(Context context,int viewId, String url)
	{
		//ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(url, (ImageView) getView(viewId));
		Glide.with(context).load(url).asBitmap().centerCrop().into((ImageView) getView(viewId));
		return this;
	}

	public int getPosition()
	{
		return mPosition;
	}
	public LayoutInflater getLayoutInflater(){
		return layoutInflater;
	}

}
