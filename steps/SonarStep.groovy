package steps

import project.Project
import environment.Environment
import component.Component

class SonarStep extends PipelineStep {

    private SonarStep(Project project, Environment environment, Component component) {
        super(project, environment, component);
        name = 'sonar';
    }

    def newInstance(Project project, Environment environment, Component component) {
        return new SonarStep(project, environment, component);
    }

    def getJobName() {
        return sprintf('%s-%s-%s-05-sonar', project.namePrefix, component.name, environment.namePrefix);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Quality check'
            deliveryPipelineConfiguration("Build", "quality check")
            scm {
                cloneWorkspace parentStep.getJobName(), 'Any'
            }
            steps {
                maven('sonar:sonar')
            }
            publishers {
                publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
                for (PipelineStep nextStep : nextSteps) {
                    downstream nextStep.getJobName(), 'SUCCESS'
                }
            }
        }
    }
}