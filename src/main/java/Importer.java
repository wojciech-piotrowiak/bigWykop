import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Importer {
    public static void main(String... input) throws IOException {

        String appKey = input[0];
        String start = input[1];
        String end = input[2];
        String path = input[3];
        //index,comments
        String kind = input[4];

        IntStream.rangeClosed(Integer.valueOf(start), Integer.valueOf(end)).forEach(value -> {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String index = String.valueOf(value);
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String query = String.format("http://a.wykop.pl/link/%s/%s/appkey,%s",kind, index, appKey);
            HttpGet method = new HttpGet(query);
            try (CloseableHttpResponse execute = httpclient.execute(method)) {
                System.out.println(execute.getStatusLine().getStatusCode());
                saveFile(execute.getEntity().getContent(),path, index);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    public static void saveFile(InputStream input,String path, String index) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            String result = buffer.lines().collect(Collectors.joining("\n"));
            if (!result.contains("\"code\":5")&&!result.contains("\"code\":41")) {
                String fileName = String.format("%s.txt", index);
                Files.write(Paths.get(path,fileName), result.getBytes());
            }
        }
    }
}
