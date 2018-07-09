#!/usr/bin/env groovy
def call(label, Closure body){
    if (label == "docker") {
        podTemplate(name: 'jenkins-slave-docker', label: 'docker', namespace: 'dev', idleMinutes: 30, containers: [
            containerTemplate(name: 'docker', image: 'docker:17.03-dind', alwaysPullImage: true,
                args: '--registry-mirror=https://2tx58yg1.mirror.aliyuncs.com -s=overlay2 --bip=172.30.1.1/24', privileged: true, ttyEnabled: true),
        ],
        volumes:[
            secretVolume(secretName: 'docker-config', mountPath: '/home/jenkins/.docker'),
        ]){
            node(label) {
                body()
            }
        }
    }
    if (label == "maven") {
        podTemplate(name: 'jenkins-slave-maven', label: 'maven', nodeSelector: 'k8s-infra-10-3', namespace: 'dev', idleMinutes: 30, containers: [
            containerTemplate(name: 'maven', image: 'maven:3.5-alpine', alwaysPullImage: true, command: 'cat', ttyEnabled: true),
            containerTemplate(name: 'docker', image: 'docker:17.03-dind', alwaysPullImage: true,
                args: '--registry-mirror=https://2tx58yg1.mirror.aliyuncs.com -s=overlay2 --bip=172.30.1.1/24', privileged: true, ttyEnabled: true),
            containerTemplate(name: 'k8s', image: 'hub.colinlabs.com/infra/jenkins-k8s', alwaysPullImage: true, command: 'cat', ttyEnabled: true), 
        ],
        volumes:[
            secretVolume(secretName: 'docker-config', mountPath: '/home/jenkins/.docker'),
            secretVolume(secretName: 'jenkins-maven-settings', mountPath: '/root/.m2'),
            secretVolume(secretName: 'k8s-context', mountPath: '/home/jenkins/.kube'),
            hostPathVolume(mountPath: '/maven/repository', hostPath: '/data/nfs/maven-repo'),
            // persistentVolumeClaim(claimName: 'maven-repo', mountPath: '/maven/repository'),
        ]){
            node(label) {
                body()
            }
        }
    }
    if (label == "k8s"){
        podTemplate(name: 'jenkins-slave-k8s', label: 'k8s', namespace: 'dev', idleMinutes: 30, containers: [
            containerTemplate(name: 'k8s', image: 'hub.colinlabs.com/infra/jenkins-k8s', alwaysPullImage: true, command: 'cat', ttyEnabled: true), 
        ],
        volumes:[
            secretVolume(secretName: 'k8s-context', mountPath: '/home/jenkins/.kube'),
        ]){
            node(label) {
                body()
            }
        }
    }
}
