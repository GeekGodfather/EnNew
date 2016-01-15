package com.ennew.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ennew.utils.EmojiUtil;
import com.ennew.utils.StringUtil;

import java.util.ArrayList;

/**
 * 自定义能识别表情、网址和表情的TextViews
 */
public class DistinguishTextView extends TextView {


	private boolean isConsumeClick = true;

	public void setConsumeClick(boolean consumeClick) {
		isConsumeClick = consumeClick;
	}

	public DistinguishTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DistinguishTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DistinguishTextView(Context context) {
		super(context);
	}
	@Override
	public void setText(CharSequence text, BufferType type) {
		if (text != null && !StringUtil.isEmpty(text.toString())) {
			SpannableStringBuilder contentSpan = new SpannableStringBuilder(text);
			distinguishSpannable(contentSpan);
			super.setText(contentSpan, BufferType.SPANNABLE);
			if(isConsumeClick){
				super.setMovementMethod(LinkMovementMethod.getInstance());
			}
		}else{
			super.setText(text, type);
		}
	}

	/**
	 * 识别字符串中存在的网址、手机号码和表情
	 */
	public void distinguishSpannable(SpannableStringBuilder builder) {
		// 进行网址的识别
		distinguishUrl(builder);
		// 进行电话的识别
		distingishTel(builder);
		// 进行表情识别
		distinguishEmoji(builder);
	}

	private void distinguishEmoji(SpannableStringBuilder builder) {
		// 获得输入的字符串的长度
		int length = builder.length();
		// 遍历到的当前字符的下标
		int position = 0;
		// 表情字符串开始字符的下标
		int emojiStartPosition = 0;
		// 表情字符串的长度
		int emojiLength = 0;
		// 用于拼接表情字符串
		StringBuilder buffer = null;
		// 是否开始拼接表情字符串
		boolean isEmjioStart = false;
		do {
			String singleStr = builder.subSequence(position, position + 1)
					.toString();

			// 遍历到表情的开始标签“]”
			if (!isEmjioStart && EmojiUtil.EMOJI_START_CHAR.equals(singleStr)) {
				buffer = new StringBuilder();
				// 记录表情字符串开始的下标
				emojiStartPosition = position;
				// 提示表情字符串开始拼接
				isEmjioStart = true;
				// 初始化表情字符串的长度
				emojiLength = 0;
			}
			if (isEmjioStart) {
				// 拼接表情字符串
				buffer.append(singleStr);
				// 计算表情字符串的长度
				emojiLength++;
				// 遍历到表情的结束标签“]”
				if (EmojiUtil.EMOJI_END_CHAR.equals(singleStr)) {
					// 提示表情字符串拼接完成
					isEmjioStart = false;

					String emojiStr = buffer.toString();
					// 计算表情字符串结束的位置在总字符串中的下标
					int emojiEndPosition = emojiStartPosition + emojiLength;
					// 防止有多个“[”
					int lastIndex = emojiStr
							.lastIndexOf(EmojiUtil.EMOJI_START_CHAR);
					if (lastIndex > 0) {
						emojiStartPosition = emojiStartPosition + lastIndex;
						emojiStr = emojiStr.substring(lastIndex,
								emojiStr.length());
					}
					// 对表情字符串进行替换
					ImageSpan imageSpan = getEmojiImageSpan(emojiStr);
					if (imageSpan != null) {
						builder.setSpan(imageSpan, emojiStartPosition,
								emojiEndPosition,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
			position++;
		} while (position < length);
	}

	/**
	 * 根据表情字符串获得表情图片
	 */
	private ImageSpan getEmojiImageSpan(String emojiStr) {
		try {
			// 根据表情字符串获得表情的后缀编码
			String emojiResIdStr = EmojiUtil.getInstance().getFaceId(emojiStr);
			// 根据表情图片的名称获得表情图片的资源ID
			Resources resources = getContext().getResources();
			int emojiResId = resources.getIdentifier(
					EmojiUtil.STATIC_FACE_PREFIX + emojiResIdStr, "drawable",
					getContext().getPackageName());
			if (emojiResId != 0) {
				// 计算图片的大小
				int imageSize = Math.round(getTextSize());
				// 根据表情图片的资源ID获得静态图片
				Drawable emojiDrawable = getContext().getResources()
						.getDrawable(emojiResId);
				// 设置图片的绘制范围
				emojiDrawable.setBounds(0, 0, imageSize, imageSize);
				ImageSpan imageSpan = new ImageSpan(emojiDrawable,
						ImageSpan.ALIGN_BOTTOM);
				return imageSpan;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 识别字符串中的手机号码
	 */
	private void distingishTel(SpannableStringBuilder builder) {
		String str = builder.toString();
		int startIndex = 0;
		int lastIndex = 0;
		// 储存上一个字符串
		String matcherStr = "";
		// 获取字符串中是电话号的数字字符串
		ArrayList<String> numbers = StringUtil.subNumberForString(str);
		// 对字符串中的数字进行识别
		if (numbers != null && numbers.size() > 0) {
			for (int i = 0; i < numbers.size(); i++) {
				String tempStr = numbers.get(i);
				if (!TextUtils.isEmpty(tempStr) && tempStr.equals(matcherStr)) {
					startIndex = str.indexOf(tempStr, lastIndex);
				} else {
					// 获得电话号开始的位置
					startIndex = str.indexOf(tempStr);
					matcherStr = tempStr;
				}
				// 获得电话号结束的位置
				lastIndex = startIndex + tempStr.length();
				// 对电话号进行处理
				builder.setSpan(new DistinguishOperationSpan(numbers.get(i)),
						startIndex, lastIndex,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

	/**
	 * 识别字符串中的url
	 */
	private void distinguishUrl(SpannableStringBuilder builder) {
		String str = builder.toString();
		int startIndex = 0;
		int lastIndex = 0;
		// 获取字符串中的URL
		ArrayList<String> urls = StringUtil.subUrlForString(str);
		// 对字符串的中URL进行识别
		if (urls != null && urls.size() > 0) {
			for (int i = 0; i < urls.size(); i++) {
				// 获得URL开始的位置
				startIndex = str.indexOf(urls.get(i));
				// 获得URL结束的位置
				lastIndex = startIndex + urls.get(i).length();
				// 对URL进行处理
				builder.setSpan(new DistinguishOperationSpan(urls.get(i)),
						startIndex, lastIndex,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}

}
