package com.haoqi.yungou.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.haoqi.yungou.R;


/**
 * 
 * @author ZJJ
 * 
 */
public class AddAndSubView extends LinearLayout
{
	Context context;
	OnNumChangeListener onNumChangeListener;
	ImageButton addButton;
	ImageButton subButton;
	EditText editText;
	int num; // editText中的数值

	public AddAndSubView(Context context)
	{
		super(context);
		this.context = context;
		num = 0;
		control(context);
	}

	/**
	 * 带初始数据实例化
	 * 
	 * @param context
	 */
	public AddAndSubView(Context context, int num)
	{
		super(context);
		this.context = context;
		this.num = num;
		control(context);
	}

	/**
	 * 从XML中实例化
	 */
	public AddAndSubView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		num = 0;
		control(context);
	}

	/**
	 *
	 * @param context
	 */
	private void control(Context context)
	{
		initialise(context); // 实例化内部view
		setViewListener();
	}

	/**
	 * 实例化内部View
	 * @param context
	 */
	private void initialise(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view=inflater.inflate(R.layout.dialog_editcart,this);
		 subButton = (ImageButton)view.findViewById(R.id.btn_decrease);
		 addButton = (ImageButton)view.findViewById(R.id.btn_increase);
		editText = (EditText)view.findViewById(R.id.et_count);

//		addButton.setText("+");
//		subButton.setText("-");
		addButton.setTag("+");
		subButton.setTag("-");
		// 设置输入类型为数字
		editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
		editText.setText(String.valueOf(num));
	}

	public void setMax(int mMaxTextNum) {
		editText.setFilters(new InputFilter[] { new InputFilterMinMax(1,mMaxTextNum) });
	}

	public class InputFilterMinMax implements InputFilter {

		private int min, max;

		public InputFilterMinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public InputFilterMinMax(String min, String max) {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString() + source.toString());
				if (isInRange(min, max, input)) {
					return null;
				}else{
					editText.setText(max+"");
					return "";
				}
			} catch (NumberFormatException nfe) {

			}
			return "";
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}
	/**
	 * 设置editText中的值
	 * 
	 * @param num
	 */
	public void setNum(int num)
	{
		this.num = num;
		editText.setText(String.valueOf(num));
	}

	/**
	 * 获取editText中的值
	 * 
	 * @return
	 */
	public int getNum()
	{
		if (editText.getText().toString() != null)
		{
			return Integer.parseInt(editText.getText().toString());
		} else
		{
			return 0;
		}
	}




	
	/**
	 * 设置输入框中的字体大小
	 * @param spValue 字体大小SP
	 */
	public void setTextSize(int spValue)
	{
		editText.setTextSize(spValue);
	}

	/**
	 * 以Drawable形式 设置按钮背景图
	 * 
	 * @param addBtnDrawable
	 *            加号背景图
	 * @param subBtnDrawable
	 *            减号背景图
	 */
	public void setButtonBgDrawable(Drawable addBtnDrawable,
			Drawable subBtnDrawable)
	{
		// 不推荐用setBackgroundDrawable，新API推荐用setBackground（在API 16中）
		addButton.setBackgroundDrawable(addBtnDrawable);
		subButton.setBackgroundDrawable(subBtnDrawable);
//		addButton.setText("");
//		subButton.setText("");
	}

	/**
	 * 以资源Resource形式 设置按钮背景图
	 * 
	 * @param addBtnResource
	 *            加号背景图
	 * @param subBtnResource
	 *            减号背景图
	 */
	public void setButtonBgResource(int addBtnResource, int subBtnResource)
	{
		addButton.setBackgroundResource(addBtnResource);
		subButton.setBackgroundResource(subBtnResource);
	}


	/**
	 * 设置按钮背景色
	 * 
	 * @param addBtnColor
	 *            加号背景色
	 * @param subBtnColor
	 *            减号背景色
	 */
	public void setButtonBgColor(int addBtnColor, int subBtnColor)
	{
		addButton.setBackgroundColor(addBtnColor);
		subButton.setBackgroundColor(subBtnColor);
	}

	/**
	 * 设置EditText文本变化监听
	 * 
	 * @param onNumChangeListener
	 */
	public void setOnNumChangeListener(OnNumChangeListener onNumChangeListener)
	{
		this.onNumChangeListener = onNumChangeListener;
	}

	/**
	 * 设置文本变化相关监听事件
	 */
	private void setViewListener()
	{
		addButton.setOnClickListener(new OnButtonClickListener());
		subButton.setOnClickListener(new OnButtonClickListener());
		editText.addTextChangedListener(new OnTextChangeListener());
	}

	/**
	 * 加减按钮事件监听器
	 * 
	 * @author ZJJ
	 * 
	 */
	class OnButtonClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			String numString = editText.getText().toString();
			if (numString == null || numString.equals(""))
			{
				num = 1;
				editText.setText("1");
			} else
			{
				if (v.getTag().equals("+"))
				{
					if (++num < 1) // 先加，再判断
					{
						num--;
						Toast.makeText(context, "亲，数量至少为1哦~",
								Toast.LENGTH_SHORT).show();
						editText.setText("1");
					} else
					{
						editText.setText(String.valueOf(num));

						if (onNumChangeListener != null)
						{
							onNumChangeListener.onNumChange(AddAndSubView.this,
									num);
						}
					}
				} else if (v.getTag().equals("-"))
				{
					if (--num < 1) // 先减，再判断
					{
						num++;
						Toast.makeText(context, "亲，数量至少为1哦~",
								Toast.LENGTH_SHORT).show();
						editText.setText("1");
					} else
					{
						editText.setText(String.valueOf(num));
						if (onNumChangeListener != null)
						{
							onNumChangeListener.onNumChange(AddAndSubView.this,
									num);
						}
					}
				}
			}
		}
	}

	/**
	 * EditText输入变化事件监听器
	 * 
	 * @author ZJJ
	 * 
	 */
	class OnTextChangeListener implements TextWatcher
	{

		@Override
		public void afterTextChanged(Editable s)
		{
			String numString = s.toString();
			if (numString == null || numString.equals(""))
			{
				num = 1;
				if (onNumChangeListener != null)
				{
					onNumChangeListener.onNumChange(AddAndSubView.this, num);
				}
			} else
			{
				int numInt = Integer.parseInt(numString);
				if (numInt < 1)
				{
					Toast.makeText(context, "亲，数量至少为1哦~",
							Toast.LENGTH_SHORT).show();
					editText.setText("1");
				} else
				{
					// 设置EditText光标位置 为文本末端
					editText.setSelection(editText.getText().toString()
							.length());
					num = numInt;
					if (onNumChangeListener != null)
					{
						onNumChangeListener
								.onNumChange(AddAndSubView.this, num);
					}
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after)
		{

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count)
		{

		}

	}

	public interface OnNumChangeListener
	{
		/**
		 * 输入框中的数值改变事件
		 * 
		 * @param view
		 *            整个AddAndSubView
		 * @param num
		 *            输入框的数值
		 */
		public void onNumChange(View view, int num);
	}

}
