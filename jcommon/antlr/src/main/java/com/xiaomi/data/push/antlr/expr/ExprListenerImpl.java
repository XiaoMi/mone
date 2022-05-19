package com.xiaomi.data.push.antlr.expr;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.reflect.MethodUtils;
import org.springframework.beans.DirectFieldAccessor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author zhangzhiyong
 * @date 07/06/2018
 */
@Slf4j
public class ExprListenerImpl extends ExprBaseListener {


    private Map<String, Object> map = Maps.newHashMap();

    private Stack<ExpNode> stack = new Stack<>();

    public ExprListenerImpl(String key, Object obj) {
        this.map.put(key, obj);
        this.stack.add(new ExpNode("id", obj));
    }


    @Override
    public void exitId(ExprParser.IdContext ctx) {
        log.debug("id=" + ctx.getText());
        if (!this.map.containsKey(ctx.getText())) {
            stack.add(new ExpNode("id", ctx.getText()));
        }
    }


    /**
     * 按索引取值
     *
     * @param ctx
     */
    @Override
    public void exitMb(ExprParser.MbContext ctx) {
        log.debug("mb=" + ctx.getText());
        Object value = this.stack.pop().value;
        if (value instanceof List) {
            Object v = ((List) value).get(Integer.parseInt(ctx.ID().getText()));
            this.stack.add(new ExpNode("id", v));
        }
    }

    @Override
    public void exitInt(ExprParser.IntContext ctx) {
        log.debug("int=" + ctx.getText());
    }


    /**
     * 计算属性
     *
     * @param ctx
     */
    @Override
    public void exitPro(ExprParser.ProContext ctx) {
        log.debug("pro");
        ExpNode node = this.stack.pop();

        if (node.type.equals("id")) {//计算属性
            Object obj = this.stack.pop().value;
            String property = node.value.toString();

            DirectFieldAccessor accessor = new DirectFieldAccessor(obj);
            Object value = accessor.getPropertyValue(property);
            this.stack.add(new ExpNode("id", value));
        } else if (node.type.equals("method")) {//执行方法

            String method = node.value.toString();

            //string to map  私有函数:把string变为map.但string必须符合json格式
            if (method.equals("toMap")) {
                Object obj = this.stack.pop().value;
                Gson gson = new Gson();
                String str = "";
                if (obj.getClass().equals(byte[].class)) {
                    str = new String((byte[]) obj);
                } else {
                    str = obj.toString();
                }

                Map m = gson.fromJson(str, Map.class);
                this.stack.add(new ExpNode("id", m));
                return;
            }


            if (method.equals("toList")) {
                Object obj = this.stack.pop().value;
                Gson gson = new Gson();
                String str = "";
                if (obj.getClass().equals(byte[].class)) {
                    str = new String((byte[]) obj);
                } else {
                    str = obj.toString();
                }

                List l = gson.fromJson(str, List.class);
                this.stack.add(new ExpNode("id", l));
                return;

            }

            if (method.equals("json")) {
                Object obj = this.stack.pop().value;
                Gson gson = new Gson();
                String str = "";
                if (obj.getClass().equals(byte[].class)) {
                    str = new String((byte[]) obj);
                } else {
                    str = obj.toString();
                }

                JsonObject jobj = gson.fromJson(str, JsonObject.class);
                this.stack.add(new ExpNode("id", jobj));
                return;

            }


            List<Object> list = Lists.newArrayList();
            //包括参数
            if (this.stack.peek().type.equals("params")) {
                String params = this.stack.pop().value.toString();
                //参数
                log.debug("========" + params);
                String[] ss = params.split(",");
                for (String s : ss) {
                    String[] pp = s.split(":");
                    if (pp[1].equals("int")) {
                        list.add(Integer.parseInt(pp[0]));
                    } else if (pp[1].equals("long")) {
                        list.add(Long.valueOf(pp[0]));
                    } else if (pp[1].equals("string")) {
                        list.add(pp[0]);
                    }
                }
            }
            Object obj = this.stack.pop().value;

            try {
                Object res = null;
                if (list.size() > 0) {
                    res = MethodUtils.invokeMethod(obj, method, list.toArray());
                } else {
                    res = MethodUtils.invokeMethod(obj, method, null);
                }
                this.stack.add(new ExpNode("id", res));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void exitMet(ExprParser.MetContext ctx) {
        log.debug("method=" + ctx.getText());
        this.stack.add(new ExpNode("method", ctx.ID().getText()));
    }

    @Override
    public void exitMp(ExprParser.MpContext ctx) {
        log.debug("map=" + ctx.getText());
        Object value = this.stack.pop().value;
        if (value instanceof Map) {
            Object v = ((Map) value).get(ctx.ID().getText());
            this.stack.add(new ExpNode("id", v));
        }
    }

    @Override
    public void exitParams(ExprParser.ParamsContext ctx) {
        log.debug("params=" + ctx.getText());
        this.stack.add(new ExpNode("params", ctx.getText()));
    }


    //    @Override
//    public void exitFangKuoHao(ExprParser.FangKuoHaoContext ctx) {
//        Object lhs = this.obj;
//        String rhs = ctx.INT().getText();
//        System.out.println("fangkuohao--------->" + (lhs + "->" + rhs));
//
//        this.stack.add(lhs);
//        this.stack.add("[]");
//        this.stack.add(rhs);
//
//
////        if (this.obj instanceof List) {
////            Object value = ((List) obj).get(Integer.parseInt(rhs));
////            this.obj = value;
////            values.put(ctx, value);
////        } else {
////            values.put(ctx, lhs + rhs);
////        }
//
//    }


//    @Override
//    public void exitParams(ExprParser.ParamsContext ctx) {
//        int count = ctx.getChildCount();
//        IntStream.range(0, count).forEach(it -> {
//            System.out.println(ctx.getChild(it).getText());
//        });
//    }


//    @Override
//    public void exitProperty1(ExprParser.Property1Context ctx) {
//        System.out.println("property");
//    }


    //    @Override
//    public void exitMethod(ExprParser.MethodContext ctx) {
//        System.out.println(ctx.getText());
//
//
//        System.out.println("---------->fangfa:" + ctx.getChild(0).getChild(0));
//        String method = ctx.getChild(0).getChild(0).getText();
//        try {
//            Object result = MethodUtils.invokeMethod(this.obj, method, null);
//            System.out.println("result----->" + result);
//            this.obj = result;
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//
//        super.exitMethod(ctx);
//    }


    public Object cal() {
        Object result = null;
        while (!this.stack.isEmpty()) {
            result = this.stack.pop().value;
        }
        return result;
    }

//    @Override
//    public void exitJuHao(ExprParser.JuHaoContext ctx) {
//        Object value = this.obj;
//        String rhs = ctx.ID().toString();
//        System.out.println("juhao--------->" + (value + "->" + rhs));
//        try {
//            String value2 = BeanUtils.getProperty(this.obj, rhs);
//            System.out.println(value2);
//            this.obj = value2;
//            values.put(ctx, value2);
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//
//
//        values.put(ctx, "");
//    }
}
