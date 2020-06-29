
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class HandleDate {

    private static Jedis jedis;
    static {
        jedis = new Jedis("192.168.2.168", 6379);
        jedis.auth("hundsun@1");
    }


    public static void generateSsq() {
        Set<Integer> blues = new HashSet<>();
        Set<Integer> reds = new HashSet<>();

        Random rand = new Random();

        for (int i = 1; i <= 33; i++) {
            int randNumber = rand.nextInt(33) + 1; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数\
            blues.add(randNumber);
            if (blues.size() == 6) {
                break;
            }
        }

        for (int i = 1; i <= 16; i++) {
            int randNumber = rand.nextInt(16) + 1;
            reds.add(randNumber);
            if (reds.size() == 1) {
                break;
            }
        }
        List list = blues.stream().collect(Collectors.toList());
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        list.stream().forEach(number -> {
            sb.append(org.apache.commons.lang3.StringUtils.leftPad(number + "", 2, "0"));
        });
        sb.append(reds.toArray()[0]);

        jedis.select(1);
        if (!jedis.exists(sb.toString()) && reds.contains(11)) {
            System.out.println("blues:" + list);
            System.out.println("red:" + reds);
        } else {
            generateSsq();
        }

    }

    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            jedis.select(1);
            Pipeline pipeline = jedis.pipelined();
            pipeline.multi();
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result.append(System.lineSeparator() + s);
                String key = StringUtils.trimAllWhitespace(s);
                pipeline.set(key, key);
            }
            pipeline.exec();
            pipeline.sync();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        File file = new File("ssq.txt");
        txt2String(file);
        generateSsq();
    }
}
