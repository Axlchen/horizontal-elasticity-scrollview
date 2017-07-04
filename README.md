# horizontal-elasticity-scrollview

仿豆瓣首页的横向弹性滑动的控件

### 效果展示

- 滑动到最左边并继续滑动后松手

![situation1](https://github.com/Axlchen/horizontal-elasticity-scrollview/blob/master/art/2017-07-04%2020_39_52.gif)

- 滑动到左边，继续滑动后恢复

![situation2](art/2017-07-04 21_22_54.gif)

- 滑动到最右边，继续滑动到一定距离，产生动作

![situation3](art/2017-07-04 21_24_24.gif)

### 使用方法
继承自[HorizontalScrollview](https://developer.android.google.cn/reference/android/widget/HorizontalScrollView.html)，所以用法基本一致

```xml
<xyz.axlchen.horizontaloverscrollview.HorizontalOverScrollView
			        android:layout_width="match_parent"
			        android:id="@+id/horizontal_over_scroll_view"
			        android:layout_height="wrap_content"
			        android:overScrollMode="never"
			        android:scrollbars="none"
			        app:ripple_color="#ccc"
			        android:layout_centerInParent="true">
			
			        <include layout="@layout/item_view"/>

<xyz.axlchen.horizontaloverscrollview.HorizontalOverScrollView>
```
