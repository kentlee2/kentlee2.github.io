---
layout: post
title:   Android中AIDL的基本用法
date:   2017-6-8 11:07:24 +0800
categories: 转载
tag: 教程
---

* content
{:toc}

早些时候就听说过**AIDL**，也常在各种Android面试题、教程甚至大牛采访中看到过它的身影。可见AIDL在Android开发中的地位十分的重要。

于是决定先从AIDL的一些基本概念和基本用法开始着手学习它，下面是一些整理的笔记。

AIDL的全称为Android Interface Definition Language, 顾名思义，它主要就是用来定义接口的一种语言:

AIDL (Android Interface Definition Language) is similar to other IDLs you might have worked with. It allows you to define the programming interface that both the client and service agree upon in order to communicate with each other using interprocess communication (IPC). On Android, one process cannot normally access the memory of another process. So to talk, they need to decompose their objects into primitives that the operating system can understand, and marshall the objects across that boundary for you. The code to do that marshalling is tedious to write, so Android handles it for you with AIDL.
Android Developer的官方文档中对AIDL做了很好的概括。当作为客户的一方和要和作为服务器的一方进行通信时，需要指定一些双方都认可的接口，
这样才能顺利地进行通信。而AIDL就是定义这些接口的一种工具。为什么要借助AIDL来定义，而不直接编写接口呢（比如直接通过Java定义一个Interface）？
这里涉及到进程间通信（IPC）的问题。和大多数系统一样，在Android平台下,各个进程都占有一块自己独有的内存空间，各个进程在通常情况下只能访问自己的独有的内存空间，而不能对别的进程的内存空间进行访问。
进程之间如果要进行通信，就必须先把需要传递的对象分解成操作系统能够理解的基本类型，并根据你的需要封装跨边界的对象。而要完成这些封装工作，需要写的代码量十分地冗长而枯燥。因此Android提供了AIDL来帮助你完成这些工作。

从AIDL的功能来看，它主要的应用场景就是IPC。虽然同一个进程中的client-service也能够通过AIDL定义接口来进行通信，但这并没有发挥AIDL的主要功能。
概括来说：

如果不需要IPC，那就直接实现通过继承Binder类来实现客户端和服务端之间的通信。
如果确实需要IPC，但是无需处理多线程，那么就应该通过Messenger来实现。Messenger保证了消息是串行处理的，其内部其实也是通过AIDL来实现。
在有IPC需求，同时服务端需要并发处理多个请求的时候，使用AIDL才是必要的
在了解了基本的概念和使用场景之后，使用AIDL的基本步骤如下：

编写.AIDL文件，定义需要的接口
实现定义的接口
将接口暴露给客户端调用
下面通过实现一个简单的远程Bound Service来练习这几个步骤：

# 1. 编写.AIDL文件，定义需要的接口 #

在Android Studio下，右键src文件夹，选择新建AIDL文件，并填写名字，这里我命名为   `IRemoteService`
 
![](http://i.imgur.com/99x4CIZ.png)

点击Finish按钮之后，会发现main下多了一个名字为AIDL的目录，目录下的包名和Java的包名保持一致，包下即是新建的`IRemoteService.aidl`文件。
内容我们编写如下：

    // IRemoteService.aidl
    package learn.android.kangel.learning;
    // Declare any non-default types here with import statements
    import learn.android.kangel.learning.HelloMsg;

    interface IRemoteService {

        HelloMsg sayHello();
    }
AIDL的写法和Java十分类似，这里我定义了一个`sayHello()`方法，用来获取一个从服务端返回的消息`HelloMsg`。
这里的HelloMsg是我自己定义的一个类型。默认情况下，AIDL支持下列所述的数据类型:

所有的基本类型（int、float等）

## String 
## CharSequence
## List
## Map

其中，List和Map中的元素类型必须是上述类型之一或者由其他AIDL生成的接口类型，或者是已经声明的Pacelable类型。
List类型可以指定泛型类，比如写成List<String>, 并且对方接收到的具体实例都是ArrayList
Map类型不支持指定泛型类，比如Map<String,String>。只能Map表示类型，并且对方接收到的具体实例都是HashMap

在这个`IRemoteService`例子中，我们希望在进程间传递一个`HelloMsg`对象：他的定义如下：

    /*HelloMsg.java*/
    public class HelloMsg {
        private String msg;
        private int pid;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public HelloMsg(String msg, int pid) {
            this.msg = msg;
            this.pid = pid;
        }
    }
为了让`HelloMsg`能够在进程间传递， 它必须实现`Parcelable`接口，Parcelable是Android提供的一种序列化方式，如果嫌手写麻烦的话，通过插件我们可以十分快捷为现有的类添加Parcelable实现：

    /*HelloMsg.java*/
    import android.os.Parcel;
    import android.os.Parcelable;

    public class HelloMsg implements Parcelable {
        private String msg;
        private int pid;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public HelloMsg(String msg, int pid) {

            this.msg = msg;
            this.pid = pid;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.msg);
            dest.writeInt(this.pid);
        }

        protected HelloMsg(Parcel in) {
            this.msg = in.readString();
            this.pid = in.readInt();
        }

        public static final Parcelable.Creator<HelloMsg> CREATOR = new Parcelable.Creator<HelloMsg>() {
            @Override
            public HelloMsg createFromParcel(Parcel source) {
                return new HelloMsg(source);
            }

            @Override
            public HelloMsg[] newArray(int size) {
                return new HelloMsg[size];
            }
        };
    }
定义好`HelloMsg.java`之后，还需要新增一个与其同名的**AIDL**文件。那么同样按照刚才的步骤右键**src**文件夹，添加一个名为HelloMsg的AIDL文件。
这个AIDL的编写十分简单，只需要简单的声明一下要用到的Pacelable类即可，有点类似C语言的头文件，这个AIDL文件是不参与编译的：

    // HelloMsg.aidl
    package learn.android.kangel.learning;
    parcelable HelloMsg;

注意到**parcelable**的首字母是小写的，这算是AIDL一个特殊的地方。
接下来还需要再`IRemoteService.aidl`文件中使用import关键字导入这个HelloMsg类型。详细的写法参考上面的`IRemoteService.aidl`代码。
即便IRemoteService.aidl和HelloMsg.aidl位于同一个包下，这里的import是必须要有的。这也是AIDL一个特殊的地方。

好了，至此编写.AIDL文件的步骤就基本结束了，这个时候需要make project或者make对应的module，Android SDK就会根据我这里编写的.AIDL文件生成对应的Java文件。
在Android Studio下，可以在**build/generated/aidl**目录下找到这些Java文件。

查看`IRemoteService.java`，可以看到其内部有一个静态抽象类**Stub**,这个**Stub**继承自**Binder**类，并抽象实现了其父接口，这里对应的是`IRemoteService`这个接口：

`public static abstract class Stub extends android.os.Binder implements` `learn.android.kangel.learning.IRemoteService`
**Stub**类除了声明了`IRemoteService.aidl`中的所有方法，还提供了一些有用的**helper**方法，比如**asInterface():**
`
public static learn.android.kangel.learning.IRemoteService asInterface(android.os.IBinder obj)`
这个方法接受一个Binder对象，并将其转化成Stub对应的接口对象（也就是这里的IRemoteService）并返回。

对于这些生成的Java文件的进一步研究和学习可以帮助我们更好地理解Android的Binder。

# 2. 实现定义的接口 #

要实现定义的接口，只需要继承自生成的**Binder**类，并实现其中的方法即可:

    IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public HelloMsg sayHello() throws RemoteException {
            return new HelloMsg("msg from service at Thread " + Thread.currentThread().toString() + "\n" +
                    "tid is " + Thread.currentThread().getId() + "\n" +
                    "main thread id is " + getMainLooper().getThread().getId(), Process.myPid());
        }
    };
这里的实现十分简单，返回一个`HelloMsg`,消息部分是当前线程的信息，当前线程的id，以及主线程的id，Process Id部分就是当前进程的Id

# 3. 将接口暴露给客户端调用 #



需要注意一点，如果希望多个Application都能够通过这个接口与服务端通信，那么所有使用这个接口的Application的src目录下都要有对应.aidl文件的副本。

在这个例子中我们编写一个名为`RemoteService`的**Service**类，并在onBind()方法中返回上述第二步中实现的接口,这样就把接口传给了客户端供其调用:

    package learn.android.kangel.learning;
    import android.app.Service;
    import android.content.Intent;
    import android.os.Binder;
    import android.os.IBinder;
    import android.os.Process;
    import android.os.RemoteException;
    import android.support.annotation.Nullable;
    import android.widget.Toast;

    public class RemoteService extends Service {

    IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public HelloMsg sayHello() throws RemoteException {
            return new HelloMsg("msg from service at Thread " + Thread.currentThread().toString() + "\n" +
                    "tid is " + Thread.currentThread().getId() + "\n" +
                    "main thread id is " + getMainLooper().getThread().getId(), Process.myPid());
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    }
以上三步完成之后，我们来继续完善这个例子来进行一些测试：

编写作为客户端的Activity：

    import android.content.ComponentName;
    import android.content.Intent;
    import android.content.ServiceConnection;
    import android.os.Bundle;
    import android.os.IBinder;
    import android.os.Process;
    import android.os.RemoteException;
    import android.support.annotation.Nullable;
    import android.support.v7.app.AppCompatActivity;
    import android.view.View;
    import android.widget.TextView;
    import android.widget.Toast;


    public class ClientActivity extends AppCompatActivity {
    private IRemoteService mRemoteService = null;
    private boolean mBind = false;
    private TextView mPidText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_client);
        mPidText = (TextView) findViewById(R.id.my_pid_text_view);
        mPidText.setText("the client pid is " + Process.myPid());
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, RemoteService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        mBind = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = IRemoteService.Stub.asInterface(service);
            mBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteService = null;
            mBind = false;
        }
    };

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.show_pid_button:
                if (mBind) {
                    try {
                        Log.i("HELLO_MSG", "the service pid is " + mRemoteService.sayHello().getPid());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.say_hello_button:
                if (mBind) {
                    try {
                        Log.i("HELLO_MSG", mRemoteService.sayHello().getMsg());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }
}
布局文件中有两个**Button**和一个**TextView**，Button的点击事件都在xml文件中完成了注册。分别用来获取服务端返回的Pid和返回的Msg。
TextView用于展示当前Activity所在线程的id。

在**onServiceConnected()**回调中，我们使用**IRemoteService.Stub.asInterface(Binder)**方法返回我们的接口的引用。接着客户端就可以通过它来对服务端发送请求了。
onButtonClick()方法中就是对接口的调用。

如果客户端和服务端处于同一个进程，onServiceConnected()回调中，是可以通过强制类型转换将返回的Binder对象转换为我们需要的接口对象的，像这样：

mRemoteService = (IRemoteService) service;
但如果客户端和服务端处于不同进程，执行这样的强转，系统会报错：

java.lang.ClassCastException: android.os.BinderProxy cannot be cast to learn.android.kangel.learning.IRemoteService
我的对此理解是，由于不同进程之间的内存空间是不能够互相访问的，A进程中的对象当然也就不能为B进程所理解。因此强制类型转换只适用于同一个进程中。

在Manifest中声明作为服务端的Service和作为客户端的Acticity

    <activity android:name=".ClientActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
    </activity>
    <service
            android:name=".RemoteService"
            android:process=":remote" />
在这里我为**RemoteService**设置了**proces**s属性，让它运行在与默认进程不同的进程中。
接下来运行我们的应用：

![](http://upload-images.jianshu.io/upload_images/2520853-fa8fe9130029e841.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看到客户端进程id为31704
尝试点击两个按钮，查看Log：
![](http://upload-images.jianshu.io/upload_images/2520853-122e1aa898f5e334.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看到**服务端的进程id为31720**，不同于客户端进程。
而且可以看到，**service所在的主线程id为1，而处理该请求的线程id为4621**。

来自远程进程的调用分发自系统为你的进程所维持的一个线程池中。这也许有点难理解。假如你通过AIDL实现了一个远程服务端的接口，然后有另外一个客户端进程调用了该接口中的方法，因为客户端和你所实现的服务端处于两个不同的进程，
因此客户端对于你而言，就是一个远程进程。当客户端对接口进行调用时，调用过程并不是由客户端进程进行处理的。而是由系统进行封装后，传递到服务端进程所持有的一个线程池中进行处理。最终线程池中的其中一个线程会被用来执行调用的具体逻辑。
而具体选择哪个线程来进行处理，是无法提前预知的。
因此作为服务端接口的实现者，应该能够处理多线程并发的情况，时刻准备好处理来自未知线程的调用，并能保证AIDL接口的实现是线程安全的。

如果服务端和客户端处于同一个进程，那么服务端将会在与发起请求的客户端所处的相同线程上处理该请求。把上述**android:process=":remote"**属性去掉，则可以对其进行验证。
但这种单进程的情况，AIDL的使用实际上是完全没必要的。