package com.xiaomi.youpin.tesla.ip.search;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author goodjava@qq.com
 * @date 2023/5/27 09:35
 */
public class MyCustomNavigationItem implements NavigationItem {

    @Override
    public void navigate(boolean requestFocus) {
        // Implement the navigation logic here.
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }


    @Override
    public @Nullable @NlsSafe String getName() {
        return "run.mone.service.MoneService";
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return "MyCustomElement";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "gopgogogogo";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return null;
            }
        };
    }
}





