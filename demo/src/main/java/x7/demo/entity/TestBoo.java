package x7.demo.entity;


import io.xream.x7.base.web.Viewable;
import x7.config.EnumCodeable;

public enum TestBoo implements EnumCodeable, Viewable<String> {


    TEST{
        @Override
        public String getCode() {
            return "T";
        }

        @Override
        public String getView() {
            return "测试";
        }
    },
    BOO{
        @Override
        public String getCode() {
            return "B";
        }
        @Override
        public String getView() {
            return "不要的";
        }
    },

    HLL{
        @Override
        public String getCode() {
            return "H";
        }
        @Override
        public String getView() {
            return "好好的";
        }
    };


}
