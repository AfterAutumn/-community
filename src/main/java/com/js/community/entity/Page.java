package com.js.community.entity;

public class Page {
    //当前页码
    private int current=1;
    //每页显示数量,默认为10
    private int limit=10;
    //帖子总数量
    private int rows;
    //跳转路径
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        //判断当前页数是否合法
        if(current>0) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>0) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //数据库中offset起始行数计算
    public int getOffset()
    {
        return (current-1)*limit;
    }

    //计算一共有多少页
    public int pageCount()
    {
        if(rows%limit==0) {
            return rows / limit;
        }else {
            return rows/limit+1;
        }
    }

    //获取起始页码
    public int fromPage()
    {
        //如果cur比1大返回cur否则返回1
        int cur=current-2;
        return cur>1?cur:1;
    }

    //获取结束页码
    public int endPage()
    {
        int cur=current+2;
        int totalPage=pageCount();
        return cur>totalPage?totalPage:cur;
    }
}
