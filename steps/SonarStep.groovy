package steps

import component.Component
import project.Project

class SonarStep extends PipelineStep {

    private SonarStep(Project project, Component component) {
        super(project, component);
        name = 'sonar';
    }

    def newInstance(Project project, Component component) {
        return new SonarStep();
    }

    def getJobName() {
        return sprintf('%s-%s-sonar', project.prefix, component.name);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Getting the source code for further processing'
            label project.build_agent_label
            deliveryPipelineConfiguration("Build", "quality check")

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