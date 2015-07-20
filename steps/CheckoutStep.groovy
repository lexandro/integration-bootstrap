package steps

import component.Component
import environment.Environment
import project.Project

class CheckoutStep extends PipelineStep {

    private CheckoutStep(Project project, Environment environment, Component component) {
        super(project, environment, component);
        name = 'checkout';
    }

    def newInstance(Project project, Environment environment, Component component) {
        return new CheckoutStep(project, environment, component);
    }


    def getJobName() {
        return sprintf('%s-%s-%s-01-clone', project.namePrefix, component.name, environment.namePrefix);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();
        def scmUrl = component.scmUrl;
        def jobCredentials = component.scmCredentials
        //
        dslFactory.job(jobName) {
            description 'Getting the source code for further processing'
            deliveryPipelineConfiguration("Start", "clone")
            scm {
                git {
                    remote {
                        url scmUrl
                        credentials jobCredentials
                    }
                    branch 'master'
                    shallowClone true
                }
            }
            // activate job for github pushes
            configure { project ->
                project / triggers / 'com.cloudbees.jenkins.GitHubPushTrigger' / spec
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