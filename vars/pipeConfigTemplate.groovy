def call() {
    def template = '''
# Default values for colinlabs.
# This is a YAML-formatted file.
# Declare variables to be passed into your templates.

### app config
pipeline:
  enabled: 'true'

app:
  name: demo
  build_label: maven
  build_cmd: mvn clean package -B -U -Dmaven.test.skip=true
  template: jar
  namespace: "default"
  url_uat: uat.colinlabs.com
  url_prod: prod.colinlabs.com
  url_path: "/"
  runEnv: {}
    # env: uat
  runArgs: []
  # - "-Dspring.profiles.active=local"

### helm charts config
replicaCount: 1
labels: {}
image:
  repository: nginx
  tag: alpine
  pullPolicy: Always
  pullSecret: harbor-devops
service:
  name: demo
  type: ClusterIP
  externalPort: 80
  internalPort: 8080
  protocol: TCP
ingress:
  enabled: true
  path: /
  hosts:
    - chart-example.local
  annotations:
    traefik.frontend.rule.type: PathPrefix
    kubernetes.io/ingress.class: traefik
  labels:
    realm: internal  # internal|external
resources: {}
# limits:
#  cpu: 100m
#  memory: 128Mi
# requests:
#  cpu: 100m
#  memory: 128Mi
  '''
  
    return template
}