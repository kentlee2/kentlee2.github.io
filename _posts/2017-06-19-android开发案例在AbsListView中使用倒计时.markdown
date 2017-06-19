
---
layout: post
title:  在AbsListView中使用倒计时
date:   2017-6-19 18:08:45 +0800
categories: 原创
tag: 教程
---

* content
{:toc}
之前做云购项目遇到了倒计时的需求，当时用了很多办法也无法解决在AbsListView中倒计时复用的问题，卡顿、倒计时错误等问题无法解决，后来再参考了一些大神的博客后得到了一些启发，自己根据项目的需求进行了改进，所以就在此分享一下我的解决方案。

在很多App中, 有多种多样的倒计时需求, 比如:

在单View上, 使用倒计时, 如(如图-1)
![](http://i.imgur.com/8W48HL0.png)

在ListView(或者GridView)的ItemView上, 使用倒计时(如图-2)
![图2](http://i.imgur.com/GiPlSSM.png)



需求1有很多倒计时的方法，比较常用的计时方法是使用Android原生的CountDownTimer类 。相比需求-1,需求-2的难度更大, 性能要求更高: 因为AbsListView会涉及到ItemView重用的问题会使得管理定时器很麻烦, 另外如果定时地通过Base#notifyDataChanged()去刷新数据, 性能又相对较低, 也会引起滚动卡顿的问题. 因此, 此文主要解决的问题是如何合理地在AbsListView中使用倒计时.

**基本思路**
1.使用CountDownTimer来完成基本倒计时功能

2.按倒计时的时间间隔来分组管理CountDownTimer, 即相同时间间隔的Item使用同一个CountDownTimer

3.每组CountDownTimer倒计时的时间取组内的最大值, 一旦Item到达自身的倒计时时间, 就会从该组倒计时中被移除

4.定义一个倒计时任务, 用来管理上述分组

5.每个业务可以根据需要创建并启动多个倒计时任务, 且可以在适当的页面生命周期函数中停止该任务
代码片段：

用一个计时器map来管理计时任务：
![](http://i.imgur.com/OP7T1BB.png)
计时或停止计时核心代码：
![](http://i.imgur.com/qdNN1Gb.png)

创建一个CountDownTask的实例

CountDownTask countDownTask = CountDownTask.create()

倒计时时间

long targetMillis = CountDownTask.elapsedRealtime() + 1000 * 60;
final int CD_INTERVAL = 1000;



final int CD_INTERVAL = 1000;


countDownTask.until(textView, targetMillis,CD_INTERVAL, 
 new OnCountDownListener() {

 @Override
    
public void onTick(View view, long millisUntilFinished) {
        
((TextView)view).setText(String.valueOf(millisUntilFinished / CD_INTERVAL));
    
}
    
@Override
    
public void onFinish(View view) {
        
((TextView)view).setText("DONE.");
    
}
});



具体使用请看
[https://github.com/kentlee2/CountDownTask](https://github.com/kentlee2/CountDownTask)
