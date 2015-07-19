package pipeline

import steps.*

class ImaginariumPipelineSteps extends PipelineSteps {

    private static ImaginariumPipelineSteps _instance;

    ImaginariumPipelineSteps() {
    }

    def createStepTree() {
        // initializing the step instances

        def checkout = CheckoutStep.newInstance(project, component);
        def compile = CompileStep.newInstance(project, component);
        def deploy = DeployStep.newInstance(project, component);
        def sonar = SonarStep.newInstance(project, component);
        def createDockerImage = DockerImageStep.newInstance(project, component);

        // linking together the step tree;
        checkout.nextSteps.add(compile);
        compile.nextSteps.add(deploy);
        deploy.nextSteps.add(sonar);
        deploy.nextSteps.add(createDockerImage);
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
