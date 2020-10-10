#include "FaceTrack.h"

FaceTrack::FaceTrack(const char *model, const char *seeta) {
    Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(makePtr<CascadeClassifier>(model));
    Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(makePtr<CascadeClassifier>(model));

    DetectionBasedTracker::Parameters detectorParams;
    tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, detectorParams);
    faceAlignment = makePtr<seeta::FaceAlignment>(seeta);
}

void FaceTrack::startTracking() {
    tracker->run();
}

void FaceTrack::stopTracking() {
    tracker->stop();
}

/*
 * src是灰度图
 * */

void FaceTrack::detector(Mat src, vector<Rect2f> &rects) {
    vector<Rect> faces;
    tracker->process(src);
    tracker->getObjects(faces);

    if (faces.size()) {
        Rect face = faces[0];
        rects.push_back(Rect2f(face.x, face.y, face.width, face.height));

        //构造seeta引擎所需要的人脸数据
        seeta::ImageData imageData(src.cols, src.rows);
        seeta::FaceInfo faceInfo;
        seeta::Rect bbox;
        bbox.x = face.x;
        bbox.y = face.y;
        bbox.width = face.width;
        bbox.height = face.height;
        faceInfo.bbox = bbox;

        seeta::FacialLandmark points[5];//取人脸的五个特征点
        faceAlignment->PointDetectLandmarks(imageData, faceInfo, points);
        //把五个特征点也存入rects
        for (int i = 0; i < 5; ++i) {
            rects.push_back(Rect2f(points[i].x, points[i].y, 0, 0));
        }
    }
}
