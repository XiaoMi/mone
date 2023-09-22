///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.codecheck.docCheck;
//
//import com.xiaomi.youpin.codecheck.po.CheckResult;
//import org.apache.commons.lang3.tuple.Pair;
//
///**
// * @author goodjava@qq.com
// */
//public class JavaDocReader {
//
//    private static final String DESC = "interface should have java doc or comment";
//    private static final String CHINA_DESC = "interface should have java doc or comment";
//    private static final Pair<Integer, CheckResult> success = Pair.of(CheckResult.INFO, CheckResult.getInfoRes("DubboMethodMustReturnResultRule", "", ""));
//
//    private static RootDoc root;
//
//    public static class Doclet {
//
//        public Doclet() {
//        }
//
//        public static boolean start(RootDoc root) {
//            JavaDocReader.root = root;
//            return true;
//        }
//    }
//
//    public static Pair<Integer, CheckResult> show() {
//        boolean isWarn = false;
//        String methodName = "";
//        ClassDoc[] classes = root.classes();
//        for (int i = 0; i < classes.length; ++i) {
//            if (classes[i].isInterface()) {
//                for (MethodDoc method : classes[i].methods()) {
//                    if (method.commentText() == null || "".equals(method.commentText())) {
//                        isWarn = true;
//                        methodName += method.name() + " ";
//                    }
//                }
//            }
//        }
//
//        return isWarn ? Pair.of(CheckResult.WARN, CheckResult.getWarnRes("method name: " + methodName, DESC, CHINA_DESC)) : success;
//
//    }
//
//    public static String show0() {
//        ClassDoc[] classes = root.classes();
//        String res = "";
//        for (int i = 0; i < classes.length; ++i) {
//            if (classes[i].getRawCommentText() != null && !"".equals(classes[i].getRawCommentText())) {
//                res = "ClassName: " + classes[i].name() + ", doc: \n" + classes[i].getRawCommentText() + "\n";
//            }
//            for (MethodDoc method : classes[i].methods()) {
//                if (method.commentText() != null && !"".equals(method.commentText())) {
//                    res += "MethodName: " + method.name() + ", doc: \n" + method.commentText() + "\n";
//                }
//            }
//        }
//
//        return res;
//    }
//
//    public static RootDoc getRoot() {
//        return root;
//    }
//
//    public JavaDocReader() {
//
//    }
//
//
//    public static Pair<Integer, CheckResult> check(String path) {
//        if (path == null || "".equals(path)) {
//            return success;
//        }
//        try {
//            com.sun.tools.javadoc.Main.execute(new String[]{"-doclet",
//                    Doclet.class.getName(),
//                    "-encoding", "utf-8",
//                    path});
//            return show();
//        } catch (Throwable e) {
//            return success;
//        }
//    }
//
//    public static String getDoc(String path) {
//        if (path == null || "".equals(path)) {
//            return "";
//        }
//        try {
//            com.sun.tools.javadoc.Main.execute(new String[]{"-doclet",
//                    Doclet.class.getName(),
//                    "-encoding", "utf-8",
//                    path});
//            return show0();
//        } catch (Throwable e) {
//            return "";
//        }
//    }
//
//}
