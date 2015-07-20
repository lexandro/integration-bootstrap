package tools

import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.DslFactory
import tools.DslFactoryProvider


public class Views {

    static public def deliveryPipelineView(String viewName, String regexPatternPrefix, String initialJobName) {
        DslFactoryProvider.getInstance().deliveryPipelineView(viewName) {
            showAggregatedPipeline true
            enableManualTriggers true
            pipelineInstances 5
            pipelines {
                component(regexPatternPrefix + ' delivery pipeline', initialJobName)
            }
        }
    }
}
