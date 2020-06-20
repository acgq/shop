package com.github.shop.entity;

import java.util.List;

public class PageResponse<T> {
    private int pageNum;
    private int pageSize;
    private int totalPage;
    private List<T> data;
    
    public static <T> PageResponse of(int pageSize,
                                      int pageNum,
                                      int totalPage,
                                      List<T> data) {
        PageResponse<T> response = new PageResponse<>();
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        response.setTotalPage(totalPage);
        response.setData(data);
        return response;
    }
    
    public PageResponse() {
    }
    
    public int getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public int getTotalPage() {
        return totalPage;
    }
    
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
    
    public List<T> getData() {
        return data;
    }
    
    public void setData(List<T> data) {
        this.data = data;
    }
}
