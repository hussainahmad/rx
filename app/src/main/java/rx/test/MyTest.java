package rx.test;

/**
 * Created by agoyal3 on 12/17/15.
 */
public class MyTest {
    private String name, url;
    private int num;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Name: " + this.name + ", num: " + this.num + ", url: " + this.url;
    }
}
