package steps

import javaposse.jobdsl.dsl.DslFactory
import project.Project
import environment.Environment
import component.Component
import tools.DslFactoryProvider

abstract class PipelineStep {

    protected static DslFactory dslFactory;

    public def name = 'Abstract step';

    public LinkedList<PipelineStep> nextSteps = new LinkedList<>();

    protected Project project
    protected Environment environment
    protected Component component

    PipelineStep(Project project, Environment environment, Component component) {
        dslFactory = DslFactoryProvider.getInstance();
        this.project = project
        this.environment = environment
        this.component = component
    }

    static def newInstance(Project project, Environment environment, Component component) {
        println("newInstance is unimplemented in abstract class PipelineStep");
    };

    abstract def getJobName();

    abstract def createJob(PipelineStep parentStep);

}