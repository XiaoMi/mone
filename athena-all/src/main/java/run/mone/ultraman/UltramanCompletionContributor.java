package run.mone.ultraman;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author goodjava@qq.com
 * @date 2022/5/2 09:57
 */
public class UltramanCompletionContributor extends CompletionContributor {

//    public UltramanCompletionContributor() {
//        extend(CompletionType.BASIC, PlatformPatterns.psiElement(Field),
//                new CompletionProvider<>() {
//                    public void addCompletions(@NotNull CompletionParameters parameters,
//                                               @NotNull ProcessingContext context,
//                                               @NotNull CompletionResultSet resultSet) {
//                        resultSet.addElement(LookupElementBuilder.create("Hello"));
//                    }
//                }
//        );
//    }


    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        result.addElement(LookupElementBuilder.create("Hello"));
    }
}
