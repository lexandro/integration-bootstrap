package steps

import project.Project
import environment.Environment
import component.Component

class CompileStep extends PipelineStep {

    private CompileStep(Project project, Environment environment, Component component) {
        super(project, environment, component);
        name = 'compile';
    }

    def newInstance(Project project, Environment environment, Component component) {
        return new CompileStep(project, environment, component);
    }

    def getJobName() {
        return sprintf('%s-%s-%s-02-compile', project.namePrefix, component.name, environment.namePrefix);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Quick code health check.'
            deliveryPipelineConfiguration("Build", "compile")
            publishers {
                publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
                for (PipelineStep nextStep : nextSteps) {
                    downstream nextStep.getJobName(), 'SUCCESS'
                }
            }
            scm {
                cloneWorkspace parentStep.getJobName(), 'Any'
            }
            steps {
                maven('compile')
            }
        }
    }
}