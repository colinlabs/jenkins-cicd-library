import io.colin.piper.Constants

def helm(config){
    def gconst = new Constants()
    
    def deployEnv = env.DEPLOY_ENV
    def kubeContextName = "${deployEnv}@context"
    def configName = "${config.app.name}.yaml"
    def deployArgs = []

    // deploy args
    deployArgs.add("app.runEnv.ENV=${deployEnv}")
    deployArgs.add("app.runEnv.GIT_COMMIT=${config.GIT_COMMIT}")
    deployArgs.add("image.tag=${config.VERSION}")

    if (deployEnv == "prod"){
        deployArgs.add("ingress.hosts[0]=${config.app.url_prod}")
        deployArgs.add("replicaCount=${config.replicaCount}")
    } else if (deployEnv == "uat") {
        deployArgs.add("ingress.hosts[0]=${config.app.url_uat}")
        deployArgs.add("replicaCount=1")
    }
    container("k8s") {
        def deployCmd
        def deployName = config.app.name
        def deployStatus
        // def deployNameNew
        // def deployNameOld

        ws("resource") {
            echo "Clone helm charts from gitlab..."
            git credentialsId: 'gitlab-ssh-key', changelog: false,
                url: 'git@code.colinlabs.com:container/jenkins-cicd.git', branch: 'develop'

            // if ( DEPLOY_TYPE == "blue-green") {
            //     deployArgs.add("ingress.enabled=false")
            //     deployArgs.add("service.type=NodePort")

            //     def version = pipeShell.debug("kubectl get deployment --context=$kubeContextName  -l app=${config.app.name} -o json|jq '.items[0].spec.template.metadata.labels.buildid'")
            //     if (version == "") {
            //         deployNameOld = config.app.name
            //     } else {
            //         deployNameOld = config.app.name + version
            //     }
            //     deployNameNew = config.app.name + "-" + env.BUILD_ID

            // } else if ( DEPLOY_TYPE == "rolling-update") {
            //     deployNameNew = config.app.name
            // }

            deployArgs = deployArgs.join(",").toString()
            deployCmd = "helm upgrade --install --kube-context=$kubeContextName --namespace ${config.app.namespace} -f $configName --set $deployArgs $deployName . "

            dir('resources/charts/colinlabs') {
                if (fileExists(configName)) {
                    sh "rm -rf $configName"
                }
                // write release config
                writeYaml file: configName, data: config

                echo "Start deploy project..."
                echo "command => $deployCmd"
                pipeApprove("ops")
                sh "$deployCmd"

                echo "Wating for deploy status"
                
                sleep(30)

                def readyCmd = """ kubectl get deployment --context=$kubeContextName --namespace ${config.app.namespace} $deployName -o json | jq '.status.conditions[] | select(.reason == "MinimumReplicasAvailable") | .status' | tr -d '"' |tr -d '\n' """
                
                // waiting ready status
                for (i = 1 ;i <= 60; i++) {
                    echo "retry [$i]"
                    deployStatus = pipeShell.nodebug(readyCmd)

                    if (deployStatus == "True") { break }
                    sleep(2)

                }
                if (deployStatus == "True") {
                    currentBuild.result = "SUCCESS"
                    echo "[SUCCESS]"
                    pipeDingTalkNotify.succeed()
                } else {                    
                    // rollback
                    sh "helm status $deployName --kube-context=${kubeContextName}"
                    echo "[FAILED]"
                    pipeDingTalkNotify.fail()
                    // rollback to preversion
                    echo "Start rollback..."
                    def rollversion = pipeShell.noDebug("helm history $deployName --max 1|tail -1|awk '{print \$1}'").toInteger() - 1
                    def rollCmd = "helm rollback ${deployName} ${rollversion} --wait --timeout 300 --recreate-pods"
                    
                    echo "Rollback to preversion [${rollversion}]"
                    sh "${rollCmd}"

                }

            }
        }
    }
}
