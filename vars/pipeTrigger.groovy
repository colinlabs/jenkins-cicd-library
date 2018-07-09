def call(name){
    def config = pipeParseConfig(name)
    def jenkinsfilename = "jenkinsfile/Jenkinsfile"
    def jenkinsfile = libraryResource(jenkinsfilename)

    if (config.pipeline.enabled) {
        pipePodTemplate(config.app.build_label){
            // global ENV with APP_NAME
            env.APP_NAME = name
            ws("workspace/${config.app.name}") {
                writeFile file: jenkinsfilename, text: jenkinsfile
                load(jenkinsfilename)
            }
        }
    } else {
        echo "Project pipeline disabled!! skip..."
    }
}
