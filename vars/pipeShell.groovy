def nodebug(cmd) {
    def result = sh(returnStdout: true, script: "#!/bin/sh -e\n" + cmd)
    return result
}

def debug(cmd) {
    def result = sh(returnStdout: true, script: cmd)
    return result
}