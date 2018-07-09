package io.colin.piper

class Constants {
    static final CONSUL_HOST      = 'http://172.17.10.1:32299'
    static final CONSUL_TOKEN     = 'consul'
    static final CONSUL_KV_PREFIX = '/jenkins'
    static final DINGTALK_URL     = 'https://oapi.dingtalk.com/robot/send?access_token='
    static final DINGTALK_TOKEN   = 'token'
    static final DEPLOY_ENV       = ["uat", "prod"].join('\n')
    static final DEPLOY_TYPE      = ["rolling-update"].join("\n")
    static final GITLAB_CONN      = 'Gitlab'
    
    // approve user split with ","
    static final APPROVE_USERS_TEST = "admin"
    static final APPROVE_USERS_OPS  = "admin"
}