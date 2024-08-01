package run.mone.ultraman.listener;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author goodjava@qq.com
 * @date 2024/5/31 14:51
 */
public class CaretHoverEditorFactoryListener implements EditorFactoryListener {


    public static final Key<CaretHoverPlugin> PLUGIN_KEY = new Key<>("plugin_key");

    public static final Key<AtomicInteger> EDIT_INCR_ID_KEY = new Key<>("edit_incr_id_key");

    //用来记录操作类型的
    public static final Key<AtomicReference<String>> EDIT_ACTION_TYPE = new Key<>("edit_action_type");

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        Project project = editor.getProject();
        if (project != null) {
            CaretHoverPlugin plugin = new CaretHoverPlugin(project, editor);
            editor.putUserData(EDIT_INCR_ID_KEY, new AtomicInteger());
            editor.putUserData(PLUGIN_KEY, plugin);
            editor.putUserData(EDIT_ACTION_TYPE, new AtomicReference<>(""));
        }
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        CaretHoverPlugin plugin = editor.getUserData(PLUGIN_KEY);
        if (plugin != null) {
            // Perform any necessary cleanup in the plugin
            plugin.editorReleased();
            // Remove the plugin from the editor's user data
            editor.putUserData(PLUGIN_KEY, null);
            editor.putUserData(EDIT_INCR_ID_KEY, null);
            editor.putUserData(EDIT_ACTION_TYPE, null);
        }
    }

}
