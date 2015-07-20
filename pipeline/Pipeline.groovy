package pipeline

import component.Component
import environment.Environment
import project.Project
import steps.*
import tools.*

class Pipeline {

    Project project;
    Environment environment;
    Component component;
    PipelineSteps pipelineSteps;

    def static builder() {
        return new PipelineBuilder();
    }
    //
    def generate() {
        pipelineSteps.configure(project, environment, component);
        processSteps(pipelineSteps.firstStep);
        Views.deliveryPipelineView(project.name + ' delivery pipeline', sprintf('%s-%s-%s-01-clone', project.namePrefix, component.name, environment.namePrefix), pipelineSteps.firstStep.getJobName());


    }

    private def processSteps(PipelineStep step) {
        processSteps(step, null);
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

    //
    static class PipelineBuilder {

        Pipeline _instance;

        PipelineBuilder() {
            _instance = new Pipeline();
        }

        def project(Project project) {
            _instance.project = project;
            return this;
        }

        def environment(Environment environment) {
            _instance.environment = environment;
            return this;
        }

        def component(Component component) {
            _instance.component = component;
            return this;
        }

        def pipelineSteps(PipelineSteps pipelineSteps) {
            _instance.pipelineSteps = pipelineSteps;
            return this;
        }

        def build() {
            return _instance;
        }

    }

}

