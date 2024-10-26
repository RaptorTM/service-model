package info

class LandscapeInfo {
    String name
    String paasId
    String segmentId
    String mainRegistry

    LandscapeInfo makeLandscapeInfo(svc) {
        return new LandscapeInfo(
                name: svc['name'],
                paasId: svc['paasId'],
                segmentId: svc['segmentId'],
                mainRegistry: svc['mainRegistry'],
        )
    }

    LandscapeInfo getLandscapeInfoByName(String name) {
        def svc = ServiceModelConfig['landscapes'].find { svc -> svc['name'] == name }
        return makeLandscapeInfo(svc)
    }

    LandscapeInfo getLandscapeInfoBySegment(String segmentId) {
        def svc = ServiceModelConfig['landscapes'].find { svc -> svc['segmentId'] == segmentId }
        return makeLandscapeInfo(svc)
    }
}


