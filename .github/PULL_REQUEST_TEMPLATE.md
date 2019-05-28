Thank you for submitting a contribution to Drillflow.

In order to streamline the review of the contribution please
ensure the following steps have been taken:

### For all changes:
- [ ] Is there a Github ticket associated with this PR? Is it referenced 
     in the commit message?

- [ ] Does your PR title start with DF-XXXX where XXXX is the waffle number you are trying to resolve? Pay particular attention to the hyphen "-" character.

- [ ] Has your PR been rebased against the latest commit within the target branch (typically dev)?

### For code changes:
- [ ] Have you ensured that the full suite of tests is executed via mvn clean install at the root Drillflow folder?
- [ ] Have you written or updated unit tests to verify your changes?
- [ ] Have you updated Soap UI
- [ ] Have you added documentation for any API related changes to the /docs folder

### For documentation related changes:
- [ ] Have you ensured that format looks appropriate for the output in which it is rendered?

### Note:
Please ensure that once the PR is submitted, you check travis-ci for build issues and submit an update to your PR as soon as possible.
