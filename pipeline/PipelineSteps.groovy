package pipeline

import project.Project
import component.Component
import environment.Environment
import steps.PipelineStep

abstract class PipelineSteps {

    protected PipelineStep firstStep;
    //
    protected Project project
    protected Environment environment;
    protected Component component

    PipelineSteps() {
    }

    PipelineStep getFirstStep() {
        return firstStep
    }

    def configure(Project project, Environment environment, Component component) {
        this.project = project
        this.environment = environment
        this.component = component
        createStepTree();
    }

    abstract def createStepTree();

    static PipelineSteps getInstance() {
    };
}
