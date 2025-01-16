package run.mone.m78.service.asr.xiaoai.DTO;

public enum XiaoaiDomainEnum {
        PRO("ws://test.com:80"),
        PREV("ws://test.com:80"),
        STAG("ws://test.com:80");

        private String domain;

        XiaoaiDomainEnum(String domain) {
            this.domain = domain;
        }

        public String getDomain() {
            return domain;
        }
    }