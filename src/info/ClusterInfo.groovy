package info

import groovy.transform.CompileStatic

@CompileStatic
class ClusterInfo {
    String name
    String landscape
    String cloudApiUrl
    String paasId
    String platform

    ClusterInfo makeClusterInfo(svc) {
        return new ClusterInfo(
                name: svc['name'],
                landscape: svc['landscape'],
                cloudApiUrl: svc['cloudApiUrl'],
                paasId: svc['paasId'],
                platform: svc['platform'] ? svc['platform'] : 'ocp',
        )
    }

    ClusterInfo getClusterInfoByName(String name, ServiceModelConfig) {
        def svc = ServiceModelConfig['clusters'].find { svc -> svc['name'] == name }
        return makeClusterInfo(svc)
    }
}
