import io.colin.piper.Constants
import io.colin.piper.Utils

def get(ckey) {
    def utils = new Utils()
    def gconst = new Constants()
    static final consulKVURL = gconst.CONSUL_HOST + "/v1/kv" + ckey
    def response = httpRequest consoleLogResponseBody: false,
                                validResponseCodes: '200,404',
                                quiet: false,
                                httpMode: 'GET',
                                url: consulKVURL,
                                customHeaders: [[maskValue: true, name: 'X-Consul-Token', value: gconst.CONSUL_TOKEN]]
    if (response.status == 200) {
        def respJson = readJSON text: response.content
        def value = utils.decodeBase64(respJson[0].Value)
        return value  
    } else if (response.status == 400) {
        return ""
    } else {
        error "Unknown resturn status code"
    }
}

def set(ckey,cvalue) {
    def gconst = new Constants()
    static final consulKVURL = Constants.CONSUL_HOST + "/v1/kv" + ckey    

    def response = httpRequest consoleLogResponseBody: false,
                   validResponseCodes: '200',
                   contentType: 'APPLICATION_JSON_UTF8',
                   quiet: false,
                   httpMode: 'PUT',
                   url: consulKVURL,
                   requestBody: cvalue,
                   customHeaders: [[maskValue: true, name: 'X-Consul-Token', value: gconst.CONSUL_TOKEN]]
    if ( response.status == 200) {
        return true
    } else {
        error "Unknown resturn status code"
    }
}