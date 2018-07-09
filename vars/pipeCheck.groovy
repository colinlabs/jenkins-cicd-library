def preCheck() {
    // scan multibranch skip job, only get parameters info
    if (env.DEPLOY_ENV == null) {
        currentBuild.result = 'ABORTED'
        error "Only scan pipeline branch, Skip build!!!"
    }
}

def configCheck(config) {
    // check branch whitelists, TAG: v1.0.0
    def branchList = ["master", "latest", "develop", "release"]
    def branch = config.GIT_BRANCH
    if (branchList.count{it.startsWith(branch)} || branch ==~ /release.*/) {
        echo "Match branch with [${branch}]"
    } else if (branch ==~ /v(\d+).(\d+).(\d+)/) {
        echo "Match tag with [${branch}]"
    } else {
        pipeDingTalkNotify.fail()

        currentBuild.result = 'ABORTED'
        error "[$branch] branch or tag  NOT MATCH with $branchList or like v1.0.0, Skip job!!! "
    }
}