package pipeline

import component.Component
import steps.PipelineStep
import project.Project

abstract class PipelineSteps {

    protected PipelineStep firstStep;
    protected Project project
    protected Component component

    PipelineSteps() {
    }

    PipelineStep getFirstStep() {
        return firstStep
    }

    def configure(Project project, Component component) {
        this.component = component
        this.project = project
        createStepTree();
    }

    abstract def createStepTree();

    static PipelineSteps getInstance() {
    };
}
