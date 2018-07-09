def call(args){
    if (args == "jar") {
        return '''
        FROM hub.colinlabs.com/infra/jar:onbuild
        '''
    } else if (args == "war") {
        return '''
        FROM hub.colinlabs.com/infra/war:onbuild
        '''      
    } else if (args == "oldwar") {
        return '''
        FROM hub.colinlabs.com/infra/war
        RUN sed -i '2 i\\cp /data/*$ENV.war /app/ROOT.war' /usr/local/tomcat/bin/catalina.sh
        ADD target/*prod.war target/*uat.war /data/
        '''
    }
}