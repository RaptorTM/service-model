package info

import groovy.transform.CompileStatic

@CompileStatic
public class LandscapeInfo {
    String name
    String paasId
    String segmentId
    String mainRegistry

    static public LandscapeInfo makeLandscapeInfo(svc) {
        return new LandscapeInfo(
                name: svc['name'],
                paasId: svc['paasId'],
                segmentId: svc['segmentId'],
                mainRegistry: svc['mainRegistry'],
        )
    }

    static LandscapeInfo getLandscapeInfoByName(String name, ServiceModelConfig) {
        def svc = ServiceModelConfig['landscapes'].find { svc -> svc['name'] == name }
        return makeLandscapeInfo(svc)
    }

    static LandscapeInfo getLandscapeInfoBySegment(String segmentId, ServiceModelConfig) {
        def svc = ServiceModelConfig['landscapes'].find { svc -> svc['segmentId'] == segmentId }
        return makeLandscapeInfo(svc)
    }
}


