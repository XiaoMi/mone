package run.mone.ai.gpt.bo;

import lombok.Data;

import java.util.List;

@Data
public class ResponsePayload {
    private List<Choice> choices;
    private long created;
    private String id;
    private String model;
    private String object;
    private List<PromptFilterResult> prompt_filter_results;
    private String system_fingerprint;
    private Usage usage;
    private Error error;

    @Data
    public static class Error {
        private String code;
        private String message;
    }

    @Data
    public static class Choice {
        private ContentFilterResults content_filter_results;
        private String finish_reason;
        private int index;
        private Message message;


        @Data
        public static class ContentFilterResults {
            private FilterResult hate;
            private FilterResult self_harm;
            private FilterResult sexual;
            private FilterResult violence;

        }

        @Data
        public static class FilterResult {
            private boolean filtered;
            private String severity;

        }

        @Data
        public static class Message {
            private String content;
            private String role;

        }
    }

    @Data
    public static class PromptFilterResult {
        private int prompt_index;
        private ContentFilterResult content_filter_result;


        @Data
        public static class ContentFilterResult {
            private JailbreakResult jailbreak;
            private Choice.FilterResult sexual;
            private Choice.FilterResult violence;
            private Choice.FilterResult hate;
            private Choice.FilterResult self_harm;


            @Data
            public static class JailbreakResult {
                private boolean filtered;
                private boolean detected;

            }
        }
    }

    @Data
    public static class Usage {
        private int completion_tokens;
        private int prompt_tokens;
        private int total_tokens;
    }
}
