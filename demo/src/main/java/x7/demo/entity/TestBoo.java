package x7.demo.entity;


import io.xream.x7.base.web.Viewable;

public enum TestBoo implements Viewable<String> {


    TEST("0000"){
        @Override
        public String getView() {
            return "测试";
        }
    },
    BOO("1111"){
        @Override
        public String getView() {
            return "不要的";
        }
    };

    private String id;
    public String getId() {
        return id;
    }

    private TestBoo(String id){
        this.id = id;
    }

    public TestBoo get(String id) {
        for (TestBoo noticeType : values()) {
            if (noticeType.getId().equals(id))
                return noticeType;
        }
        return TEST;
    }
}
