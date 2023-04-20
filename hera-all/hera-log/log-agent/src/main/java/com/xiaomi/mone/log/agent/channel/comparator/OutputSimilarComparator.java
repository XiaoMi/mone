package com.xiaomi.mone.log.agent.channel.comparator;

import com.xiaomi.mone.log.agent.factory.OutPutServiceFactory;
import com.xiaomi.mone.log.agent.output.Output;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/15 10:08
 */
public class OutputSimilarComparator implements SimilarComparator<Output> {

    private Output oldOutput;

    public OutputSimilarComparator(Output oldOutput) {
        this.oldOutput = oldOutput;
    }

    @Override
    public boolean compare(Output newOutput) {
        if (null == oldOutput) {
            return false;
        }
        if (oldOutput == newOutput) {
            return true;
        }
        return OutPutServiceFactory.getOutPutService(newOutput.getServiceName()).compare(oldOutput, newOutput);
    }

}
