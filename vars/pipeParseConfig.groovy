import io.colin.piper.Constants

def call(appname, gitinfo=""){
    def gconst = new Constants()
    def config 
    def consulKey = gconst.CONSUL_KV_PREFIX + "/" + appname

    config = pipeConsul.get(consulKey)

    if (config == "") {
        config = genConfig()
        pipeConsul.set(consulKey, config)

    } else if (env.UPDATE_CONFIG == "true") {
        config = updateConfig(config)
        pipeConsul.set(consulKey, config)
    }

    config = readYaml text: config
    config << ['dockerfile': pipeDockerfile(config.app.template)]

    if (gitinfo != "") {
        gitinfo.VERSION = gitinfo.GIT_BRANCH.replace("master", "latest").replace("origin/", "").replace("release/", "")
        config << gitinfo
    }
    
    return config
}

def genConfig() {
    def configTemplate = pipeConfigTemplate()
    def userInput = input message: 'Please write config', ok: 'OK',
                          parameters: [text(defaultValue: configTemplate, description: 'Config template', name: 'initconfig', trim: true)]
    return userInput
}

def updateConfig(config) {

    def userInput = input message: 'Please update config', ok: 'update',
                          parameters: [text(defaultValue: config, description: 'Update Config', name: 'updateconfig', trim: true)]
    return userInput
}