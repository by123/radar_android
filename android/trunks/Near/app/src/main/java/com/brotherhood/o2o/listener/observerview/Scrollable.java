package com.brotherhood.o2o.listener.observerview;

public interface Scrollable {

	void scrollVerticallyTo(float transY);

	int getCurrentScrollY();

	void setLastHeaderY(float lastHeaderY);

	Scroller needTrans(float distance, float headerTransY);

	public void setScrollCallBack(ScrollCallBack callBack);

	public void setHeaderHeight(float headerHeight);

}
