# Development process

These are the steps that the team follows:

- Find a task to work on (JIRA?) 
- From the `TO DO` column of the current sprint(not strictly required)
- update the ticket status in JIRA to `In progress` and assign it to yourself
- git pull
- create a branch from `master` for work you're about to undertake, never base your work off `WIP` branches, start the branch name with ticket number
- do the work
- if you want to communicate or collaborate on work: push the branch, create a Pull Request (PR) and label it with `WIP` or make it a [draft pull request](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests#draft-pull-requests)
- when you feel it's ready for review push it up and create a PR if you haven't already and remove any `WIP` labels and/or remove the draft status, and move it to `In Review` in JIRA. See section below on how to create a good PR.
- pick as many reviewers as you like from the team to review your PR
- discuss and address their feedback, revise the PR as necessary
- resubmit the PR as necessary until it has at least one approval
- deploy it to staging to test the deploy and verify yourself that fix/feature works
- update the ticket status in JIRA to `In test` if it requires customer acceptance testing or QA
- follow up with Product Owner or other stakeholders for stories requiring customer acceptance testing (new features) and provide instructions on how to test
- follow up with QA for verifying bug fixes
- wait on customer acceptance and QA as necessary
- product or QA team will move accepted stories to `Accepted` column in JIRA or provide feedback and bug reports
- redeploy to staging and renotify stakeholders until it passes customer acceptance/QA testing
- merge the branch to `main`, and then delete the branch, remove the `staging` and `integration` labels from the PR
- deploy to production
- test the code on production
- [enabling feature flags](https://climatesmart.radiclebalance.com/admin/features) will be managed by Product team
- update the ticket status in JIRA to `Done`
  - Note that *the definition of done* is that it's on production, and tested on production
- notify the `Product` channel on Teams about new features, fixes and potential impact of updates
- keep an eye on [Rollbar](https://rollbar.com/ClimateSmartBusinesses/all/items/) for any new exceptions related to the new code

### Variation for long-running feature branches

- we target a feature branch instead of `main`, e.g. past feature branches were `rebrand` and `chargebee`
- we create a PR that tracks the feature branch progress and lists which tickets have been merged into it.
- we deploy to `integration` instead of `staging` for customer acceptance and QA
- once accepted by product / QA we can merge PR down to the feature branch and mark it as `Done` in JIRA even though it's not on `production`
- we ensure that there is a JIRA ticket/epic that is not marked `Done` until the feature branch is merged to `main`, verified on `production`, think of it like a final rollup deployment ticket


## Testing on `staging` and `integration`

We use `staging` for testing of short-lived branches, customer acceptance testing, as well as a smoke test just before deploying to production.

We use `integration`for integration of long-lived branches and sharing progress on an epic.

The `staging` and `integration` branches are not precious, we should be able to delete them and recreate them at any time (after coordinating with team that no ones using what's up there currently).

The process is as follows:
- depending on which environment you want to target, tag your PR with `staging` or `integration`, merge your branch into the branch named after the environment (`staging` or `integration`), or rebase the environment branch onto your branch
- push it up to the github repo
- `cap staging deploy` or `cap integration deploy`
- test on the env, share with stakeholders, collaborate, etc.
- repeat as necessary, people may also merge their branches to `staging` or `integration`, this is good as we can see if there are issues. If you want to delete (or push with force) and recreate `staging` or `integration` branches, you should coordinate with the dev team via Teams


## Qualities of a good pull request

- put ticket number in PR name, e.g. "CT-123 improve roles ui"
- tests are green! ideally for each commit
- all conflicts are resolved, rebase off main before pushing
- small, try to make multiple pull requests over one large pull request when possible
- the description includes a link to the ticket in JIRA, e.g. "Closes [CT-123](https://climatesmart.atlassian.net/browse/CT-123)"
- the commits/messages tell a story that is logical and easy to follow
- If it is work-in-progress then label it with `WIP` or make it a draft PR
- Explain how to test it, perhaps with a titled subsection `To Test`
- Include any specific deployment tasks with checkboxes under a `To Deploy`

## How to conduct a review

- respond to PR requests as promptly as possible, check at beginning and end of work day so no review languishes longer than a day
- be respectful
- pursue technical excellence whenever possible
- if something doesn't make sense, ask a question
- check out the branch, reset/migrate db as necessary, and review UI and user flows in your local development environment
- follow any test instructions provided

## How to review UI

- check out the branch and exercise the UI in your local environment
- check that the UI is acceptable on both desktop and mobile viewport sizes
- review our design system and ensure we are not diverging from it without commenting that it's a diversion, and if possible reintegrate necessary expansions back into the design the system

## How to make a good commit message

- first line of commit is less than 50 characters, followed by a blank line, then no longer than 72
characters per line
- for more guidance see [this post](https://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html)