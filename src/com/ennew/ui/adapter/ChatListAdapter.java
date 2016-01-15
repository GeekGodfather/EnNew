package com.ennew.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.ennew.R;
import com.ennew.callback.AudioBtnOnClickListener;
import com.ennew.callback.MultiItemTypeSupport;
import com.ennew.config.MyConstant;
import com.ennew.db.DataBaseManager;
import com.ennew.model.MessageInfo;
import com.ennew.ui.base.BaseCommonAdapter;
import com.ennew.ui.base.ViewHolder;
import com.ennew.utils.ImageUtil;
import com.ennew.utils.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends BaseCommonAdapter<MessageInfo> implements
		MultiItemTypeSupport<MessageInfo> {

	private final String userName;
	private LruCache<Integer, Bitmap> imageCache = new LruCache<Integer, Bitmap>(
			(int) (Runtime.getRuntime().maxMemory() / 8)) {
		@Override
		protected int sizeOf(Integer key, Bitmap value) {
			return value.getWidth() * value.getHeight();
		}
	};

	Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				notifyDataSetChanged(false);
			}
		};
	};

	public void notifyImage() {
		final List<MessageInfo> tempList = new ArrayList<MessageInfo>();
		tempList.addAll(data);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < tempList.size(); i++) {
					MessageInfo info = tempList.get(i);
					if (info.getContentType() == 2
							&& !TextUtils.isEmpty(info.getContent())
							&& imageCache.get(i) == null) {
						Bitmap bitmap = ImageUtil.StringToBitmap(info
								.getContent());
						if (bitmap != null) {
							imageCache.put(i, bitmap);
							handler.sendEmptyMessage(0);
						}
					}
				}
				tempList.clear();
			}
		}).start();
	}

	public ChatListAdapter(Context context) {
		super(context, -1);
		setMultiItemTypeSupport(this);
		userName = SharedPrefUtil.getStateString(context,
				MyConstant.USER_NAME);
	}

	@Override
	public void convertView(final ViewHolder holder, final MessageInfo info) {
		holder.setTextView(R.id.item_chat_title, info.getSenderName());
		if (info.getContentType() == 2) {
			holder.setVisible(R.id.item_chat_prompt, false);
			holder.setVisible(R.id.item_chat_content, false);
			holder.setVisible(R.id.item_chat_audio_layout, false);
			holder.setVisible(R.id.item_chat_img, true);
			ImageView childView = holder.getChildView(R.id.item_chat_img);
			int position = holder.getPosition();
			if (imageCache.get(position) != null) {
				childView.setImageBitmap(imageCache.get(position));
				childView.setTag(position);
			} else {
				Object obj = childView.getTag();
				if (obj != null) {
					int tag = (Integer) obj;
					if (tag == position) {
						childView.setImageBitmap(null);
					}
				}
			}
		} else if (info.getContentType() == 1) {
			holder.setVisible(R.id.item_chat_prompt, false);
			holder.setVisible(R.id.item_chat_img, false);
			holder.setVisible(R.id.item_chat_audio_layout, false);
			holder.setVisible(R.id.item_chat_content, true);
			holder.setTextView(R.id.item_chat_content, info.getContent());
		} else if (info.getContentType() == 3) {
			// 当是别人发送的录音消息并且没有播放过，显示未读标志
			if(!TextUtils.isEmpty(userName)
					&& !userName.equals(info.getSenderName()) && info.getIsRead() == 0){
				holder.setVisible(R.id.item_chat_prompt,true);
			}else{
				holder.setVisible(R.id.item_chat_prompt,false);
			}
			holder.setVisible(R.id.item_chat_content, false);
			holder.setVisible(R.id.item_chat_img, false);
			holder.setVisible(R.id.item_chat_audio_layout, true);
			holder.setTextView(R.id.item_chat_duration, info.getDuration() + "\"");
			ImageView audioImageView = holder
					.getChildView(R.id.item_chat_audio);
			if (!info.getSenderName().equals(userName)) {
				audioImageView.setImageResource(R.drawable.chatfrom_voice_playing);
			} else {
				audioImageView.setImageResource(R.drawable.chatto_voice_playing);
			}
			AudioBtnOnClickListener audioBtnOnClickListener = new AudioBtnOnClickListener(
					audioImageView, info){
				@Override
				public void onClick(View v) {
					super.onClick(v);
					info.setIsRead(1);
					// 当录音播放的时候将当前标志隐藏，并且标记当前消息已读
					DataBaseManager.getInstance().updateMessageInfoReadState(info);
					holder.setVisible(R.id.item_chat_prompt, false);
				}
			};
			holder.setOnClickListener(R.id.item_chat_root_layout, audioBtnOnClickListener);
		}

	}

	@Override
	public int getLayoutItemRes(int position, MessageInfo t) {
		MessageInfo chatMsg = t;
		if (!TextUtils.isEmpty(userName)
				&& !userName.equals(chatMsg.getSenderName())) {
			return R.layout.item_chat_in;
		} else {
			return R.layout.item_chat_out;
		}
	}

	@Override
	public int getItemViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position, MessageInfo t) {
		MessageInfo chatMsg = t;
		String userName = SharedPrefUtil.getStateString(context,
				MyConstant.USER_NAME);
		if (!TextUtils.isEmpty(userName)
				&& !userName.equals(chatMsg.getSenderName())) {
			return 0;
		}
		return 1;
	}

	public void setDatas(List<MessageInfo> msgList) {
		data = msgList;
	}

	public void notifyDataSetChanged(boolean flag) {
		notifyDataSetChanged();
		if (flag) {
			notifyImage();
		}

	}

}
