package steps

import component.Component
import project.Project

class CheckoutStep extends PipelineStep {

    private CheckoutStep(Project project, Component component) {
        super(project, component);
        name = 'checkout';
    }

    def newInstance(Project project, Component component) {
        return new CheckoutStep();
    }


    def getJobName() {
        return sprintf('%s-%s-01-clone', project.prefix, component.name);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Getting the source code for further processing'
            label project.build_agent_label
            deliveryPipelineConfiguration("Build", "clone")
            publishers {
                publishCloneWorkspace '**', '', 'Any', 'TAR', true, null
                for (PipelineStep nextStep : nextSteps) {
                    downstream nextStep.getJobName(), 'SUCCESS'
                }
            }
            scm {
                git {
                    remote {
                        url component.scmUrl;
                        credentials project.scmCredentials;
                    }
                    branch 'master'
                    shallowClone true
                }
            }
            triggers {
            }
        }
    }
}