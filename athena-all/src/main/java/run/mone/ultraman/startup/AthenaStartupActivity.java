//package run.mone.ultraman.startup;
//
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.startup.ProjectActivity;
//import kotlin.Unit;
//import kotlin.coroutines.Continuation;
//import lombok.extern.slf4j.Slf4j;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
///**
// * @author goodjava@qq.com
// * @date 2024/5/29 09:20
// */
//@Slf4j
//public class AthenaStartupActivity implements ProjectActivity {
//
//    @Nullable
//    @Override
//    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
//        log.info("open project:{}", project.getName());
////        TerminalExecutionConsole terminalConsole = new TerminalExecutionConsole(project, new JBTerminalWidget(project));
////        terminalConsole.getTerminalWidget().addMessageFilter(new Filter() {
////            @Override
////            public @Nullable Result applyFilter(@NotNull String line, int entireLength) {
////                return null;
////            }
////        });
//
//        return null;
//    }
//}
