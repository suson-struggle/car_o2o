package cn.wolfcode.car;

public class ValueTransfer {
    public static void main(String[] args) {
        User user = new User();
        user.setName("你好");
        change(user);
    }

    public static void change(User user) {
        user.setName("哈哈");
        System.out.println(user);
    }
}

class User {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}

