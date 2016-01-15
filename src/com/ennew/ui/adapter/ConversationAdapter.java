package com.ennew.ui.adapter;

import android.content.Context;

import com.ennew.R;
import com.ennew.model.Conversation;
import com.ennew.ui.base.BaseCommonAdapter;
import com.ennew.ui.base.ViewHolder;
import com.ennew.widget.DistinguishTextView;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

/**
 * 会话记录
 */

public class ConversationAdapter extends BaseCommonAdapter<Conversation> {


	private BitmapDisplayConfig config;

	public ConversationAdapter(Context context) {
		super(context, R.layout.conversation_item_layout);
		config = new BitmapDisplayConfig();
		config.setLoadingDrawable(context.getResources().getDrawable(R.drawable.def_heardimg));
		config.setLoadFailedDrawable(context.getResources().getDrawable(R.drawable.def_heardimg));
	}

	@Override
	public void convertView(ViewHolder holder, Conversation conversation) {
		holder.setTextView(R.id.friend_name, conversation.getConversationName());
		holder.setImageUrl(R.id.heard_img, conversation.getPortraitImg(), config);
		holder.setTextView(R.id.msg_last_time, conversation.getLastTime());
		DistinguishTextView contentTv = holder.getChildView(R.id.msg_content);
		contentTv.setConsumeClick(false);
		if (conversation.getContentType() == 3) {
			contentTv.setText("[录音]");
		}  else if (conversation.getContentType() == 2) {
			contentTv.setText( "[图片]");
		}else {
			contentTv.setText( conversation.getLastMeaage());
		}
	}


	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
}
