import { commands, Config, Job, reusable } from '@circleci/circleci-config-sdk';
import { Command } from '@circleci/circleci-config-sdk/dist/src/lib/Components/Commands/exports/Command';
import { NodeLtsExecutor } from '../../executors';
import { NotifyOnFailureCommand, WebuiInstallCommand } from '../../commands';
import { orbs } from '../../orbs';
import { config } from '../../config';

export class ChromaticConsoleJob {
  private static jobName = 'job-console-webui-build-storybook';

  public static create(dynamicConfig: Config): Job {
    dynamicConfig.importOrb(orbs.keeper);
    dynamicConfig.importOrb(orbs.github);

    const webUiInstallCommand = WebuiInstallCommand.get();
    dynamicConfig.addReusableCommand(webUiInstallCommand);

    const notifyOnFailureCommand = NotifyOnFailureCommand.get(dynamicConfig);
    dynamicConfig.addReusableCommand(notifyOnFailureCommand);

    const steps: Command[] = [
      new commands.Checkout(),
      new commands.workspace.Attach({ at: '.' }),
      new reusable.ReusedCommand(webUiInstallCommand, { 'apim-ui-project': 'gravitee-apim-console-webui' }),
      new reusable.ReusedCommand(orbs.keeper.commands['env-export'], {
        'secret-url': config.secrets.githubApiToken,
        'var-name': 'GITHUB_TOKEN',
      }),
      new reusable.ReusedCommand(orbs.keeper.commands['env-export'], {
        'secret-url': config.secrets.chromaticProjectToken,
        'var-name': 'CHROMATIC_PROJECT_TOKEN',
      }),
      // TODO:
      //  - Handle npx chromatic command failure, make the job fails
      //  - Create a new project in Chromatic and update the token
      new commands.Run({
        name: 'Running Chromatic',
        command: `SB_URL=$(cd gravitee-apim-console-webui && npx chromatic --project-token=$CHROMATIC_PROJECT_TOKEN --exit-once-uploaded -d=storybook-static | grep -o "View your Storybook at https:\\/\\/[0-9a-z-]*\\.chromatic\\.com" | grep -o "https:.*")
echo "export SB_URL=$SB_URL" >> $BASH_ENV`,
      }),
      new reusable.ReusedCommand(orbs.github.commands['setup']),
      new commands.Run({
        name: 'Edit Pull Request Description',
        command: `# First check there is an associated pull request, otherwise just stop the job here
if ! gh pr view --json title;
then
  echo "No PR found for this branch, stopping the job here."
  exit 0
fi

# If PR state is different from OPEN
if [ "$(gh pr view --json state --jq .state)" != "OPEN" ];
then
  echo "PR is not opened, stopping the job here."
  exit 0
fi

export PR_BODY_STORYBOOK_SECTION="
<!-- Storybook placeholder -->
---

📚&nbsp;&nbsp;View the storybook of this branch [here](\${SB_URL})
<!-- Storybook placeholder end -->
"

export CLEAN_BODY=$(gh pr view --json body --jq .body | sed '/Storybook placeholder -->/,/Storybook placeholder end -->/d')

gh pr edit --body "$CLEAN_BODY$PR_BODY_STORYBOOK_SECTION"`,
      }),
      new reusable.ReusedCommand(notifyOnFailureCommand),
    ];

    return new Job(ChromaticConsoleJob.jobName, NodeLtsExecutor.create('small'), steps);
  }
}
