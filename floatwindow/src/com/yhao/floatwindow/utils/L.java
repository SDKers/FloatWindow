package com.yhao.floatwindow.utils;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

/**
 * @Copyright © 2015 sanbo Inc. All rights reserved.
 * @Description
 * 
 *              <pre>
 * Log统一管理类,提供功能：
 * 1.log工具类支持全部打印   「支持Log的所有功能.」
 * 2.支持类似C的格式化输出或Java的String.format「%个数和参数个数需要一直才能格式化」
 * 3.支持Java堆栈打印
 * 4.支持键入和不键入TAG  「不键入tag,tag是sanbo，默认第一个参数String为tag」
 * 5.支持shell控制log是否打印.
 *          tag为sanbo的控制命令：setprop log.tag.sanbo log等级.
 *          log等级：VERBOSE/DEBUG/INFO/WARN/ERROR/ASSERT
 * 6.格式化输出.
 * 7.支持XML/JSON/Map/Array等更多对象打印
 *              </pre>
 * 
 * @Version: 6.1
 * @Create: 2015年6月18日 下午4:14:01
 * @Author: sanbo
 */
public class L {

    private L() {}

    private static final int JSON_INDENT = 2;
    // 是否打印bug.建议在application中调用init接口初始化
    public static boolean USER_DEBUG = true;
    // 是否接受shell控制打印
    private static boolean isShellControl = true;
    // 是否打印详细log,详细打印调用的堆栈
    private static boolean isNeedCallstackInfo = false;
    // 是否按照条形框输出,有包裹域的输出
    private static boolean isNeedWrapper = false;
    // 是否格式化展示,主要针对JSON
    private static boolean isFormat = false;

    // 默认tag
    private static String DEFAULT_TAG = "SFloatWindow";
    // 临时tag.用法：调用log中大于1个参数,且第一个参数为字符串,且不是format用法,字符串长度没超过协议值,此时启用临时tag
    private static String TEMP_TAG = "";
    // 规定每段显示的长度.每行最大日志长度 (Android Studio3.1最多2902字符)
    private static int LOG_MAXLENGTH = 2900;

    // 解析属性最大层级
    public static final int MAX_CHILD_LEVEL = 3;
    // 换行符
    public static final String BR = System.getProperty("line.separator");
    // 类名(getClassName).方法名(getMethodName)[行号(getLineNumber)]
    private static String content_simple_callstack = "简易调用堆栈: %s.%s[%d]";

    // 格式化时，行首封闭符
    private static String CONTENT_LINE = "║ ";
    // 空格
    private static String CONTENT_SPACE = "  ";
    private static String CONTENT_LOG_INFO = "log info:";
    private static String CONTENT_LOG_EMPTY = "打印的日志信息为空!";

    private static String content_title_begin =
        "╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════";
    private static String content_title_info_callstack =
        "╔══════════════════════════════════════════════════════════════调用详情══════════════════════════════════════════════════════════════";
    private static String content_title_info_log =
        "╔══════════════════════════════════════════════════════════════日志详情══════════════════════════════════════════════════════════════";
    private static String content_title_info_error =
        "╔══════════════════════════════════════════════════════════════异常详情══════════════════════════════════════════════════════════════";
    private static String content_title_info_type = "╔════════════════════════════════════════════════════「%s"
        + "」════════════════════════════════════════════════════";
    private static String content_title_end =
        "╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════";

    /**
     * 行首为该符号时，不增加行首封闭符
     */
    private static String CONTENT_A = CONTENT_LINE;
    private static String CONTENT_B = "╔";
    private static String CONTENT_C = "╚";
    private static String CONTENT_D = " ╔";
    private static String CONTENT_E = " ╚";

    private static String CONTENT_WARNNING_SHELL =
        "Wranning....不够打印级别,请在命令行设置指令后重新尝试打印,命令行指令: adb shell setprop log.tag." + DEFAULT_TAG + " ";

    public static final class MLEVEL {
        public static final int VERBOSE = 0x1;
        public static final int DEBUG = 0x2;
        public static final int INFO = 0x3;
        public static final int WARN = 0x4;
        public static final int ERROR = 0x5;
        public static final int WTF = 0x6;
    }

    /**
     * 初始化接口
     *
     * @param showLog 是否展示log，默认展示
     * @param shellControl 是否使用shell控制log动态打印.默认不使用. shell设置方式：setprop log.tag.sanbo INFO
     *        最后一个参数为log等级,可选项目：VERBOSE/DEBUG/INFO/WARN/ERROR/ASSERT
     * @param needWarpper 是否需要格式化输出
     * @param needCallStackInfo 是否需要打印详细的堆栈调用信息.
     * @param format 是否需要格式化.
     * @param defaultTag android logcat的tag一个意义,不设置默认的tag为"sanbo"
     */
    public static void init(boolean showLog, boolean shellControl, boolean needWarpper, boolean needCallStackInfo,
        boolean format, String defaultTag) {
        USER_DEBUG = showLog;
        isShellControl = shellControl;
        isNeedWrapper = needWarpper;
        isNeedCallstackInfo = needCallStackInfo;
        isFormat = format;
        if (!TextUtils.isEmpty(defaultTag)) {
            DEFAULT_TAG = defaultTag;
        }
    }

    /*********************************************************************************************************/
    /**
     * 支持可变参数打印,根据不同的结构支持. 可以统一成一个接口
     */
    /*********************************************************************************************************/
    public static void v(Object... args) {
        if (isShellControl) {
            if (!Log.isLoggable(DEFAULT_TAG, Log.VERBOSE)) {
                Log.v(DEFAULT_TAG, CONTENT_WARNNING_SHELL + "VERBOSE");
                return;
            }
        }
        parserArgsMain(MLEVEL.VERBOSE, args);
    }

    public static void d(Object... args) {
        if (isShellControl) {
            if (!Log.isLoggable(DEFAULT_TAG, Log.DEBUG)) {
                Log.d(DEFAULT_TAG, CONTENT_WARNNING_SHELL + "DEBUG");
                return;
            }
        }
        parserArgsMain(MLEVEL.DEBUG, args);
    }

    public static void i(Object... args) {
        if (isShellControl) {
            if (!Log.isLoggable(DEFAULT_TAG, Log.INFO)) {
                Log.i(DEFAULT_TAG, CONTENT_WARNNING_SHELL + "INFO");
                return;
            }
        }
        parserArgsMain(MLEVEL.INFO, args);
    }

    public static void w(Object... args) {
        if (isShellControl) {
            if (!Log.isLoggable(DEFAULT_TAG, Log.WARN)) {
                Log.w(DEFAULT_TAG, CONTENT_WARNNING_SHELL + "WARN");
                return;
            }
        }
        parserArgsMain(MLEVEL.WARN, args);
    }

    public static void e(Object... args) {
        if (isShellControl) {
            if (!Log.isLoggable(DEFAULT_TAG, Log.ERROR)) {
                Log.e(DEFAULT_TAG, CONTENT_WARNNING_SHELL + "ERROR");
                return;
            }
        }
        parserArgsMain(MLEVEL.ERROR, args);
    }

    public static void wtf(Object... args) {
        if (isShellControl) {
            if (!Log.isLoggable(DEFAULT_TAG, Log.ASSERT)) {
                Log.wtf(DEFAULT_TAG, CONTENT_WARNNING_SHELL + "ASSERT");
                return;
            }
        }
        parserArgsMain(MLEVEL.WTF, args);
    }

    private static Character FORMATER = '%';

    /**
     * 解析参数入口.这步骤开始忽略类型.解析所有参数,参数检查逻辑： 1.是否为String,若为String,则先判断是否格式化输出,不是再进行字符串转换格式尝试 2.对象其他类型判断:
     * StringBuffer>StringBuild>Throwable>Intent>List>Map
     *
     * @param level
     * @param args
     */
    private static void parserArgsMain(int level, Object[] args) {

        /*
         * 确认打印
         */
        if (!USER_DEBUG) {
            Log.e(DEFAULT_TAG, "请确认Log工具类已经设置打印!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        // 开始

        if (isFormat) {
            sb.append(CONTENT_LOG_INFO).append("\n");
        }
        String stackinfo = getCallStaceInfo();
        if (!TextUtils.isEmpty(stackinfo)) {
            sb.append(stackinfo).append("\n");
        }

        if (args[0] instanceof String) {
            // if (isNeedWrapper) {
            // sb.append(content_title_info_log).append("\n");
            // }
            String one = (String)args[0];
            // 解析fromat
            if (one.contains(String.valueOf(FORMATER)) && args.length > 1) {

                /*
                 * 参数解析
                 */
                Object[] temp = new Object[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    temp[i - 1] = args[i];
                }

                // 查找%个数
                Pattern p = Pattern.compile("%", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(one);
                int count = 0;
                while (m.find()) {
                    count++;
                }

                /**
                 * %和后面参数一样，则格式化，否则不进行格式化
                 */
                if (count == temp.length) {
                    // 格式化操作
                    String log = String.format(Locale.getDefault(), one, temp);
                    if (isNeedWrapper) {
                        sb.append(content_title_info_log).append("\n");
                    }
                    sb.append(wrapperString(log)).append("\n");
                } else {
                    if (isNeedWrapper) {
                        sb.append(content_title_info_log).append("\n");
                    }
                    StringBuilder tempSB = new StringBuilder();
                    for (Object obj : args) {
                        // 解析成字符串,添加
                        String tempStr = objectToString(obj);
                        // Log.i(DEFAULT_TAG, "tempStr:" + tempStr);
                        if (!TextUtils.isEmpty(tempStr)) {
                            // sb.append(nativeWrapperString(temp)).append("\n");
                            tempSB.append(tempStr).append("\t");
                        }
                    }
                    sb.append(wrapperString(tempSB.toString())).append("\n");
                }
            } else {
                // 不符合format规则数据
                if (args.length > 1) {
                    // 大于一次参数，第一个参数是字符串，默认是tag
                    String log = processTagCase(args);
                    if (!TextUtils.isEmpty(log)) {
                        sb.append(wrapperString(log)).append("\n");
                    } else {
                        // 需要支持打印""或者null
                        sb.append(wrapperString("")).append("\n");
                    }
                } else {
                    if (isNeedWrapper) {
                        sb.append(content_title_info_log).append("\n");
                    }
                    sb.append(wrapperString(one)).append("\n");
                }
            }
        } else {

            for (Object obj : args) {
                // 解析成字符串,添加
                String temp = processObjectCase(obj);
                // Log.i(DEFAULT_TAG, "temp:" + temp);
                if (!TextUtils.isEmpty(temp)) {
                    // sb.append(nativeWrapperString(temp)).append("\n");
                    sb.append(temp).append("\n");
                }
            }
        }
        // 结束,标记结束符
        if (isNeedWrapper) {
            sb.append(content_title_end);
        }
        // 打印字符
        preparePrint(level, sb.toString());

    }

    /*********************************************************************************************************/
    /**
     * 基础工具方法
     */
    /*********************************************************************************************************/
    /**
     * 处理对象
     *
     * @param obj
     * @return
     */
    private static String processObjectCase(Object obj) {

        StringBuilder sb = new StringBuilder();
        try {
            // 1.解析对象
            String result = objectToString(obj);
            if (!TextUtils.isEmpty(result)) {
                // 2.打印行头
                header(obj, sb);
                // 3.打印内容
                sb.append(wrapperString(result));// .append("\n");
            } else {
                // 需要支持""或null
                if (isNeedWrapper) {
                    sb.append(content_title_info_log).append("\n");
                }
                sb.append(wrapperString(""));// .append("\n");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * <pre>
     * 只有第一个参数为字符串且不是格式化的情况下才会进入该方法.
     * 该方法是负责处理tag或者message的情况. 主要要支持多重格式：
     * 1.L.x(TAG,Object);
     * 2.L.x(msg,Object);
     * 默认第一个参数为字符串且参数大于2个，第一个参数就为tag
     * </pre>
     *
     * @param args
     * @return
     */
    private static String processTagCase(Object[] args) {
        String one = (String)args[0];
        StringBuilder sb = new StringBuilder();
        TEMP_TAG = one;
        for (int i = 1; i < args.length; i++) {
            sb.append(processObjectCase(args[i])).append("\n");
        }
        return sb.toString();
    }

    /**
     * 打印行头
     *
     * @param obj
     * @param sb
     */
    private static void header(Object obj, StringBuilder sb) {
        if (isNeedWrapper) {
            if (obj instanceof String) {
                sb.append(content_title_info_log).append("\n");
            } else if (obj instanceof Throwable) {
                sb.append(content_title_info_error).append("\n");
            } else {
                sb.append(String.format(content_title_info_type, obj.getClass().getName())).append("\n");
            }
        }
    }

    /**
     * 处理堆栈信息
     *
     * @return
     */
    private static String getCallStaceInfo() {
        Exception callStack = new Exception("debug_info call stack.");
        StringBuilder sb = new StringBuilder();
        StackTraceElement stackElement[] = Thread.currentThread().getStackTrace();
        // 现在文件
        boolean currentFile = false;
        // 现在文件多重调用
        boolean isKeeping = false;
        for (StackTraceElement ste : stackElement) {
            if (currentFile && !isKeeping) {
                break;
            }
            if (ste.getClassName().equals(L.class.getName())) {
                if (!currentFile) {
                    currentFile = true;
                }
                isKeeping = true;
                continue;
            } else {
                if (currentFile) {

                    // 堆栈的错误第一行可以不要
                    String cc = null;
                    if (isNeedCallstackInfo) {
                        cc = parseString(callStack);
                        String[] tempArray = cc.split("\n");
                        StringBuilder tempSB = new StringBuilder();
                        for (int i = 1; i < tempArray.length; i++) {
                            tempSB.append(CONTENT_SPACE).append(CONTENT_SPACE).append(CONTENT_SPACE)
                                .append(tempArray[i]);
                            if (i != tempArray.length - 1) {
                                tempSB.append("\n");
                            }
                        }
                        cc = tempSB.toString();
                    }

                    if (isNeedWrapper) {
                        if (isNeedCallstackInfo) {

                            sb.append("\n").append(content_title_info_callstack).append("\n").append(CONTENT_LINE)
                                .append(CONTENT_SPACE).append("文件名:     " + ste.getFileName()).append("\n")
                                .append(CONTENT_LINE).append(CONTENT_SPACE).append("类名:      " + ste.getClassName())
                                .append("\n").append(CONTENT_LINE).append(CONTENT_SPACE)
                                .append("方法名:     " + ste.getMethodName()).append("\n").append(CONTENT_LINE)
                                .append(CONTENT_SPACE).append("行号:      " + ste.getLineNumber()).append("\n")
                                .append(CONTENT_LINE).append(CONTENT_SPACE)
                                .append("Native方法:" + (!ste.isNativeMethod() ? "不是" : "是")).append("\n")
                                .append(CONTENT_LINE).append(CONTENT_SPACE).append("调用堆栈详情:").append("\n")
                                .append(wrapperString(cc));
                        } else {
                            sb.append("\n").append(content_title_begin).append("\n").append(CONTENT_LINE)
                                .append(String.format(content_simple_callstack, ste.getClassName(), ste.getMethodName(),
                                    ste.getLineNumber()));
                            // 上一层会处理
                            // .append("\n");
                        }
                    } else {
                        if (isNeedCallstackInfo) {
                            sb.append("文件名:    " + ste.getFileName()).append("\n")
                                .append("类名:      " + ste.getClassName()).append("\n")
                                .append("方法名:    " + ste.getMethodName()).append("\n")
                                .append("行号:      " + ste.getLineNumber()).append("\n")
                                .append("Native方法:" + (!ste.isNativeMethod() ? "不是" : "是")).append("\n")
                                .append("调用堆栈详情:").append("\n").append(wrapperString(cc));
                        } else {
                            if (isFormat) {
                                sb.append(String.format(content_simple_callstack, ste.getClassName(),
                                    ste.getMethodName(), ste.getLineNumber()));
                            }
                        }
                    }

                    isKeeping = false;
                    break;
                }
            }
        }
        currentFile = false;
        isKeeping = false;
        callStack = null;
        stackElement = null;
        return sb.toString();
    }

    /*********************************************************************************************************/
    /**
     * 解析对象成字符串
     */
    /*********************************************************************************************************/
    private static String objectToString(Object object) {
        return objectToString(object, 0);
    }

    /**
     * 是否为静态内部类
     *
     * @param cla
     * @return
     */
    private static boolean isStaticInnerClass(Class<?> cla) {
        if (cla != null && cla.isMemberClass()) {
            int modifiers = cla.getModifiers();
            return (modifiers & Modifier.STATIC) == Modifier.STATIC;
        }
        return false;
    }

    /**
     * 根据类型匹配
     *
     * @param object
     * @param childLevel
     * @return
     */
    private static String objectToString(Object object, int childLevel) {
        if (object == null) {
            return null;
        }
        if (childLevel > MAX_CHILD_LEVEL) {
            return object.toString();
        }
        // 支持的类型.单独处理
        Class<?> czz = object.getClass();

        if (Build.VERSION.SDK_INT > 20) {
            if (BaseBundle.class.isAssignableFrom(czz)) {
                BaseBundle bundle = (BaseBundle)object;
                return parseString(bundle);
            }
        } else {
            if (Bundle.class.isAssignableFrom(czz)) {
                Bundle bundle = (Bundle)object;
                return parseString(bundle);
            }
        }
        if (String.class.isAssignableFrom(czz)) {
            String obj = (String)object;
            return parseString(obj);
        } else if (Number.class.isAssignableFrom(czz)) {
            Number obj = (Number)object;
            return String.valueOf(obj);
        } else if (Intent.class.isAssignableFrom(czz)) {
            Intent obj = (Intent)object;
            return parseString(obj);
        } else if (Collection.class.isAssignableFrom(czz)) {
            Collection<?> obj = (Collection<?>)object;
            return parseString(obj);
        } else if (Map.class.isAssignableFrom(czz)) {
            Map<?, ?> obj = (Map<?, ?>)object;
            return parseString(obj);
        } else if (Throwable.class.isAssignableFrom(czz)) {
            Throwable obj = (Throwable)object;
            return parseString(obj);
        } else if (Reference.class.isAssignableFrom(czz)) {
            Reference<?> obj = (Reference<?>)object;
            return parseString(obj);
        } else if (Message.class.isAssignableFrom(czz)) {
            Message obj = (Message)object;
            return parseString(obj);
            // } else if (isSubClass(czz, Activity.class)) {
        } else if (Activity.class.isAssignableFrom(czz)) {
            Activity obj = (Activity)object;
            return parseString(obj);
        } else if (JSONArray.class.isAssignableFrom(czz)) {
            JSONArray obj = (JSONArray)object;
            return format(obj);
        } else if (JSONObject.class.isAssignableFrom(czz)) {
            JSONObject obj = (JSONObject)object;
            return format(obj);
        } else if (StringBuilder.class.isAssignableFrom(czz)) {
            StringBuilder obj = (StringBuilder)object;
            return obj.toString();
        } else if (StringBuffer.class.isAssignableFrom(czz)) {
            StringBuffer obj = (StringBuffer)object;
            return obj.toString();
        } else if (Class.class.isAssignableFrom(czz)) {
            return parseStringByObject(object, childLevel);
        } else if (isArray(object)) {
            StringBuilder result = new StringBuilder();
            traverseArray(result, object);
            return result.toString();
        } else {
            if (object.toString().startsWith(object.getClass().getName() + "@")) {
                return parseStringByObject(object, childLevel);
            } else {
                // 若对象重写toString()方法默认走toString()
                return object.toString();
            }
        }
    }

    /**
     * 拼接class的字段和值
     *
     * @param cla
     * @param obj
     * @param o 对象
     * @param childOffset 递归解析属性的层级
     */
    private static void getClassFields(Class<?> cla, JSONObject obj, Object o, int childOffset) {
        try {
            if (cla.equals(Object.class)) {
                return;
            }
            // if (isSubClass) {
            // builder.append(BR + BR + "=> ");
            // }
            Field[] fields = cla.getDeclaredFields();
            for (int i = 0; i < fields.length; ++i) {
                Field field = fields[i];
                field.setAccessible(true);
                if (cla.isMemberClass() && !isStaticInnerClass(cla) && i == 0) {
                    continue;
                }

                if (field.getName().equals("$change")) {
                    continue;
                }
                // 解决Instant Run情况下内部类死循环的问题
                // System.out.println(field.getName()+ "***" +subObject.getClass() + "啊啊啊啊啊啊" +
                // cla);
                if (!isStaticInnerClass(cla)
                    && (field.getName().equals("$change") || field.getName().equalsIgnoreCase("this$0"))) {
                    continue;
                }
                Object subObject = null;
                try {
                    subObject = field.get(o);
                } catch (IllegalAccessException e) {
                    subObject = e;
                } finally {
                    if (subObject != null) {

                        if (childOffset < MAX_CHILD_LEVEL) {
                            if (!Number.class.isAssignableFrom(subObject.getClass())) {
                                subObject = objectToString(subObject, childOffset + 1);
                                String s = (String)subObject;
                                s = s.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\r\n", "");
                                try {
                                    JSONObject temp = new JSONObject(s);
                                    obj.put(field.getName(), temp);
                                } catch (Throwable e) {
                                    try {
                                        JSONArray arr = new JSONArray(s);
                                        obj.put(field.getName(), arr);
                                    } catch (Throwable e2) {
                                        obj.put(field.getName(), s);
                                    }
                                }
                            } else {
                                obj.put(field.getName(), subObject);
                            }
                        } else {
                            obj.put(field.getName(), subObject.toString());
                        }
                    } else {
                        obj.put(field.getName(), "null");
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数组的纬度
     *
     * @param object
     * @return
     */
    private static int getArrayDimension(Object object) {
        int dim = 0;
        for (int i = 0; i < object.toString().length(); ++i) {
            if (object.toString().charAt(i) == '[') {
                ++dim;
            } else {
                break;
            }
        }
        return dim;
    }

    /**
     * 是否为数组
     *
     * @param object
     * @return
     */
    private static boolean isArray(Object object) {
        return object.getClass().isArray();
    }

    /**
     * 获取数组类型
     *
     * @param object 如L为int型
     * @return
     */
    private static char getType(Object object) {
        if (isArray(object)) {
            String str = object.toString();
            return str.substring(str.lastIndexOf("[") + 1, str.lastIndexOf("[") + 2).charAt(0);
        }
        return 0;
    }

    /**
     * 遍历数组
     *
     * @param result
     * @param array
     */
    private static void traverseArray(StringBuilder result, Object array) {
        if (isArray(array)) {
            if (getArrayDimension(array) == 1) {
                switch (getType(array)) {
                    case 'I':
                        result.append(Arrays.toString((int[])array));
                        break;
                    case 'D':
                        result.append(Arrays.toString((double[])array));
                        break;
                    case 'Z':
                        result.append(Arrays.toString((boolean[])array));
                        break;
                    case 'B':
                        result.append(Arrays.toString((byte[])array));
                        break;
                    case 'S':
                        result.append(Arrays.toString((short[])array));
                        break;
                    case 'J':
                        result.append(Arrays.toString((long[])array));
                        break;
                    case 'F':
                        result.append(Arrays.toString((float[])array));
                        break;
                    case 'C':
                        result.append(Arrays.toString((char[])array));
                        break;
                    case 'L':
                        Object[] objects = (Object[])array;
                        result.append("[");
                        for (int i = 0; i < objects.length; ++i) {
                            result.append(objectToString(objects[i]));
                            if (i != objects.length - 1) {
                                result.append(",");
                            }
                        }
                        result.append("]");
                        break;
                    default:
                        result.append(Arrays.toString((Object[])array));
                        break;
                }
            } else {
                result.append("[");
                for (int i = 0; i < ((Object[])array).length; i++) {
                    traverseArray(result, ((Object[])array)[i]);
                    if (i != ((Object[])array).length - 1) {
                        result.append(",");
                    }
                }
                result.append("]");
            }
        } else {
            result.append("not a array!!");
        }
    }

    private static String parseStringByObject(Object object, int childLevel) {
        try {
            JSONObject obj = new JSONObject();
            getClassFields(object.getClass(), obj, object, childLevel);
            Class<?> superClass = object.getClass().getSuperclass();
            if (superClass != null) {
                while (!superClass.equals(Object.class)) {
                    getClassFields(superClass, obj, object, childLevel);
                    superClass = superClass.getSuperclass();
                }
            } else {
                obj.put("toString", object.toString());
            }
            return format(obj);
        } catch (Throwable e) {
            return object.toString();
        }
    }

    private static String parseString(Activity activity) {
        JSONObject obj = new JSONObject();
        // Field[] fields = activity.getClass().getDeclaredFields();
        Field[] fields = activity.getClass().getFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if ("org.aspectj.lang.JoinPoint$StaticPart".equals(f.getType().getName())) {
                continue;
            }
            if (f.getName().equals("$change") || f.getName().equalsIgnoreCase("this$0")) {
                continue;
            }
            try {
                Object fieldValue = f.get(activity);
                obj.put(f.getName(), objectToString(fieldValue));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        StringBuilder builder = new StringBuilder(activity.getClass().getName());
        builder.append(" {");
        builder.append(BR);
        for (Field field : fields) {
            field.setAccessible(true);
            if ("org.aspectj.lang.JoinPoint$StaticPart".equals(field.getType().getName())) {
                continue;
            }
            if (field.getName().equals("$change") || field.getName().equalsIgnoreCase("this$0")) {
                continue;
            }
            try {
                Object fieldValue = field.get(activity);
                builder.append(field.getName()).append("=>").append(objectToString(fieldValue)).append(BR);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        builder.append("}");
        Log.d("www", builder.toString());
        return format(obj);
    }

    private static String parseString(Message message) {
        if (message == null) {
            return null;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("what", message.what);
            obj.put("when", message.getWhen());
            obj.put("arg1", message.arg1);
            obj.put("arg2", message.arg2);
            obj.put("data", parseString(message.getData()));
            obj.put("obj", objectToString(message.obj));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return format(obj);
    }

    private static String parseString(Reference<?> reference) {
        Object actual = reference.get();
        return objectToString(actual);
    }

    private static String parseString(Map<?, ?> map) {
        JSONObject obj = new JSONObject();
        Set<?> keys = map.keySet();
        for (Object key : keys) {
            try {
                Object value = map.get(key);
                if (key == null) {
                    key = "null";
                }
                if (value != null) {
                    obj.put(objectToString(key), objectToString(value));
                } else {
                    obj.put(objectToString(key), "null");
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return format(obj);

    }

    private static String parseString(Collection<?> collection) {

        JSONArray arr = new JSONArray();
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            arr.put(objectToString(o));
        }
        return format(arr);
    }

    @TargetApi(21)
    private static String parseString(BaseBundle bundle) {
        if (bundle != null) {
            JSONObject bun = new JSONObject();
            for (String key : bundle.keySet()) {
                try {
                    bun.put(key, objectToString(bundle.get(key)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return format(bun);
        }
        return null;
    }

    private static String parseString(Bundle bundle) {
        if (bundle != null) {
            JSONObject bun = new JSONObject();
            for (String key : bundle.keySet()) {
                try {
                    bun.put(key, objectToString(bundle.get(key)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return format(bun);
        }
        return null;
    }

    /**
     * 处理对象且返回.处理顺序是： JSONObject>JSONArray>XML
     *
     * @param src
     * @return
     */
    private static String parseString(String src) {
        try {
            JSONObject oo = new JSONObject(src);
            return format(oo);
        } catch (JSONException e1) {
            // 不是JSONObject
            try {
                JSONArray arr = new JSONArray(src);
                return format(arr);
            } catch (JSONException e2) {
                // 不是JSONArray

                StringReader reader = null;
                try {
                    reader = new StringReader(src);
                    Source xmlInput = new StreamSource(reader);
                    StreamResult xmlOutput = new StreamResult(new StringWriter());
                    Transformer transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                    transformer.transform(xmlInput, xmlOutput);
                    String xml = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
                    return xml;
                } catch (Throwable e3) {
                    // 不是XML
                    return src;
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        } catch (Throwable e) {
            return src;
        }
    }

    /**
     * 将异常信息打印出来。出来数据是带行前的双竖线(如果设置wrapper是true),不带头
     *
     * @param e
     * @return
     */
    private static String parseString(Throwable e) {
        // 可以使用系统的这个接口
        // return Log.getStackTraceString(e);
        // 自己实现堆栈,较之官方的增加了两个字符的缩进
        StringWriter sw = null;
        PrintWriter pw = null;
        StringBuilder sb = new StringBuilder();
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
            String result = sw.toString();
            String[] ss = result.split("\n");
            String s = null;
            for (int i = 0; i < ss.length; i++) {
                s = ss[i];
                // 一般首第一个字符不知道是什么东西
                if (s.substring(1, 3).equalsIgnoreCase("at")) {
                    // 部分堆栈怕其他行缩进失误
                    // if (i > 0) {
                    sb.append(CONTENT_SPACE).append(s);
                } else {
                    sb.append(s);
                }
                if (i != ss.length - 1) {
                    sb.append("\n");
                }
            }
        } catch (Throwable error) {
            error.printStackTrace();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sb.toString();
    }

    private static String parseString(Intent intent) {
        JSONObject obj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(intent.getScheme())) {
                obj.put("Scheme", intent.getScheme());
            }
            if (!TextUtils.isEmpty(intent.getAction())) {
                obj.put("Action", intent.getAction());
            }
            if (!TextUtils.isEmpty(intent.getDataString())) {
                obj.put("DataString", intent.getDataString());
            }
            if (!TextUtils.isEmpty(intent.getType())) {
                obj.put("Type", intent.getType());
            }
            if (!TextUtils.isEmpty(intent.getPackage())) {
                obj.put("Package", intent.getPackage());
            }
            if (!TextUtils.isEmpty(intent.getComponent().toString())) {
                obj.put("ComponentInfo", intent.getComponent().toString());
            }
            if (!TextUtils.isEmpty(intent.getCategories().toString())) {
                obj.put("Categories", intent.getCategories().toString());
            }
            String extras = parseString(intent.getExtras());
            if (!TextUtils.isEmpty(extras)) {
                obj.put("Extras", extras);
            }
            String flags = getFlags(intent.getFlags());
            if (!TextUtils.isEmpty(flags)) {
                obj.put("Flags", intent.getType());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return format(obj);
    }

    private static String getFlags(int flags) {
        // 获取相应信息
        SparseArray<String> flagMap = new SparseArray<String>();
        Class<?> cla = Intent.class;
        Field[] fields = cla.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (field.getName().startsWith("FLAG_")) {
                    int value = 0;
                    Object object = field.get(cla);
                    if (object instanceof Integer || object.getClass().getSimpleName().equals("int")) {
                        value = (Integer)object;
                    }

                    if (flagMap.get(value) == null) {
                        flagMap.put(value, field.getName());
                    }
                }
            } catch (Throwable e) {
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < flagMap.size(); i++) {
            int flagKey = flagMap.keyAt(i);
            if ((flagKey & flags) == flagKey) {
                builder.append(flagMap.get(flagKey));
                builder.append(" | ");
            }
        }
        if (TextUtils.isEmpty(builder.toString())) {
            builder.append(flags);
        } else if (builder.indexOf("|") != -1) {
            builder.delete(builder.length() - 2, builder.length());
        }
        return builder.toString();
    }

    /*********************************************************************************************************/
    /**
     * 格式化字符串、异常、JSONArray、JSONObject
     */
    /*********************************************************************************************************/

    /**
     * 格式化输出JSONArray
     *
     * @param arr
     * @return
     */
    private static String format(JSONArray arr) {
        if (arr != null) {
            try {
                return isFormat ? (arr.toString(JSON_INDENT)) : arr.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 格式化输出JSONObject
     *
     * @param obj
     * @return
     */
    private static String format(JSONObject obj) {

        if (obj != null) {
            try {
                return isFormat ? obj.toString(JSON_INDENT) : obj.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /*********************************************************************************************************/
    /**
     * 字符串包裹处理
     */
    /*********************************************************************************************************/
    /**
     * 字符串处理,wrapper选中情况下,行首加封闭符
     *
     * @param log
     * @return
     */
    private static String wrapperString(String log) {
        StringBuilder sb = new StringBuilder();

        if (TextUtils.isEmpty(log)) {
            if (isNeedWrapper) {
                sb.append(CONTENT_LINE);
            }
            sb.append(CONTENT_LOG_EMPTY);
            return sb.toString();
        }
        String ss[] = new String[] {};
        String temp = null;
        if (log.contains("\n")) {
            ss = log.split("\n");
            if (ss.length > 0) {
                sb = new StringBuilder();
                for (int i = 0; i < ss.length; i++) {
                    temp = ss[i];
                    if (isNeedWrapper && !temp.startsWith(CONTENT_A) && !temp.startsWith(CONTENT_B)
                        && !temp.startsWith(CONTENT_C) && !temp.startsWith(CONTENT_D)
                        && !temp.startsWith(CONTENT_LOG_INFO) && !TextUtils.isEmpty(temp)
                        && !temp.startsWith(CONTENT_E)) {
                        sb.append(CONTENT_LINE);
                    }
                    sb.append(temp);

                    if (i != ss.length - 1) {
                        sb.append("\n");
                    }
                }
            }
        } else if (log.contains("\r")) {
            ss = log.split("\r");
            if (ss.length > 0) {
                sb = new StringBuilder();
                for (int i = 0; i < ss.length; i++) {
                    temp = ss[i];

                    if (isNeedWrapper && !temp.startsWith(CONTENT_A) && !temp.startsWith(CONTENT_B)
                        && !temp.startsWith(CONTENT_D) && !temp.startsWith(CONTENT_E)
                        && !temp.startsWith(CONTENT_LOG_INFO) && !TextUtils.isEmpty(temp)
                        && !temp.startsWith(CONTENT_C)) {
                        sb.append(CONTENT_LINE);
                    }
                    sb.append(temp);
                    if (i != ss.length - 1) {
                        sb.append("\r");
                    }
                }
            }
        } else if (log.contains("\r\n")) {
            ss = log.split("\r\n");
            if (ss.length > 0) {
                sb = new StringBuilder();
                for (int i = 0; i < ss.length; i++) {
                    temp = ss[i];

                    if (isNeedWrapper && !temp.startsWith(CONTENT_A) && !temp.startsWith(CONTENT_B)
                        && !temp.startsWith(CONTENT_D) && !temp.startsWith(CONTENT_E)
                        && !temp.startsWith(CONTENT_LOG_INFO) && !TextUtils.isEmpty(temp)
                        && !temp.startsWith(CONTENT_C)) {
                        sb.append(CONTENT_LINE);
                    }
                    sb.append(temp);

                    if (i != ss.length - 1) {
                        sb.append("\r\n");
                    }
                }
            }
        } else if (log.contains("\n\r")) {
            ss = log.split("\n\r");
            if (ss.length > 0) {
                sb = new StringBuilder();
                for (int i = 0; i < ss.length; i++) {
                    temp = ss[i];
                    if (isNeedWrapper && !temp.startsWith(CONTENT_A) && !temp.startsWith(CONTENT_B)
                        && !temp.startsWith(CONTENT_D) && !temp.startsWith(CONTENT_E)
                        && !temp.startsWith(CONTENT_LOG_INFO) && !TextUtils.isEmpty(temp)
                        && !temp.startsWith(CONTENT_C)) {
                        sb.append(CONTENT_LINE);
                    }
                    sb.append(temp);

                    if (i != ss.length - 1) {
                        sb.append("\n\r");
                    }
                }
            }
        } else {
            if (isNeedWrapper && !log.startsWith(CONTENT_A) && !log.startsWith(CONTENT_B) && !log.startsWith(CONTENT_D)
                && !log.startsWith(CONTENT_LOG_INFO) && !TextUtils.isEmpty(log) && !log.startsWith(CONTENT_E)
                && !log.startsWith(CONTENT_C)) {
                sb.append(CONTENT_LINE);
            }
            sb.append(log);
        }
        return sb.toString();
    }

    /*********************************************************************************************************/
    /**
     * 打印方法
     */
    /*********************************************************************************************************/

    /**
     * 动态检查临时.切割大文件
     *
     * @param level
     * @param msg
     */
    private static void preparePrint(int level, String msg) {
        String tag = DEFAULT_TAG;
        if (!TextUtils.isEmpty(TEMP_TAG)) {
            tag = TEMP_TAG;
        }

        if (msg.length() > LOG_MAXLENGTH) {
            List<String> splitStr = getStringBysplitLine(msg, LOG_MAXLENGTH);

            StringBuilder sb = null;
            for (int i = 0; i < splitStr.size(); i++) {
                String line = splitStr.get(i);

                if (sb == null) {
                    sb = new StringBuilder();
                }
                if (sb.length() + line.length() >= LOG_MAXLENGTH) {
                    realPrint(level, tag, wrapperString(sb.toString()));
                    sb = new StringBuilder();
                    if (line.length() >= LOG_MAXLENGTH) {
                        realPrint(level, tag, wrapperString(line));
                    } else {
                        sb.append(line);
                    }
                    if (i != splitStr.size() - 1) {
                        sb.append("\n");
                    }
                } else {
                    sb.append(line);
                    if (i != splitStr.size() - 1) {
                        sb.append("\n");
                    }
                }
            }
            if (sb != null) {
                realPrint(level, tag, wrapperString(sb.toString()));
                sb = null;
            }
        } else {
            realPrint(level, tag, wrapperString(msg));
        }
        TEMP_TAG = "";
    }

    /**
     * 真正打印单个信息
     *
     * @param level
     * @param tag
     * @param printStr
     */
    private static void realPrint(int level, String tag, String printStr) {
        switch (level) {
            case MLEVEL.DEBUG:
                Log.d(tag, printStr);
                break;
            case MLEVEL.INFO:
                Log.i(tag, printStr);
                break;
            case MLEVEL.ERROR:
                Log.e(tag, printStr);
                break;
            case MLEVEL.VERBOSE:
                Log.v(tag, printStr);
                break;
            case MLEVEL.WARN:
                Log.w(tag, printStr);
                break;
            case MLEVEL.WTF:
                Log.wtf(tag, printStr);
                break;
            default:
                break;
        }
    }

    /**
     * 按行分割字符串
     *
     * @param msg
     * @param maxLen
     * @return
     */
    private static List<String> getStringBysplitLine(String msg, int maxLen) {
        List<String> result = new ArrayList<String>();
        String[] lines = msg.split("\n");
        if (lines.length == 1) {
            lines = msg.split("\r");
            if (lines.length == 1) {
                lines = msg.split("\r\n");
                if (lines.length == 1) {
                    lines = msg.split("\n\r");
                }
            }
        }
        if (lines.length > 1) {

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                // 单行都超过最大长度，直接按照字符串分别来做
                processLine(maxLen, result, line);
            }
        } else {
            processLine(maxLen, result, msg);
        }

        return result;
    }

    /**
     * 处理单行超长处理
     *
     * @param maxLen
     * @param result
     * @param line
     */
    private static void processLine(int maxLen, List<String> result, String line) {
        if (line.length() > maxLen) {
            int current = 0;
            String str;
            while (true) {
                try {
                    str = line.substring(current, current + maxLen);
                    result.add(str);
                    current += maxLen;
                } catch (StringIndexOutOfBoundsException e) {
                    str = line.substring(current, line.length());
                    result.add(str);
                    break;
                }
            }
        } else {
            result.add(line);
        }
    }

}