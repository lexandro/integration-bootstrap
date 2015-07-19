package pipeline

import javaposse.jobdsl.dsl.jobs.*
import component.*
import javaposse.jobdsl.dsl.DslFactory
import environment.*
import steps.*
import project.Project
import tools.DslFactoryProvider

class PipelineBuilder {

    static DslFactory dslFactory;
    static boolean _initialized = false;

    Component component;
    Environment environment;
    Project project;
    PipelineSteps pipelineSteps

    def static builder() {
        if (!_initialized) {
            init();
        }
        //
        return new PipelineBuilder();
    }

    private static void init() {
        dslFactory = DslFactoryProvider.getInstance();
    }

    PipelineBuilder() {
    }

    def project(Project project) {
        this.project = project;
        return this;
    }

    def environment(Project environment) {
        this.environment = environment;
        return this;
    }

    def component(Component component) {
        this.component = component;
        return this;
    }

    def pipelineSteps(PipelineSteps pipelineSteps) {
        this.pipelineSteps = pipelineSteps;
        return this;
    }

    def build() {
        pipelineSteps.configure(project, component);
        processSteps(pipelineSteps.firstStep, null);
    }

    private def processSteps(PipelineStep step, PipelineStep parentStep) {
        // 1. create step
        // 2. link to the parent
        // 3. recursively process next steps
        //
        step.createJob(parentStep)
        if (step.nextSteps.size() > 0) {
            for (PipelineStep nextStep : step.nextSteps) {
                processSteps(nextStep, step);
            }
        }
    }
}