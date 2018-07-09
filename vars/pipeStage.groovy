def call(name, Closure body) {
    try {
        gitlabCommitStatus(name) {
            stage(name) {
                body()
            }
        }
    } catch (Exception e) {
        currentBuild.result = "FAILURE"
        pipeDingTalkNotify.fail(e)
        error(e)
    }   
}