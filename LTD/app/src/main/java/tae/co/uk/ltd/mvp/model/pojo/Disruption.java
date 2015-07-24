package tae.co.uk.ltd.mvp.model.pojo;

import java.io.Serializable;

public class Disruption implements Serializable {

    private static final long serialVersionUID = 1L;

    public String mId, mStatus, mSeverity, mLevelOfInterest, mCategory, mSubCategory, mStartTime;
    public String mLocation, mCorridor, mComments, mCurrentUpdate, mRemarkTime, mLastModifiedTime;
    public int mSeverityBg;
    public Point mPoint;

    public void setPoint(Point point) {
        mPoint = point;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public void setSeverity(String severity) {
        mSeverity = severity;
    }

    public void setSeverityBG(int severityBG) {
        mSeverityBg = severityBG;
    }

    public void setLevelOfInterest(String levelOfInterest) {
        mLevelOfInterest = levelOfInterest;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public void setSubCategory(String subCategory) {
        mSubCategory = subCategory;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public void setCorridor(String corridor) {
        mCorridor = corridor;
    }

    public void setComments(String comments) {
        mComments = comments;
    }

    public void setCurrentUpdate(String currentUpdate) {
        mCurrentUpdate = currentUpdate;
    }

    public void setRemarkTime(String remarkTime) {
        mRemarkTime = remarkTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        mLastModifiedTime = lastModifiedTime;
    }
}
