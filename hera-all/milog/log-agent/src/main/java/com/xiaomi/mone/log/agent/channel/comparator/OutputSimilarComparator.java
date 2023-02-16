package com.xiaomi.mone.log.agent.channel.comparator;

import com.xiaomi.mone.log.agent.export.Output;
import com.xiaomi.mone.log.agent.export.RmqOutput;
import com.xiaomi.mone.log.agent.export.TalosOutput;

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
        boolean isSimilar = false;
        String type = newOutput.getOutputType();
        switch (type) {
            case Output.OUTPUT_ROCKETMQ:
                isSimilar = mqSimilarCompare(newOutput);
                break;
            case Output.OUTPUT_TALOS:
                isSimilar = talosSimilarCompare(newOutput);
                break;
            default:
                break;
        }
        return isSimilar;
    }

    private boolean mqSimilarCompare(Output newOutput) {
        if (!oldOutput.getOutputType().equals(newOutput.getOutputType())) {
            return false;
        }
        RmqOutput newRmqOutput = (RmqOutput) newOutput;
        RmqOutput oldRmqOutput = (RmqOutput) oldOutput;
        if (newRmqOutput.equals(oldRmqOutput)) {
            return true;
        }
        return false;
    }

    private boolean talosSimilarCompare(Output newOutput) {
        if (!oldOutput.getOutputType().equals(newOutput.getOutputType())) {
            return false;
        }
        TalosOutput newTalosOutput = (TalosOutput) newOutput;
        TalosOutput oldTalosOutput = (TalosOutput) oldOutput;
        if (newTalosOutput.equals(oldTalosOutput)) {
            return true;
        }
        return false;
    }

}
