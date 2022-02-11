package com.brotherhood.o2o.bean;

import java.util.List;

/**
 * 分页对象
 */
public class PageItem<T> {
	private int pageCount;
	private int pageNum;
	private List<T> content;

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

}
