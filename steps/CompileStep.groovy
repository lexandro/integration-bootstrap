package steps

import component.Component
import project.Project

class CompileStep extends PipelineStep {

    private CompileStep(Project project, Component component) {
        super(project, component);
        name = 'compile';
    }

    def newInstance(Project project, Component component) {
        return new CompileStep();
    }

    def getJobName() {
        return sprintf('%s-%s-02-compile', project.prefix, component.name);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Quick code health check.'
            label project.build_agent_label
            deliveryPipelineConfiguration("Build", "compile")
            publishers {
                publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
                for (PipelineStep nextStep : nextSteps) {
                    downstream nextStep.getJobName(), 'SUCCESS'
                }
//                mailer(ProjectData.notification_email, true, false)
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