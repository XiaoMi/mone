/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.data.push.antlr.json;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.Map;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class JSONListenerImpl extends JSONBaseListener {

    ParseTreeProperty<String> newJson = new ParseTreeProperty<>();

    public String getNewJson(ParseTree ctx) {
        return newJson.get(ctx);
    }

    void setNewJson(ParseTree ctx, String s) {
        newJson.put(ctx, s);
    }


    private Map<String, String> map;

    public JSONListenerImpl(Map<String, String> values) {
        this.map = values;
    }


    @Override
    public void exitJson(JSONParser.JsonContext ctx) {
        setNewJson(ctx, getNewJson(ctx.getChild(0)));
    }


    @Override
    public void exitVar(JSONParser.VarContext ctx) {
        String value = map.get(ctx.getText());
        JsonElement m = new Gson().fromJson(value, JsonElement.class);
        if (m.isJsonArray()) {
            setNewJson(ctx,value);
        } else {
            setNewJson(ctx, "\"" + value + "\"");
        }
    }

    @Override
    public void exitPair(JSONParser.PairContext ctx) {
        String tag = ctx.STRING().getText();
        JSONParser.ValueContext vctx = ctx.value();
        String x = String.format("%s:%s", tag, getNewJson(vctx));
        setNewJson(ctx, x);
    }

    @Override
    public void exitObjectValue(JSONParser.ObjectValueContext ctx) {
        setNewJson(ctx, getNewJson(ctx.obj()));
    }

    @Override
    public void exitNumber(JSONParser.NumberContext ctx) {
        super.exitNumber(ctx);
    }


    @Override
    public void enterNumber(JSONParser.NumberContext ctx) {
        setNewJson(ctx, ctx.getText());
    }

    @Override
    public void exitAtom(JSONParser.AtomContext ctx) {
        setNewJson(ctx, ctx.getText());
    }

    @Override
    public void exitEmptyObject(JSONParser.EmptyObjectContext ctx) {
        setNewJson(ctx, "{}");
    }


    @Override
    public void exitAnObject(JSONParser.AnObjectContext ctx) {
        StringBuilder buf = new StringBuilder();
        int size = ctx.pair().size();
        for (int i = 0; i < size; i++) {
            buf.append(getNewJson(ctx.pair().get(i)));
            if (i < size - 1) {
                buf.append(",");
            }
        }
        setNewJson(ctx, "{" + buf.toString() + "}");
    }

    @Override
    public void exitArrayValue(JSONParser.ArrayValueContext ctx) {
        setNewJson(ctx, getNewJson(ctx.array()));
    }

    @Override
    public void exitEmptyArray(JSONParser.EmptyArrayContext ctx) {
        setNewJson(ctx, "[]");
    }

    @Override
    public void exitArrayOfValues(JSONParser.ArrayOfValuesContext ctx) {
        StringBuilder buf = new StringBuilder();
        int i = 0;
        int size = ctx.value().size();
        for (JSONParser.ValueContext vctx : ctx.value()) {
            buf.append(getNewJson(vctx));
            if (i < size - 1) {
                buf.append(",");
            }
            i++;
        }
        setNewJson(ctx, "[" + buf.toString() + "]");
    }

    @Override
    public void exitString(JSONParser.StringContext ctx) {
        setNewJson(ctx, ctx.getText());
    }


}
