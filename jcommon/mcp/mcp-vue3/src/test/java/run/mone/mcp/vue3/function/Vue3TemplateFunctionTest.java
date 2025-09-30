package run.mone.mcp.vue3.function;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import run.mone.mcp.vue3.service.Vue3TemplateService;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@TestPropertySource(properties = {
    "mcp.agent.name=vue3-test",
    "mcp.grpc.port=9088"
})
public class Vue3TemplateFunctionTest {

    @Test
    public void testGenerateComponent() {
        Vue3TemplateService service = new Vue3TemplateService();
        
        Map<String, Object> params = new HashMap<>();
        params.put("componentName", "TestComponent");
        params.put("useTypeScript", true);
        params.put("usePinia", true);
        
        Flux<String> result = service.generateVue3Component(params);
        
        StepVerifier.create(result)
            .expectNextMatches(content -> 
                content.contains("TestComponent") && 
                content.contains("<template>") &&
                content.contains("<script setup lang=\"ts\">") &&
                content.contains("import { useStore }")
            )
            .verifyComplete();
    }

    @Test
    public void testGeneratePage() {
        Vue3TemplateService service = new Vue3TemplateService();
        
        Map<String, Object> params = new HashMap<>();
        params.put("pageName", "TestPage");
        params.put("useRouter", true);
        
        Flux<String> result = service.generateVue3Page(params);
        
        StepVerifier.create(result)
            .expectNextMatches(content -> 
                content.contains("TestPage") && 
                content.contains("<template>") &&
                content.contains("import { useRouter }")
            )
            .verifyComplete();
    }

    @Test
    public void testGenerateProject() {
        Vue3TemplateService service = new Vue3TemplateService();
        
        Map<String, Object> params = new HashMap<>();
        params.put("projectName", "test-project");
        params.put("useTypeScript", true);
        params.put("useVite", true);
        
        Flux<String> result = service.generateVue3Project(params);
        
        StepVerifier.create(result)
            .expectNextMatches(content -> 
                content.contains("test-project") && 
                content.contains("package.json") &&
                content.contains("vite.config.ts")
            )
            .verifyComplete();
    }
}
