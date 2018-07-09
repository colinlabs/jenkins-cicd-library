def maven(cmd){
    if (!env.STEP_BUILD.toBoolean()) { return }
    def command
    if (cmd != null) {
        command = cmd
    } else {
        command = "mvn clean package -B -U -Dmaven.test.skip=true"
    }
    container("maven") {
        sh command
    }
}

def docker(config, args){
    if (!env.STEP_BUILD.toBoolean()) { return }
    String repoURL     = config.image.repository
    String tagName     = config.VERSION
    String buildArgs   = (args != null) ? args : ""
    String dockerfileName = (config.dockerfileName != null) ? config.dockerfileName : "Dockerfile"

    if(!fileExists ('Dockerfile')) {
        writeFile file: 'Dockerfile', text: config.dockerfile
    }
    container("docker"){
        sh """
            [ -d ${env.APP_NAME} ] && cp -r ${env.APP_NAME}/target .

            docker build --pull ${buildArgs} -t ${repoURL}:${tagName} -f ${dockerfileName} .
            docker push ${repoURL}:${tagName}
            mv Dockerfile Dockerfile.old                
        """
    }
}

