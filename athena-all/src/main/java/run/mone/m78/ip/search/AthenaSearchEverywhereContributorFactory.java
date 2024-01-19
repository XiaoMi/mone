package run.mone.m78.ip.search;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/27 12:08
 */
public class AthenaSearchEverywhereContributorFactory implements SearchEverywhereContributorFactory<AthenaSearchInfo> {


    @Override
    public @NotNull SearchEverywhereContributor<AthenaSearchInfo> createContributor(@NotNull AnActionEvent initEvent) {
        @Nullable Module module = initEvent.getData(LangDataKeys.MODULE);
        return new AthenaSearchEverywhereContributor(initEvent.getProject(), module);
    }
}
