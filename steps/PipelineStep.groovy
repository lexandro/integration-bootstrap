package steps

import component.Component
import javaposse.jobdsl.dsl.DslFactory
import project.Project
import tools.DslFactoryProvider

abstract class PipelineStep {

    protected static DslFactory dslFactory;

    public def name = 'Abstract step';

    public LinkedList<PipelineStep> nextSteps = new LinkedList<>();

    protected Project project
    protected Component component

    PipelineStep(Project project, Component component) {
        dslFactory = DslFactoryProvider.getInstance();
        this.component = component
        this.project = project
    }

    static def newInstance(Project project, Component component) {

    };

    abstract def getJobName();

    abstract def createJob(PipelineStep parentStep);

}