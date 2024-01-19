package run.mone.m78.ip.search;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author goodjava@qq.com
 * @date 2023/5/27 09:34
 */
public class MyChooseByNameContributor implements ChooseByNameContributor {


    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        // Return the names of the elements you want to include in the search results.
        return new String[]{"MyCustomElement"};
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        // Return the elements that match the given name.
        if ("MyCustomElement".equals(name)) {
            return new NavigationItem[]{new NavigationItem() {
                @Override
                public @Nullable @NlsSafe String getName() {
                    return "run.mone.service.MoneService";
                }

                @Override
                public @Nullable ItemPresentation getPresentation() {
                    return new ItemPresentation() {
                        @Override
                        public @NlsSafe @Nullable String getPresentableText() {
                            return "vvv";
                        }

                        @Override
                        public @Nullable Icon getIcon(boolean unused) {
                            return null;
                        }
                    };
                }

                @Override
                public void navigate(boolean requestFocus) {

                }

                @Override
                public boolean canNavigate() {
                    return true;
                }

                @Override
                public boolean canNavigateToSource() {
                    return true;
                }
            }};
        }
        return new NavigationItem[0];
    }



}
