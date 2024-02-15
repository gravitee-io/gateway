/*
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { commands, Config, Job, workflow, Workflow } from '@circleci/circleci-config-sdk';

import { CircleCIEnvironment } from '../pipelines';
import { isE2EBranch, isSupportBranchOrMaster } from '../utils';
import { config } from '../config';
import { BaseExecutor } from '../executors';
import {
  BuildBackendImagesJob,
  BuildBackendJob,
  ChromaticConsoleJob,
  CommunityBuildBackendJob,
  DangerJsJob,
  DeployOnAzureJob,
  E2ECypressJob,
  E2EGenerateSDKJob,
  E2ELintBuildJob,
  E2ETestJob,
  PerfLintBuildJob,
  PublishJob,
  ReleaseHelmJob,
  SetupJob,
  SnykApimChartsJob,
  SonarCloudAnalysisJob,
  StorybookConsoleJob,
  TestApimChartsJob,
  TestDefinitionJob,
  TestGatewayJob,
  TestIntegrationJob,
  TestPluginJob,
  TestRepositoryJob,
  TestRestApiJob,
  ValidateJob,
  WebuiBuildJob,
  WebuiLintTestJob,
} from '../jobs';

export class PullRequestsWorkflow {
  static create(dynamicConfig: Config, environment: CircleCIEnvironment): Workflow {
    let jobs: workflow.WorkflowJob[] = [];
    // Needed to publish helm chart in internal repository
    environment.isDryRun = true;
    if (isSupportBranchOrMaster(environment.branch)) {
      jobs.push(
        ...this.getCommonJobs(dynamicConfig, environment, false, false),
        ...this.getE2EJobs(dynamicConfig, environment),
        ...this.getMasterAndSupportJobs(dynamicConfig, environment),
      );
    } else if (isE2EBranch(environment.branch)) {
      jobs.push(...this.getCommonJobs(dynamicConfig, environment, false, true), ...this.getE2EJobs(dynamicConfig, environment));
    } else {
      jobs = this.getCommonJobs(dynamicConfig, environment, true, true);
    }
    return new Workflow('pull_requests', jobs);
  }

  private static getCommonJobs(
    dynamicConfig: Config,
    environment: CircleCIEnvironment,
    filterJobs: boolean,
    addValidationJob: boolean,
  ): workflow.WorkflowJob[] {
    const jobs: workflow.WorkflowJob[] = [];
    const requires: string[] = [];

    if (!filterJobs || shouldBuildAll(environment.changedFiles) || shouldBuildHelm(environment.changedFiles)) {
      const apimChartsTestJob = TestApimChartsJob.create(dynamicConfig);
      dynamicConfig.addJob(apimChartsTestJob);
      jobs.push(new workflow.WorkflowJob(apimChartsTestJob, { name: 'Helm Chart - Lint & Test' }));

      requires.push('Helm Chart - Lint & Test');
    }

    if (!filterJobs || shouldBuildAll(environment.changedFiles) || shouldBuildBackend(environment.changedFiles)) {
      const setupJob = SetupJob.create(dynamicConfig);
      dynamicConfig.addJob(setupJob);

      const validateBackendJob = ValidateJob.create(dynamicConfig);
      dynamicConfig.addJob(validateBackendJob);

      const dangerJSJob = DangerJsJob.create(dynamicConfig);
      dynamicConfig.addJob(dangerJSJob);

      const buildBackendJob = BuildBackendJob.create(dynamicConfig, environment);
      dynamicConfig.addJob(buildBackendJob);

      const testDefinitionJob = TestDefinitionJob.create(dynamicConfig);
      dynamicConfig.addJob(testDefinitionJob);

      const testGatewayJob = TestGatewayJob.create(dynamicConfig);
      dynamicConfig.addJob(testGatewayJob);

      const testRestApiJob = TestRestApiJob.create(dynamicConfig);
      dynamicConfig.addJob(testRestApiJob);

      const testIntegrationJob = TestIntegrationJob.create(dynamicConfig);
      dynamicConfig.addJob(testIntegrationJob);

      const testPluginsJob = TestPluginJob.create(dynamicConfig);
      dynamicConfig.addJob(testPluginsJob);

      const testRepositoryJob = TestRepositoryJob.create(dynamicConfig);
      dynamicConfig.addJob(testRepositoryJob);

      const sonarCloudAnalysisJob = SonarCloudAnalysisJob.create(dynamicConfig, environment);
      dynamicConfig.addJob(sonarCloudAnalysisJob);

      jobs.push(
        new workflow.WorkflowJob(setupJob, { name: 'Setup', context: config.jobContext }),
        new workflow.WorkflowJob(validateBackendJob, {
          name: 'Validate backend',
          context: config.jobContext,
          requires: ['Setup'],
        }),
        new workflow.WorkflowJob(dangerJSJob, {
          name: 'Run Danger JS',
          context: config.jobContext,
          requires: ['Validate backend'],
        }),
        new workflow.WorkflowJob(buildBackendJob, {
          name: 'Build backend',
          context: config.jobContext,
          requires: ['Validate backend'],
        }),
        new workflow.WorkflowJob(testDefinitionJob, {
          name: 'Test definition',
          context: config.jobContext,
          requires: ['Build backend'],
        }),
        new workflow.WorkflowJob(testGatewayJob, {
          name: 'Test gateway',
          context: config.jobContext,
          requires: ['Build backend'],
        }),
        new workflow.WorkflowJob(testRestApiJob, {
          name: 'Test rest-api',
          context: config.jobContext,
          requires: ['Build backend'],
        }),
        new workflow.WorkflowJob(testIntegrationJob, {
          name: 'Integration tests',
          context: config.jobContext,
          requires: ['Build backend'],
        }),
        new workflow.WorkflowJob(testPluginsJob, {
          name: 'Test plugins',
          context: config.jobContext,
          requires: ['Build backend'],
        }),
        new workflow.WorkflowJob(testRepositoryJob, {
          name: 'Test repository',
          context: config.jobContext,
          requires: ['Build backend'],
        }),
        new workflow.WorkflowJob(sonarCloudAnalysisJob, {
          name: 'Sonar - gravitee-apim-definition',
          context: config.jobContext,
          requires: ['Test definition'],
          working_directory: 'gravitee-apim-definition',
        }),
        new workflow.WorkflowJob(sonarCloudAnalysisJob, {
          name: 'Sonar - gravitee-apim-gateway',
          context: config.jobContext,
          requires: ['Test gateway'],
          working_directory: 'gravitee-apim-gateway',
        }),
        new workflow.WorkflowJob(sonarCloudAnalysisJob, {
          name: 'Sonar - gravitee-apim-rest-api',
          context: config.jobContext,
          requires: ['Test rest-api'],
          working_directory: 'gravitee-apim-rest-api',
        }),

        new workflow.WorkflowJob(sonarCloudAnalysisJob, {
          name: 'Sonar - gravitee-apim-plugin',
          context: config.jobContext,
          requires: ['Test plugins'],
          working_directory: 'gravitee-apim-plugin',
        }),
        new workflow.WorkflowJob(sonarCloudAnalysisJob, {
          name: 'Sonar - gravitee-apim-repository',
          context: config.jobContext,
          requires: ['Test repository'],
          working_directory: 'gravitee-apim-repository',
        }),
      );

      requires.push('Test definition', 'Test gateway', 'Test plugins', 'Test repository', 'Test rest-api');
    }

    if (!filterJobs || shouldBuildAll(environment.changedFiles) || shouldBuildConsole(environment.changedFiles)) {
      const webuiLintTestJob = WebuiLintTestJob.create(dynamicConfig);
      dynamicConfig.addJob(webuiLintTestJob);

      const webuiBuildJob = WebuiBuildJob.create(dynamicConfig, environment);
      dynamicConfig.addJob(webuiBuildJob);

      const storybookConsoleJob = StorybookConsoleJob.create(dynamicConfig);
      dynamicConfig.addJob(storybookConsoleJob);

      const chromaticConsoleJob = ChromaticConsoleJob.create(dynamicConfig);
      dynamicConfig.addJob(chromaticConsoleJob);

      const sonarCloudAnalysisJob = SonarCloudAnalysisJob.create(dynamicConfig, environment);
      dynamicConfig.addJob(sonarCloudAnalysisJob);

      jobs.push(
        new workflow.WorkflowJob(webuiLintTestJob, {
          name: 'Lint & test APIM Console',
          context: config.jobContext,
          'apim-ui-project': config.dockerImages.console.project,
          resource_class: 'large',
        }),
        new workflow.WorkflowJob(webuiBuildJob, {
          name: 'Build APIM Console and publish image',
          context: config.jobContext,
          'apim-ui-project': config.dockerImages.console.project,
          'docker-image-name': config.dockerImages.console.image,
        }),
        new workflow.WorkflowJob(storybookConsoleJob, {
          name: 'Build Console Storybook',
          context: config.jobContext,
        }),
        new workflow.WorkflowJob(chromaticConsoleJob, {
          name: 'Deploy console in chromatic',
          context: config.jobContext,
          requires: ['Build Console Storybook'],
        }),
        new workflow.WorkflowJob(sonarCloudAnalysisJob, {
          name: 'Sonar - gravitee-apim-console-webui',
          context: config.jobContext,
          requires: ['Lint & test APIM Console'],
          working_directory: config.dockerImages.console.project,
        }),
      );

      requires.push('Lint & test APIM Console', 'Build APIM Console and publish image');
    }

    if (!filterJobs || shouldBuildAll(environment.changedFiles) || shouldBuildPortal(environment.changedFiles)) {
      const webuiLintTestJob = WebuiLintTestJob.create(dynamicConfig);
      dynamicConfig.addJob(webuiLintTestJob);

      const webuiBuildJob = WebuiBuildJob.create(dynamicConfig, environment);
      dynamicConfig.addJob(webuiBuildJob);

      const sonarCloudAnalysisJob = SonarCloudAnalysisJob.create(dynamicConfig, environment);
      dynamicConfig.addJob(sonarCloudAnalysisJob);

      jobs.push(
        new workflow.WorkflowJob(webuiLintTestJob, {
          name: 'Lint & test APIM Portal',
          context: config.jobContext,
          'apim-ui-project': config.dockerImages.portal.project,
          resource_class: 'large',
          node_version: '20.9',
        }),
        new workflow.WorkflowJob(webuiBuildJob, {
          name: 'Build APIM Portal and publish image',
          context: config.jobContext,
          'apim-ui-project': config.dockerImages.portal.project,
          'docker-image-name': config.dockerImages.portal.image,
          node_version: '20.9',
        }),
        new workflow.WorkflowJob(sonarCloudAnalysisJob, {
          name: 'Sonar - gravitee-apim-portal-webui',
          context: config.jobContext,
          requires: ['Lint & test APIM Portal'],
          working_directory: config.dockerImages.portal.project,
        }),
      );

      requires.push('Lint & test APIM Portal', 'Build APIM Portal and publish image');
    }

    // compute check-workflow job
    if (addValidationJob && requires.length > 0) {
      const checkWorkflowJob = new Job('job-validate-workflow-status', BaseExecutor.create('small'), [
        new commands.Run({
          name: 'Check workflow jobs',
          command: 'echo "Congratulations! If you can read this, everything is OK"',
        }),
      ]);
      dynamicConfig.addJob(checkWorkflowJob);
      jobs.push(new workflow.WorkflowJob(checkWorkflowJob, { name: 'Validate workflow status', requires }));
    }

    return jobs;
  }

  private static getE2EJobs(dynamicConfig: Config, environment: CircleCIEnvironment): workflow.WorkflowJob[] {
    const buildImagesJob = BuildBackendImagesJob.create(dynamicConfig, environment);
    dynamicConfig.addJob(buildImagesJob);

    const e2eGenerateSdkJob = E2EGenerateSDKJob.create(dynamicConfig);
    dynamicConfig.addJob(e2eGenerateSdkJob);

    const e2eLintBuildJob = E2ELintBuildJob.create(dynamicConfig);
    dynamicConfig.addJob(e2eLintBuildJob);

    const e2eTestJob = E2ETestJob.create(dynamicConfig, environment);
    dynamicConfig.addJob(e2eTestJob);

    const e2eCypressJob = E2ECypressJob.create(dynamicConfig, environment);
    dynamicConfig.addJob(e2eCypressJob);

    const perfLintBuildJob = PerfLintBuildJob.create(dynamicConfig);
    dynamicConfig.addJob(perfLintBuildJob);

    return [
      new workflow.WorkflowJob(buildImagesJob, {
        name: 'Build and push rest api and gateway images',
        context: config.jobContext,
        requires: ['Build backend'],
      }),
      new workflow.WorkflowJob(e2eGenerateSdkJob, {
        context: config.jobContext,
        name: 'Generate e2e tests SDK',
        requires: ['Build backend'],
      }),
      new workflow.WorkflowJob(e2eLintBuildJob, {
        context: config.jobContext,
        name: 'Lint & Build APIM e2e',
        requires: ['Generate e2e tests SDK'],
      }),
      new workflow.WorkflowJob(perfLintBuildJob, {
        context: config.jobContext,
        name: 'Lint & Build APIM perf',
        requires: ['Generate e2e tests SDK'],
      }),
      new workflow.WorkflowJob(e2eTestJob, {
        context: config.jobContext,
        name: 'E2E - << matrix.execution_mode >> - << matrix.database >>',
        requires: ['Lint & Build APIM e2e', 'Build and push rest api and gateway images'],
        matrix: {
          execution_mode: ['v3', 'v4-emulation-engine'],
          database: ['mongo', 'jdbc', 'bridge'],
        },
      }),
      new workflow.WorkflowJob(e2eCypressJob, {
        context: config.jobContext,
        name: 'Run Cypress UI tests',
        requires: [
          'Lint & Build APIM e2e',
          'Build and push rest api and gateway images',
          'Build APIM Console and publish image',
          'Build APIM Portal and publish image',
        ],
      }),
    ];
  }

  private static getMasterAndSupportJobs(dynamicConfig: Config, environment: CircleCIEnvironment): workflow.WorkflowJob[] {
    const communityBuildJob = CommunityBuildBackendJob.create(dynamicConfig);
    dynamicConfig.addJob(communityBuildJob);

    const snykApimChartsJob = SnykApimChartsJob.create(dynamicConfig, environment);
    dynamicConfig.addJob(snykApimChartsJob);

    const publishOnArtifactoryJob = PublishJob.create(dynamicConfig, 'artifactory');
    dynamicConfig.addJob(publishOnArtifactoryJob);

    const publishOnNexusJob = PublishJob.create(dynamicConfig, 'nexus');
    dynamicConfig.addJob(publishOnNexusJob);

    const releaseHelmDryRunJob = ReleaseHelmJob.create(dynamicConfig, environment);
    dynamicConfig.addJob(releaseHelmDryRunJob);

    const deployOnAzureJob = DeployOnAzureJob.create(dynamicConfig, environment);
    dynamicConfig.addJob(deployOnAzureJob);

    return [
      new workflow.WorkflowJob(communityBuildJob, { name: 'Check build as Community user', context: config.jobContext }),
      new workflow.WorkflowJob(snykApimChartsJob, { name: 'Scan snyk Helm chart', context: config.jobContext, requires: ['Setup'] }),
      new workflow.WorkflowJob(releaseHelmDryRunJob, {
        name: 'Publish Helm chart (internal repo)',
        context: config.jobContext,
        requires: ['Setup'],
      }),
      new workflow.WorkflowJob(publishOnArtifactoryJob, {
        name: 'Publish on artifactory',
        context: config.jobContext,
        requires: ['Test definition', 'Test gateway', 'Test plugins', 'Test repository', 'Test rest-api'],
      }),
      new workflow.WorkflowJob(publishOnNexusJob, {
        name: 'Publish on nexus',
        context: config.jobContext,
        requires: ['Test definition', 'Test gateway', 'Test plugins', 'Test repository', 'Test rest-api'],
      }),
      new workflow.WorkflowJob(deployOnAzureJob, {
        name: 'Deploy on Azure cluster',
        context: config.jobContext,
        requires: [
          'Test definition',
          'Test gateway',
          'Test plugins',
          'Test repository',
          'Test rest-api',
          'Build and push rest api and gateway images',
          'Build APIM Console and publish image',
          'Build APIM Portal and publish image',
        ],
      }),
    ];
  }
}

export function shouldBuildConsole(changedFiles: string[]): boolean {
  return changedFiles.some((file) => file.includes(config.dockerImages.console.project));
}

export function shouldBuildPortal(changedFiles: string[]): boolean {
  return changedFiles.some((file) => file.includes(config.dockerImages.portal.project));
}

export function shouldBuildBackend(changedFiles: string[]): boolean {
  const mavenProjectsIdentifiers = [
    'gravitee-apim-definition',
    'gravitee-apim-distribution',
    'gravitee-apim-gateway',
    'gravitee-apim-integration-tests',
    'gravitee-apim-plugin',
    'gravitee-apim-repository',
    'gravitee-apim-rest-api',
  ];
  return changedFiles.some((file) => mavenProjectsIdentifiers.some((identifier) => file.includes(identifier)));
}

export function shouldBuildHelm(changedFiles: string[]): boolean {
  return changedFiles.some((file) => file.includes('helm'));
}

export function shouldBuildAll(changedFiles: string[]): boolean {
  const baseDepsIdentifiers = [/^.circleci/, /^pom.xml$/, /^.gitignore$/, /^.prettierrc$/, /^gravitee-apim-e2e/];
  return changedFiles.some((file) => baseDepsIdentifiers.some((identifier) => identifier.test(file)));
}
