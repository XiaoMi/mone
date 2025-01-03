package run.mone.hive.actions;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.Message;

/**
 * Action for creating technical design documentation
 */
@Slf4j
public class WriteDesign extends Action {

    public WriteDesign() {
        super("Create technical design documentation");
    }

    @Override
    public CompletableFuture<Message> run(Map<String, Object> map) {
        return CompletableFuture.supplyAsync(() -> {
            Message message = (Message) map.get("message");
            log.info("Creating technical design from message: {}", message);
            StringBuilder design = new StringBuilder();
            design.append("# Technical Design Document\n\n");
            design.append("## System Architecture\n");
            design.append("The system follows a layered architecture with the following components:\n\n");
            design.append("1. Presentation Layer\n");
            design.append("2. Business Logic Layer\n");
            design.append("3. Data Access Layer\n\n");
            design.append("## Component Design\n");
            design.append("### Core Components\n");
            design.append("- User Management\n");
            design.append("- Authentication Service\n");
            design.append("- Data Processing Engine\n");
            return new Message(design.toString());
        });
    }
} 