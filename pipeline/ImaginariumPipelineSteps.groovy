package pipeline

import steps.*
import steps.PipelineStep
import project.Project
import environment.Environment
import component.Component

class ImaginariumPipelineSteps extends PipelineSteps {

    private static ImaginariumPipelineSteps _instance;

    ImaginariumPipelineSteps() {
    }

    def createStepTree() {
        // initializing the step instances
        def checkout = CheckoutStep.newInstance(project, environment, component);
        def compile = CompileStep.newInstance(project, environment, component);
        def deploy = DeployStep.newInstance(project, environment, component);
        def dockerImage = DockerImageStep.newInstance(project, environment, component);
        def sonar = SonarStep.newInstance(project, environment, component);
        def release = ReleaseStep.newInstance(project, environment, component);
//
        // building the step tree;
        checkout.nextSteps.add(compile);
        compile.nextSteps.add(deploy);
        deploy.nextSteps.add(dockerImage);
        deploy.nextSteps.add(sonar);
        dockerImage.nextSteps.add(release);
        //
        firstStep = checkout;

    }

    static PipelineSteps getInstance() {
        if (_instance == null) {
            _instance = new ImaginariumPipelineSteps();
        }
        return _instance;
    }


}
