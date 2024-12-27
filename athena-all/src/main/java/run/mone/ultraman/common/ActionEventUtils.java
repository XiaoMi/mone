package run.mone.ultraman.common;

import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.speedSearch.SpeedSearchSupply;

/**
 * @author goodjava@qq.com
 * @date 2023/4/20 17:22
 */
public class ActionEventUtils {


    /**
     * 进行 Java 应用配置的设置与执行
     * 获取数据上下文和项目
     * 创建运行管理器、配置工厂、配置设置等
     * 设置配置的主类名和程序参数
     * 添加和选择配置
     * 获取执行器，构建执行环境
     * 获取程序运行器，若不为空则异步执行
     */
    public static void setupAndExecuteJavaAppConfiguration(String className, String params, Project _project) {
        Project project = null;

        // 获取当前的数据上下文
        if (null == _project) {
            DataContext dataContext = DataManager.getInstance().getDataContext();
            project = CommonDataKeys.PROJECT.getData(dataContext);
        } else {
            project = _project;
        }

        if (project == null) {
            return; // 如果没有项目，直接返回
        }

        // 获取 RunManager
        RunManager runManager = RunManager.getInstance(project);

        // 创建一个新的 Java 应用程序运行配置
        ConfigurationFactory factory = ApplicationConfigurationType.getInstance().getConfigurationFactories()[0];
        RunnerAndConfigurationSettings configurationSettings = runManager.createConfiguration("MioneRun", factory);
        ApplicationConfiguration applicationConfiguration = (ApplicationConfiguration) configurationSettings.getConfiguration();

        // 设置 main 函数的参数
        applicationConfiguration.setMainClassName(className); // 设置主类名
        applicationConfiguration.setProgramParameters(params); // 设置参数

        // 将新的运行配置添加到 RunManager
        runManager.addConfiguration(configurationSettings);
        runManager.setSelectedConfiguration(configurationSettings);

        Executor executor = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);

        // 创建 ExecutionEnvironment
        ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.create(executor, configurationSettings.getConfiguration());
        ExecutionEnvironment environment = builder.build();

        // 获取 ProgramRunner
        ProgramRunner<?> runner = ProgramRunner.getRunner(DefaultRunExecutor.EXECUTOR_ID, environment.getRunProfile());

        if (runner != null) {
            // 在事件调度线程中执行运行配置
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    runner.execute(environment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


    /**
     * 执行默认的运行操作。
     * 首先获取当前的数据上下文，然后获取默认的运行动作。
     * 如果该动作存在，则创建一个动作事件并执行该动作。
     */
    public static void executeDefaultRunAction() {
        // 获取当前的数据上下文
        DataContext dataContext = DataManager.getInstance().getDataContext();
        // 获取 "Run" 功能对应的 Action
        AnAction runAction = ActionManager.getInstance().getAction(IdeActions.ACTION_DEFAULT_RUNNER);
        // 模拟触发 "Run" 功能的 Action
        if (runAction != null) {
            AnActionEvent event = AnActionEvent.createFromDataContext(
                    ActionPlaces.UNKNOWN, null, dataContext
            );
            runAction.actionPerformed(event);
        }
    }

    /**
     * 启动调试器的方法。首先获取当前数据上下文，然后获取默认的调试器动作。如果该动作存在，则创建一个动作事件并执行该动作。
     */
    public static void startDebugger() {
        // 获取当前的数据上下文
        DataContext dataContext = DataManager.getInstance().getDataContext();
        // 获取 "Run" 功能对应的 Action
        AnAction runAction = ActionManager.getInstance().getAction(IdeActions.ACTION_DEFAULT_DEBUGGER);
        // 模拟触发 "Run" 功能的 Action
        if (runAction != null) {
            AnActionEvent event = AnActionEvent.createFromDataContext(
                    ActionPlaces.UNKNOWN, null, dataContext
            );
            runAction.actionPerformed(event);
        }
    }


    public static AnActionEvent createAnAction(Project project, String data) {
        // 创建一个新的 Presentation 对象
        Presentation presentation = new Presentation();
        // 获取 ActionManager 实例
        ActionManager actionManager = ActionManager.getInstance();

        // 创建一个新的 DataContext 对象
        DataContext dataContext = new MyDataContext(project, data);

        // 创建一个新的 AnActionEvent 对象
        AnActionEvent anActionEvent = new AnActionEvent(
                null,
                dataContext,
                "",
                presentation,
                actionManager,
                0
        );
        return anActionEvent;
    }

    private static class MyDataContext implements DataContext {
        private final Project project;

        private String data;

        public MyDataContext(Project project, String data) {
            this.project = project;
            this.data = data;
        }

        @Override
        public Object getData(String dataId) {
            if (CommonDataKeys.PROJECT.is(dataId)) {
                return project;
            }
            if (dataId.equals(SpeedSearchSupply.SPEED_SEARCH_CURRENT_QUERY.getName())) {
                return this.data;
            }
            // 如果需要支持其他 dataId，可以在此处添加相应的条件
            return null;
        }

    }
}