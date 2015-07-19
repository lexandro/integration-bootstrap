package tools

import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.DslFactory
import tools.DslFactoryProvider


public class Views {

    static public def createView(String viewName, String regexPatternPrefix, String viewDescription) {
        DslFactoryProvider.getInstance().listView(viewName) {
            description viewDescription
            jobs {
                regex(regexPatternPrefix + '(.*?)')
            }
            columns {
                status()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
        }
    }
}
