import io.colin.piper.Constants

def call(users){
    def gconst = new Constants()

    def choiceList = ["Agree", "Deny"]
    def userInput

    if (users == "test") {
        users = gconst.APPROVE_USERS_TEST
        if (env.GLOBAL_APPROVE_USERS_TEST != null) {
            users = users + "," + env.GLOBAL_APPROVE_USERS_TEST
        }
    } else if (users == "ops") {
        users = gconst.APPROVE_USERS_OPS
        if (env.GLOBAL_APPROVE_USERS_OPS != null) {
            users = users + "," + env.GLOBAL_APPROVE_USERS_OPS
        }
    }

    timeout(time: 1, unit: 'DAYS') {
        userInput = input message: '是否允许上线', submitter: users, ok: "确认",
                          parameters: [choice(choices: ['Agree', 'Deny'], description: '', name: 'approve')]
    }
    if (userInput != "Agree") {
        def errmsg = "Approve refused"
        pipeDingTalkNotify.fail(errmsg)
        currentBuild.result = 'ABORTED'
        error(errmsg)
    }
}
