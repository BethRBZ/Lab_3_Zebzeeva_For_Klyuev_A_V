import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Main {

    public static void main(String[] args) {
        String path = "C:\\";
        String fileName = "ёжик.txt";
        ForkJoinPool pool = new ForkJoinPool();
        List<File> result = pool.invoke(new SearchTask(new File(path), fileName));
        for (File file : result) {
            System.out.println("Поиск удался! Файл находится в " + file.getAbsolutePath());
        }
        if (result.isEmpty())
            System.out.println("Файл не найден");
    }

    private static class SearchTask extends RecursiveTask<List<File>> {
        private final File directory;
        private final String fileName;

        public SearchTask(File directory, String fileName) {
            this.directory = directory;
            this.fileName = fileName;
        }

        @Override
        protected List<File> compute() {
            List<SearchTask> subTasks = new ArrayList<>();
            List<File> result = new ArrayList<>();
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        SearchTask task = new SearchTask(file, fileName);
                        task.fork();
                        subTasks.add(task);
                    } else {
                        if (file.getName().equals(fileName)) {
                            result.add(file);
                        }
                    }
                }
            }
            for (SearchTask task : subTasks) {
                result.addAll(task.join());
            }
            return result;
        }
    }
}
