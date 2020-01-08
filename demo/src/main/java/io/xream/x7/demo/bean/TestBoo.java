package io.xream.x7.demo.bean;

import io.xream.x7.common.web.Viewable;

public enum TestBoo implements Viewable<String> {


    TEST{
        @Override
        public String getView() {
            return "测试";
        }
    },
    BOO{
        @Override
        public String getView() {
            return "不要的";
        }
    };


}
