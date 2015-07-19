package steps

import component.Component
import project.Project

class DeployStep extends PipelineStep {

    private DeployStep(Project project, Component component) {
        super(project, component);
        name = 'Deploy';
    }

    def newInstance(Project project, Component component) {
        return new DeployStep();
    }

    def getJobName() {
        return sprintf('%s-%s-03-deploy', project.prefix, component.name);
    }

    def createJob(PipelineStep parentStep) {
        def jobName = this.getJobName();

        dslFactory.job(jobName) {
            description 'Getting the source code for further processing'
            label project.build_agent_label
            deliveryPipelineConfiguration("Build", "deploy")

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
            /* An ugly XML hacking here*/
            configure { project ->
                project / builders / 'org.jfrog.hudson.maven3.Maven3Builder' {
                    mavenName('Maven3')
                    rootPom('pom.xml')
                    goals('install -DskipTests')
                }
            }

            /*  deploying to artifactory */
            configure { project ->
                project / buildWrappers / 'org.jfrog.hudson.maven3.ArtifactoryMaven3Configurator' {
                    details {
                        artifactoryName(ProjectData.artifactoryServerId)
                        repositoryKey(ProjectData.artifactoryRepositoryKey)
                        snapshotsRepositoryKey(ProjectData.artifactorySnapshotsRepositoryKey)
                        artifactoryUrl(ProjectData.artifactoryServerUrl)
                    }
                    deployBuildInfo(true)
                    deployArtifacts(true)
                }
            }
            triggers {
            }
        }
    }

}