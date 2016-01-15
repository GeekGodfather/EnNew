package com.ennew.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.ennew.R;
import com.ennew.utils.EmojiUtil;

public class EmojiEditText extends EditText {

	public EmojiEditText(Context context) {
		super(context);
	}

	public EmojiEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmojiEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		SpannableString content = new SpannableString(text);
		DistinguishEmoji(content);
		super.setText(content, BufferType.SPANNABLE);
	}

	/**
	 * 将表情字符串转化成表情图片
	 */
	public void insertEmoji(String text) {
		SpannableStringBuilder builder = new SpannableStringBuilder();
		builder.append(text);
		ImageSpan imageSpan = getImageSpan(text);
		builder.setSpan(imageSpan, 0, text.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		int insertIndex = Selection.getSelectionEnd(getText());
		getText().insert(insertIndex, builder);
	}

	/**
	 * 将输入的字符串中的表情字符串使用图片替换
	 */
	private void DistinguishEmoji(Spannable spannable) {
		// 获得字符串原长度
		int length = spannable.length();
		// 当前字符串的下标
		int position = 0;
		// 表情字符串开始的位置
		int tagStartPosition = 0;
		// 表情字符串的长度
		int tagLength = 0;
		// 表情字符串
		StringBuilder buffer = new StringBuilder();
		// 是否开始拼接表情字符串
		boolean inTag = false;

		// 字符串长度为0时，直接返回
		if (length <= 0) {
			return;
		}
		do {
			String c = spannable.subSequence(position, position + 1).toString();

			// 遍历到表情的开始标签“[”
			if (!inTag && c.equals(EmojiUtil.EMOJI_START_CHAR)) {
				buffer = new StringBuilder();
				// 记录表情字符串开始的位置
				tagStartPosition = position;
				// Log.d(TAG, "   Entering tag at " + tagStartPosition);
				// 设置标识为true，表示表情字符开始拼装
				inTag = true;
				// 重新计算表情字符串的长度
				tagLength = 0;
			}

			if (inTag) {
				// 拼接表情字符串
				buffer.append(c);
				// 计算表情字符串的长度
				tagLength++;

				// 遍历到表情的结束标签“]”
				if (c.equals(EmojiUtil.EMOJI_END_CHAR)) {
					// 设置标识为false，表示表情字符串拼装完成
					inTag = false;

					String tag = buffer.toString();
					// 计算表情字符串结束的位置
					int tagEnd = tagStartPosition + tagLength;
					// 删除字符串中多余的“[”
					int lastIndex = tag.lastIndexOf(EmojiUtil.EMOJI_START_CHAR);
					if (lastIndex > 0) {
						tagStartPosition = tagStartPosition + lastIndex;
						tag = tag.substring(lastIndex, tag.length());
					}
					// 获取静态表情图片
					ImageSpan imageSpan = getImageSpan(tag);
					// 使用静态表情图片替代表情字符串
					if (imageSpan != null) {
						spannable.setSpan(imageSpan, tagStartPosition, tagEnd,
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
			position++;
		} while (position < length);
	}

	/**
	 * 根据表情字符串获取静态表情
	 */
	private ImageSpan getImageSpan(String content) {
		// 根据表情字符串获得表情的后缀编码
		String idStr = EmojiUtil.getInstance().getFaceId(content);
		// 根据表情字符串获得静态表情的资源ID
		Resources resources = getContext().getResources();
		int id = resources.getIdentifier(EmojiUtil.STATIC_FACE_PREFIX + idStr,
				"drawable", getContext().getPackageName());
		if (id > 0) {
			try {
				// 计算图片的宽高
				int size = Math.round(getTextSize());
				// 根据静态表情的资源ID获得静态表情图片
				Drawable emoji = getContext().getResources().getDrawable(id);
				// 设置图片的绘制范围
				emoji.setBounds(0, 0, size, size);
				// 创建可以替代文字的ImageSpan
				ImageSpan imageSpan = new ImageSpan(emoji,
						ImageSpan.ALIGN_BOTTOM);
				return imageSpan;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
