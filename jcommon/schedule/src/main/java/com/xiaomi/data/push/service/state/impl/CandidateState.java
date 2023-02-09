package com.xiaomi.data.push.service.state.impl;

import com.xiaomi.data.push.context.ServerContext;
import com.xiaomi.data.push.service.state.BaseState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by zhangzhiyong on 12/06/2018.
 */
@Component
@Scope("prototype")
public class CandidateState extends BaseState {

    private static final Logger logger = LoggerFactory.getLogger(CandidateState.class);

    @Autowired
    private ServerContext serverContext;

    @Override
    public void execute() {
        logger.info("--->state CandidateState");
    }
}
