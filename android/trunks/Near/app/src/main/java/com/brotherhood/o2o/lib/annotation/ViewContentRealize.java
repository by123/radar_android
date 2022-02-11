package com.brotherhood.o2o.lib.annotation;


/*//AbstractLevelFragment子类添加
AbstractViewContent子类时 需要实现的方法*/
public interface ViewContentRealize {

	void refresh();
	void release();
	void resume();
}
