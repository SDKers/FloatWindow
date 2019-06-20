#  FloatWindowUpdate

> 致歉:
>    近期因为公司业务,一直在帮客户解决问题。一直没大面积维护，欢迎大家PR，一起加入维护
>> 近期工作要点:
>> 1. 权限判断和验证部分,兼容更多的平台和设备
>> 2. 权限、悬浮窗、管理三部分解耦
>> 如有想加入维护的可以私聊


## 特性：
------

 1. 支持拖动，提供自动贴边等动画

 2. 内部自动进行权限申请操作

 3. 可自由指定要显示悬浮窗的界面

 4. 应用退到后台时，悬浮窗会自动隐藏

 5. 除小米外，4.4~7.0 无需权限申请

 6. 位置及宽高可设置百分比值，轻松适配各分辨率

 7. 链式调用，简洁清爽


## 开发工具集成指南:
------

### 1. 编译

根据平台执行编译指令即可

    * Windows: gradlew.bat release
    * Linux/Mac: gradlew release

推荐使用shell编译(linux/mac终端,window建议cmder):
   `sh build.sh`
   输出在跟目录下的release目录中

### 2. 使用

* `Android studio`集成，直接使用编译成的aar包即可.AAR包路径 `floatwindow/build/outputs/aar/`

* `eclipse`集成相对麻烦些: 将AAR解压开，将`classes.jar`修改名字拷贝到项目`libs`中;并在权限配置中集成权限和申请权限的页面即可

``` xml
<manifest>
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <....../>
     <application>
         <....../>
         <activity
             android:name="com.yhao.floatwindow.FloatActivity"
             android:configChanges="keyboardHidden|orientation|screenSize"
             android:launchMode="standard"
             android:windowSoftInputMode="stateHidden|stateAlwaysHidden" />
     
     </application>
</manifest>
```



##  具体使用方法
======


**0.声明权限**

> 非必要，如果想兼容更多使用场景，建议集成.

``` xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
```


**1.创建悬浮控件**

``` java
FloatWindow
      .with(getApplicationContext())
      .setView(view)
      .build();

```

setView 方法可设置 View 子类或 xml 布局。

**2.设置宽高及显示位置**

``` java
FloatWindow
      .with(getApplicationContext())
      .setView(view)
      .setWidth(100)                   //100px
      .setHeight(Screen.width,0.2f)    //屏幕宽度的 20%
      .setX(100)                       //100px
      .setY(Screen.height,0.3f)        //屏幕高度的 30%
      .build();
```

可设置具体数值或屏幕宽/高百分比，默认宽高为 wrap_content；默认位置为屏幕左上角，x、y 为偏移量。


**3.指定界面显示**

``` java
FloatWindow
      .with(getApplicationContext())
      .setView(view)
      .setFilter(true, A_Activity.class, C_Activity.class)
      .build();

```
此方法表示 A_Activity、C_Activity 显示悬浮窗，其他界面隐藏。

``` java
.setFilter(false, B_Activity.class)
```
此方法表示 B_Activity 隐藏悬浮窗，其他界面显示。

注意：setFilter 方法参数可以识别该 Activity 的子类

也就是说，如果 A_Activity、C_Activity 继承自 BaseActivity，你可以这样设置：

``` java
    .setFilter(true, BaseActivity.class)
```

**4.桌面显示**

``` java
FloatWindow
      .with(getApplicationContext())
      .setView(view)
      .setDesktopShow(true)                //默认 false
      .build();

```

**5.可拖动悬浮窗**

``` java
FloatWindow
      .with(getApplicationContext())
      .setView(view)
      .setMoveType(MoveType.slide)         //可拖动，释放后自动贴边
      .build();

```

共提供 4 种 MoveType :

MoveType.SLIDE       : 可拖动，释放后自动贴边 （默认）

MoveType.BACK        : 可拖动，释放后自动回到原位置

MoveType.ACTIVE      : 可拖动

MoveType.INACTIVE    : 不可拖动


**6.悬浮窗动画**

``` java
FloatWindow
      .with(getApplicationContext())
      .setView(view)
      .setMoveType(MoveType.slide)
      .setMoveStyle(500, new AccelerateInterpolator())  //贴边动画时长为500ms，加速插值器
      .build();

```

自定义动画效果，只在 MoveType.slide 或 MoveType.back 模式下设置此项才有意义。默认减速插值器，默认动画时长为 300ms。


**7.后续操作**

``` java
//手动控制
FloatWindow.get().show();
FloatWindow.get().hide();

//修改显示位置
FloatWindow.get().updateX(100);
FloatWindow.get().updateY(100);

//销毁
FloatWindow.destroy();

```

以上操作应待悬浮窗初始化后进行。


**8.多个悬浮窗**

``` java

FloatWindow
        .with(getApplicationContext())
        .setView(imageView)
        .build();

FloatWindow
        .with(getApplicationContext())
        .setView(button)
        .setTag("new")
        .build();


FloatWindow.get("new").show();
FloatWindow.get("new").hide();
FloatWindow.destroy("new");

```

创建第一个悬浮窗不需加 tag，之后再创建就需指定唯一 tag ，以此区分，方便进行后续操作。

