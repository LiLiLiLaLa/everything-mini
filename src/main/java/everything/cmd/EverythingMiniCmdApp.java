package everything.cmd;



import everything.core.EverythingMiniManager.EverythingMiniManager;
import everything.core.model.Condition;
import everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

public class EverythingMiniCmdApp{
    private static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        welcome();
        //统一调度器
        EverythingMiniManager manager = EverythingMiniManager.getInstance();

        //交互式
        interactive(manager);
    }

    private static void interactive(EverythingMiniManager manager) {
        while (true) {
            System.out.print("everything >>");
            String input = scanner.nextLine();
            //优先处理search
            if (input.startsWith("search")) {
                //search name [file_type]  search [“java 课件”] DOC
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!"search".equals(values[0])) {
                        help();
                        continue;
                    }
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values[1].length() >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager, condition);
                    continue;
                } else {
                    help();
                    continue;
                }
            }
            switch (input) {
                case "help":
                    help();
                    break;
                case "index":
                    index(manager);
                    break;
                case "quit":
                    quit();
                    break;
                default:
                    help();
            }
        }
    }


    private static void search(EverythingMiniManager manager, Condition condition) {
        System.out.println("检索功能");
        manager.search(condition);
        List<Thing> thingList = manager.search(condition);
        for(Thing thing : thingList){
            System.out.println(thing.getPath());
        }
    }


    private static void index(final EverythingMiniManager manager) {
        //统一调度器器中的index
        new Thread(() -> manager.buildIndex()).start();
    }

    private static void welcome() {
        System.out.println("welcome to use,IntelligentEverything");
    }

    private static void help() {
        System.out.println("命令列表:");
        System.out.println("退出: quit");
        System.out.println("帮助: help");
        System.out.println("索引: index");
        System.out.println("历史指令: history");
        System.out.println("搜索: search <name> [<file-Type> img | doc | bin | archieve | other]");
        System.out.println("搜索文件特殊格式: search <\"name contain spacing \"> [<file-Type> img | doc | bin | archieve | other]");
    }

    private static void quit() {
        System.out.println("再见");
        System.exit(0);
    }

}
