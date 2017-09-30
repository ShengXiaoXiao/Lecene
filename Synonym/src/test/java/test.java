
public class test {
    public static void main(String[] args) {
        Output.setTrieTree();
        System.out.println(Output.test("快，杯子是什么，急求", "告诉我什么是矿泉水"));
        System.out.println(Output.test("快，哈利波特是谁写的，急求", "告诉我哈利波特作者是谁"));
        System.out.println(Output.test("快，北大是什么，急求", "告诉我什么是北京大学"));
        System.out.println(Output.test("快，老妇是谁，急求", "告诉我谁是老奶奶"));
    }

}
